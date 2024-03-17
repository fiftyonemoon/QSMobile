package com.quicksolveplus.covid.covid_result.test_result

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.Window
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.quicksolveplus.api.Api
import com.quicksolveplus.api.RequestParameters
import com.quicksolveplus.api.ResponseStatus
import com.quicksolveplus.covid.covid_tracking.adapter.SupportedDocAdapter
import com.quicksolveplus.covid.covid_tracking.models.CovidItem
import com.quicksolveplus.covid.covid_result.test_result.adapter.OptionListAdapter
import com.quicksolveplus.covid.covid_result.test_result.models.Options
import com.quicksolveplus.covid.covid_result.test_result.models.OptionsItem
import com.quicksolveplus.covid.covid_result.test_result.viewmodel.OptionViewModel
import com.quicksolveplus.dialogs.QSAlert
import com.quicksolveplus.dialogs.QSImagePicker
import com.quicksolveplus.modifiers.Actifiers.cameraIntent
import com.quicksolveplus.modifiers.Actifiers.imageIntent
import com.quicksolveplus.modifiers.Actifiers.registerActivityResultLauncher
import com.quicksolveplus.qspmobile.R
import com.quicksolveplus.qspmobile.databinding.ActivityTestingResultDetailsBinding
import com.quicksolveplus.utils.*
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.InputStream

class TestingResultDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTestingResultDetailsBinding
    private var supportDocumentAdapter: SupportedDocAdapter? = null
    private var supportDocList: ArrayList<CovidItem> = arrayListOf()
    private var optionList: ArrayList<Options> = arrayListOf()
    private var optionItemList: ArrayList<OptionsItem> = arrayListOf()
    private val viewModel: OptionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestingResultDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()

        setObservers()
        getOptionsData()

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
            Api.getOptionList -> {
                if (success.data is Options) {
                    for (i in 0 until success.data.size) {
                        optionList.add(success.data)
                    }
                }
            }

        }
    }


    private fun getOptionsData() {
        Preferences.instance?.user?.run {
            val body = RequestParameters.forGetOptionsList(171)
            viewModel.forGetOptionList(body = body)
        }
    }

    private fun submitTestResult() {


    }


    private fun initUI() {
        binding.apply {
            toolBar.tvTitle.setText(R.string.str_test_result_details)
            toolBar.ivSave.isVisible = true
            toolBar.ivBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
            ivSupportDocument.setOnClickListener(onClickListener)
            tvDate.setOnClickListener(onClickListener)
            tvResult.setOnClickListener(onClickListener)
        }
    }

    override fun onBackPressed() {
        saveConfirmationDialog()
    }


    private fun saveConfirmationDialog() {
        toast(this, "Clicked")
        QSAlert(
            context = this,
            title = null,
            message = getString(R.string.str_save_your_changes),
            positiveButtonText = getString(R.string.action_yes),
            negativeButtonText = getString(R.string.str_cancel),
            cancelable = false
        ) { isPositive ->
            if (isPositive) {
                doRequestForSubmitTestingResult()
            }
        }.show()

    }

    private fun doRequestForSubmitTestingResult() {


    }


    private var onClickListener = View.OnClickListener {
        when (it.id) {
            R.id.ivSupportDocument -> {
                permissionForCameraAndStorage()
            }
            R.id.tvDate -> {
                openDatePickerDialog(this, binding.tvDate, binding.tvDate.text.toString())
            }
            R.id.tvResult -> {
                testingResultDialog()
            }
        }
    }

    private fun testingResultDialog() {
        val dialog = Dialog(this, R.style.AlertDialogTheme)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_testing_result)

        val rvResult = dialog.findViewById<RecyclerView>(R.id.rvResult)
        rvResult.setHasFixedSize(false)
        rvResult.layoutManager = GridLayoutManager(this, 1)
        rvResult.isNestedScrollingEnabled = false

        for (i in 0 until optionList.size) {
            optionItemList.add(optionList[i][i])
        }

        val optionListAdapter = OptionListAdapter(this, optionList) { position ->
            binding.tvResult.text = optionItemList[position].Text
            if (optionItemList[position].Text == "Did Not Work or Test" || optionItemList[position].Text == "Worked but Did Not Test" || optionItemList[position].Text == "Worked Remotely - Do Not Need to Test") {
                binding.apply {
                    tvDate.text = ""
                    supportDocList.clear()
                    clDate.visibility = View.GONE
                    clSupportDocument.visibility = View.GONE
                }
            } else {
                binding.apply {
                    clDate.visibility = View.VISIBLE
                    if (supportDocList.isEmpty()) {
                        clSupportDocument.visibility = View.VISIBLE
                    } else {
                        ivAddDoc.visibility = View.VISIBLE
                        rvSupportDocument.visibility = View.VISIBLE
                    }
                }
            }
            dialog.dismiss()
        }

        rvResult.adapter = optionListAdapter
        dialog.show()
    }


    private fun permissionForCameraAndStorage() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                TedPermission.create().setPermissionListener(onPermissionListener)
                    .setRationaleMessage(R.string.camera_message)
                    .setDeniedMessage(R.string.denied_message)
                    .setGotoSettingButtonText(R.string.str_ok).setPermissions(
                        Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES
                    ).check()
            } else {
                TedPermission.create().setPermissionListener(onPermissionListener)
                    .setRationaleMessage(R.string.camera_message)
                    .setDeniedMessage(R.string.denied_message)
                    .setGotoSettingButtonText(R.string.str_ok).setPermissions(
                        Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE
                    ).check()
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
                val inputStream: InputStream?
                try {
                    inputStream = contentResolver.openInputStream(selectedImage!!)
                    val selectedImg = BitmapFactory.decodeStream(inputStream)
//                    supportDocList.add(SupportDocModel(selectedImg))
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
//                    supportDocList.add(SupportDocModel(selectedImg))
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


    private fun setSupportDocumentAdapter() {
//        binding.apply {
//            supportDocumentAdapter = SupportedDocAdapter(
//                this@TestingResultDetailsActivity,
//                supportDocList, vi
//            ) { position -> removeDocFromList(position) }
//            binding.rvSupportDocument.adapter = supportDocumentAdapter
//        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun removeDocFromList(position: Int) {
        supportDocList.removeAt(position)
        supportDocumentAdapter!!.notifyDataSetChanged()
        if (supportDocList.isEmpty()) {
            binding.clSupportDocument.isVisible = true
            binding.rvSupportDocument.isVisible = false
            binding.ivAddDoc.isVisible = false
        }
    }


}