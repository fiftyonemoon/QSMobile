package com.quicksolveplus.covid.covid_form.declaration_form.fragments

import android.app.Activity
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.quicksolveplus.api.Api
import com.quicksolveplus.api.RequestParameters
import com.quicksolveplus.api.ResponseStatus
import com.quicksolveplus.covid.covid_form.declaration_form.CovidVaccineDeclarationActivity
import com.quicksolveplus.covid.covid_form.declaration_form.enums.CovidOptionTypeEnum
import com.quicksolveplus.covid.covid_form.declaration_form.viewmodel.CovidFormViewModel
import com.quicksolveplus.modifiers.Glidifiers.loadGlide
import com.quicksolveplus.qspmobile.R
import com.quicksolveplus.qspmobile.databinding.FragmentUploadVaccineDocumentBinding
import com.quicksolveplus.utils.Constants
import com.quicksolveplus.utils.dismissQSProgress
import com.quicksolveplus.utils.showQSProgress
import com.quicksolveplus.utils.toast
import okhttp3.ResponseBody

class UploadVaccineDocumentFragment : Fragment() {

    lateinit var binding: FragmentUploadVaccineDocumentBinding
    private val viewModel by lazy {
        ViewModelProvider(this)[CovidFormViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentUploadVaccineDocumentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        setObservers()
    }

    private fun initUI() {
        getImageFile()
        binding.apply {
            btnBack.setOnClickListener {
                val activity: Activity? = activity
                if (activity is CovidVaccineDeclarationActivity) {
                    activity.onBackPressedDispatcher.onBackPressed()
                }
            }
        }
    }

    private fun getImageFile() {
        val body = RequestParameters.forGetImageFile("", 2)
        viewModel.getImageFile(body)
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
                    if (it.msg == getString(R.string.msg_server_error)) {
                        toast(requireActivity(), it.msg)
                    }
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
                    val array = success.data.bytes()
                    val bitmap = BitmapFactory.decodeByteArray(array, 0, array.size)
                    binding.pbLoading.isGone = true
                    binding.ivBanner.loadGlide(bitmap, ContextCompat.getDrawable(requireContext(), R.drawable.ic_avatar))
                }
            }
        }
    }

    fun newInstance(covidOptionTypeEnum: CovidOptionTypeEnum, doseDate1: String, doseDate2: String, johnsonDoseDate: String, exemptionType: String): UploadVaccineDocumentFragment {
        val fragment: UploadVaccineDocumentFragment = UploadVaccineDocumentFragment()
        // Supply index input as an argument.
        val args = Bundle()
        args.putSerializable(Constants.SelectedCovidOptionTypeEnum, covidOptionTypeEnum)
        args.putString(Constants.dose_date1, doseDate1)
        args.putString(Constants.dose_date2, doseDate2)
        args.putString(Constants.exemption_type, exemptionType)
        args.putString(Constants.johnson_dose_date, johnsonDoseDate)
        fragment.arguments = args
        return fragment
    }



}