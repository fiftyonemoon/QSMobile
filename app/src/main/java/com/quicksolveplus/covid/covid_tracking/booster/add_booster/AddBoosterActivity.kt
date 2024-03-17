package com.quicksolveplus.covid.covid_tracking.booster.add_booster

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.quicksolveplus.api.Api
import com.quicksolveplus.api.RequestParameters
import com.quicksolveplus.api.ResponseStatus
import com.quicksolveplus.covid.covid_tracking.adapter.SupportedDocAdapter
import com.quicksolveplus.covid.covid_tracking.booster.add_booster.viewmodel.AddBoosterViewModel
import com.quicksolveplus.covid.covid_tracking.models.CovidItem
import com.quicksolveplus.dialogs.QSAlert
import com.quicksolveplus.dialogs.QSImagePicker
import com.quicksolveplus.modifiers.Actifiers.cameraIntent
import com.quicksolveplus.modifiers.Actifiers.imageIntent
import com.quicksolveplus.modifiers.Actifiers.registerActivityResultLauncher
import com.quicksolveplus.modifiers.Actifiers.saveBitmapToCached
import com.quicksolveplus.qspmobile.R
import com.quicksolveplus.qspmobile.databinding.ActivityAddBoosterBinding
import com.quicksolveplus.utils.*
import okhttp3.ResponseBody
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.InputStream


class AddBoosterActivity : AppCompatActivity() {

    lateinit var binding: ActivityAddBoosterBinding
    private var supportDocumentAdapter: SupportedDocAdapter? = null
    private var supportDocList: ArrayList<String> = arrayListOf()
    private val viewModel: AddBoosterViewModel by viewModels()

    private var base64Image: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBoosterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()

        setObservers()
    }

    private fun initUI() {
        binding.apply {
            toolBar.tvTitle.text = getString(R.string.str_add_booster)
            toolBar.ivSave.visibility = View.VISIBLE
            ivAddDoc.setOnClickListener(onClickListener)
            ivSupportDocument.setOnClickListener(onClickListener)
            tvDoseDate.setOnClickListener(onClickListener)
            toolBar.ivBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
            toolBar.ivSave.setOnClickListener {
                if (isValid())
                    doRequestForSubmitCovidComplianceForm()
            }
        }

    }


    private fun setObservers() {
        viewModel.responseStatus().observe(this) {
            when (it) {
                is ResponseStatus.Running -> {
                    showQSProgress(this)
                }
                is ResponseStatus.Success -> {
                    Log.e("TAG", "setObservers: $it")
                    proceedResponse(it)
                    dismissQSProgress()
                }
                is ResponseStatus.Failed -> {
                    toast(this, it.msg)
                    dismissQSProgress()
                }
                else -> {
                    toast(this, "Something goes wrong")
                    dismissQSProgress()
                }
            }
        }
    }


    private fun proceedResponse(success: ResponseStatus.Success) {
        when (success.apiName) {
            Api.uploadImageFile -> {
                if (success.data is String) {
                    binding.clSupportDocument.isVisible = false
                    binding.rvSupportDocument.isVisible = true
                    binding.ivAddDoc.isVisible = true
                    supportDocList.add(success.data)
                    setSupportDocumentAdapter()
                }
            }
            Api.getImageFile -> {
                if (success.data is ResponseBody) {
                    if (success.other is Pair<*, *>) {
                        val array = success.data.bytes()
                        val bitmap = BitmapFactory.decodeByteArray(array, 0, array.size)
                        val position = success.other.first as Int
                        val client = success.other.second as String
                        saveBitmapToCached(client, bitmap)
                        supportDocumentAdapter?.notifyItemChanged(position)
                        QSAlert(
                            context = this,
                            title = null,
                            message = getString(R.string.str_upload_successful),
                            positiveButtonText = getString(R.string.str_ok),
                            cancelable = false
                        ).show()
                    }
                }
            }
            Api.deleteImageFile -> {
                if (success.data is String) {
                    if (supportDocList.isEmpty() || supportDocList.size == 0) {
                        binding.clSupportDocument.isVisible = true
                        binding.rvSupportDocument.isVisible = false
                        binding.ivAddDoc.isVisible = false
                    }
                }
            }
        }
    }


    private fun getImageUploadData() {
        Preferences.instance?.user?.run {
            val body = RequestParameters.forUploadImageFile(uID, base64Image!!, 10)
            viewModel.uploadImageFile(body = body)
        }
    }

    private fun getImageDeleteData(position: Int) {
        Preferences.instance?.user?.run {
            val body = RequestParameters.forDeleteImageFile(supportDocList[position], 10)
            viewModel.deleteImageFile(body = body)
        }
    }


    private fun doRequestForSubmitCovidComplianceForm() {

    }

    private var onClickListener = View.OnClickListener {
        when (it.id) {
            R.id.ivAddDoc -> {
                permissionForCameraAndStorage()
            }
            R.id.ivSupportDocument -> {
                permissionForCameraAndStorage()
            }
            R.id.tvDoseDate -> {
                openDatePickerDialog(this, binding.tvDoseDate, binding.tvDoseDate.text.toString())
            }
        }
    }

    private fun permissionForCameraAndStorage() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                TedPermission.create()
                    .setPermissionListener(onPermissionListener)
                    .setRationaleMessage(R.string.camera_message)
                    .setDeniedMessage(R.string.denied_message)
                    .setGotoSettingButtonText(R.string.str_ok)
                    .setPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_MEDIA_IMAGES
                    )
                    .check()
            } else {
                TedPermission.create()
                    .setPermissionListener(onPermissionListener)
                    .setRationaleMessage(R.string.camera_message)
                    .setDeniedMessage(R.string.denied_message)
                    .setGotoSettingButtonText(R.string.str_ok)
                    .setPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                    .check()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private var onPermissionListener = object : PermissionListener {
        override fun onPermissionGranted() {
            openQSImagePicker()
        }

        override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
            permissionForCameraAndStorage()
        }
    }


    private fun openQSImagePicker() {
        QSImagePicker(this) { isCamera ->
            if (isCamera) {
                cameraLauncher.launch(cameraIntent)
            } else {
                galleryLauncher.launch(imageIntent)
            }
        }
    }

    private val cameraLauncher = registerActivityResultLauncher {
        if (it.resultCode == RESULT_OK) {

            val photo = it.data!!.extras?.get("data") as Bitmap
            val selectedImage: Uri?
            if (it.data != null) {
                selectedImage = getImageUri(this, photo)
                try {
//                    supportDocList.add(CovidItem(SupportingImageNames =  ))
                    binding.clSupportDocument.isVisible = false
                    binding.rvSupportDocument.isVisible = true
                    binding.ivAddDoc.isVisible = true
                    setSupportDocumentAdapter()
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
    }


    private fun isValid(): Boolean {
        if (binding.tvDoseDate.text.toString().trim().isEmpty()) {
            QSAlert(
                context = this,
                title = null,
                message = getString(R.string.str_please_select_dose_date_first),
                positiveButtonText = getString(R.string.str_ok),
                cancelable = false
            ).show()
            return false
        }
        if (supportDocList.isEmpty()) {
            QSAlert(
                context = this,
                title = null,
                message = getString(R.string.str_you_must_upload_supporting_documentation),
                positiveButtonText = getString(R.string.str_ok),
                cancelable = false
            ).show()
            return false
        }
        return true
    }

    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    private val galleryLauncher = registerActivityResultLauncher {
        if (it.resultCode == RESULT_OK) {
            val selectedImage: Uri?
            if (it.data != null) {
                selectedImage = it.data!!.data
                val inputStream: InputStream?
                try {
                    inputStream = contentResolver.openInputStream(selectedImage!!)
                    val selectedImg = BitmapFactory.decodeStream(inputStream)
                    val selectedImagePath = getPath(this, selectedImage)
                    Log.e("TAG", "selectedImage : $selectedImagePath")
                    // todo Api Call
                    base64Image = fileConvertToBase64(selectedImagePath)
                    getImageUploadData()
                    uploadImageAPI(selectedImagePath)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun uploadImageAPI(path: String?) {


    }


    private fun setSupportDocumentAdapter() {
        binding.apply {
            supportDocumentAdapter =
                SupportedDocAdapter(
                    this@AddBoosterActivity,
                    supportDocList,
                    viewModel
                ) { position ->
                    removeDocFromList(position)
                }
            binding.rvSupportDocument.adapter = supportDocumentAdapter
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun removeDocFromList(position: Int) {
        getImageDeleteData(position)
    }


}