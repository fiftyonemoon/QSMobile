package com.quicksolveplus.covid.covid_tracking.vaccination

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.quicksolveplus.api.Api
import com.quicksolveplus.api.ResponseStatus
import com.quicksolveplus.covid.covid_tracking.CovidTrackingActivity
import com.quicksolveplus.covid.covid_tracking.models.CovidItem
import com.quicksolveplus.covid.covid_tracking.vaccination.adapter.VaccinationSupportDocAdapter
import com.quicksolveplus.covid.covid_tracking.viewmodel.CovidViewModel
import com.quicksolveplus.dialogs.QSImagePicker
import com.quicksolveplus.dialogs.QSImageView
import com.quicksolveplus.modifiers.Actifiers.cameraIntent
import com.quicksolveplus.modifiers.Actifiers.imageIntent

import com.quicksolveplus.modifiers.Actifiers.registerActivityResultLauncher
import com.quicksolveplus.modifiers.Actifiers.saveBitmapToCached
import com.quicksolveplus.qspmobile.R
import com.quicksolveplus.qspmobile.databinding.FragmentVaccinationBinding
import com.quicksolveplus.utils.*
import okhttp3.ResponseBody
import java.io.FileNotFoundException
import java.io.InputStream


class VaccinationFragment : Fragment() {

    lateinit var binding: FragmentVaccinationBinding
    private var vaccinationSupportDocAdapter: VaccinationSupportDocAdapter? = null
    private var supportDocList: ArrayList<String> = arrayListOf()
    private var vaccinationList: ArrayList<CovidItem> = arrayListOf()
    private val viewModel by lazy {
        ViewModelProvider(this)[CovidViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentVaccinationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        setObservers()
    }


    private fun initUI() {

        vaccinationList = CovidTrackingActivity.vaccinationList
        supportDocList = vaccinationList[0].SupportingImageNames

        setSupportDocumentAdapter()
        binding.apply {
            val dose1Date = QSCalendar.formatDate(
                vaccinationList[0].Dose1Date,
                QSCalendar.DateFormats.yyyyMMddTHHmmss.label,
                QSCalendar.DateFormats.MMDDYYYY.label
            )
            val dose2Date = QSCalendar.formatDate(
                vaccinationList[0].Dose2Date,
                QSCalendar.DateFormats.yyyyMMddTHHmmss.label,
                QSCalendar.DateFormats.MMDDYYYY.label
            )

            tvType.text = vaccinationList[0].ComplianceType
            tvDoseOneDate.text = dose1Date

            if (vaccinationList[0].Dose2Date == null) {
                clDoseTwoDates.visibility = View.GONE
            } else {
                tvDoseTwoDate.text = dose2Date
            }

            ivAddDoc.setOnClickListener(onClickListener)
        }

    }

    private fun setObservers() {
        viewModel.responseStatus().observe(requireActivity()) {
            when (it) {
                is ResponseStatus.Running -> {
                    if (it.apiName != Api.getImageFile) {
                        showQSProgress(requireActivity())
                    }
                }
                is ResponseStatus.Success -> {
                    proceedResponse(it)
                    dismissQSProgress()
                }
                is ResponseStatus.Failed -> {
                    toast(requireActivity(), it.msg)
                    dismissQSProgress()
                }
                else -> {
                    toast(requireActivity(), "Something goes wrong")
                    dismissQSProgress()
                }
            }
        }
    }

    private fun proceedResponse(success: ResponseStatus.Success) {
        when (success.apiName) {
            Api.getImageFile -> {
                if (success.data is ResponseBody) {
                    if (success.other is Pair<*, *>) {
                        val array = success.data.bytes()
                        val bitmap = BitmapFactory.decodeByteArray(array, 0, array.size)
                        val position = success.other.first as Int
                        val client = success.other.second as String
                        requireActivity().saveBitmapToCached(client, bitmap)
                        vaccinationSupportDocAdapter?.notifyItemChanged(position)
                    }
                }
            }
        }
    }


    private fun setSupportDocumentAdapter() {
        binding.apply {
            vaccinationSupportDocAdapter = VaccinationSupportDocAdapter(
                requireContext(), supportDocList, viewModel
            ) { position ->
                val intent = Intent(requireContext(), QSImageView::class.java)
                intent.putExtra(Constants.covidVaccine, supportDocList[position])
                startActivity(intent)
            }
            binding.rvSupportDocument.adapter = vaccinationSupportDocAdapter
        }
    }


    private var onClickListener = View.OnClickListener {
        when (it.id) {
            R.id.ivAddDoc -> {
                permissionForCameraAndStorage()
            }
        }
    }


    private fun permissionForCameraAndStorage() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                TedPermission.create().setPermissionListener(permissionListener)
                    .setRationaleMessage(R.string.camera_message)
                    .setDeniedMessage(R.string.denied_message)
                    .setGotoSettingButtonText(R.string.str_ok).setPermissions(
                        Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES
                    ).check()
            } else {
                TedPermission.create().setPermissionListener(permissionListener)
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

    private var permissionListener = object : PermissionListener {
        override fun onPermissionGranted() {
            openQSImagePicker()
        }

        override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
            permissionForCameraAndStorage()
        }

    }


    private fun openQSImagePicker() {
        QSImagePicker(requireContext()) { isCamera ->
            if (isCamera) {
                cameraLauncher.launch(requireActivity().cameraIntent)
            } else {
                galleryLauncher.launch(requireActivity().imageIntent)
            }
        }
    }


    private val cameraLauncher = registerActivityResultLauncher {
        if (it.resultCode == AppCompatActivity.RESULT_OK) {
            val intent = it.data!!.extras?.get("data") as Bitmap
            intent.run {

            }
        }
    }

    private val galleryLauncher = registerActivityResultLauncher {
        if (it.resultCode == AppCompatActivity.RESULT_OK) {
            val selectedImage: Uri?
            if (it.data != null) {
                selectedImage = it.data!!.data
                val inputStream: InputStream?
                try {
                    inputStream = requireActivity().contentResolver.openInputStream(selectedImage!!)
                    val selectedImg = BitmapFactory.decodeStream(inputStream)
//                    supportDocList.add(SupportDocModel(selectedImg))
//                    binding.clSupportDocument.isVisible = false
                    binding.rvSupportDocument.isVisible = true
                    binding.ivAddDoc.isVisible = true
                    setSupportDocumentAdapter()
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
    }

}