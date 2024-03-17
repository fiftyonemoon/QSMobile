package com.quicksolveplus.covid.covid_form.declaration_form.fragments

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.quicksolveplus.api.Api
import com.quicksolveplus.api.RequestParameters
import com.quicksolveplus.api.ResponseStatus
import com.quicksolveplus.covid.covid_form.declaration_form.enums.CovidOptionTypeEnum
import com.quicksolveplus.covid.covid_form.declaration_form.viewmodel.CovidFormViewModel
import com.quicksolveplus.modifiers.Glidifiers.loadGlide
import com.quicksolveplus.qspmobile.R
import com.quicksolveplus.qspmobile.databinding.FragmentExemptMedicalReligiousFormBinding
import com.quicksolveplus.utils.*
import okhttp3.ResponseBody
import java.io.Serializable

class ExemptMedicalReligiousFormFragment : Fragment() {

    lateinit var binding : FragmentExemptMedicalReligiousFormBinding
    private var doseDate1: String? = null
    private var doseDate2: String? = null
    private var johnsonDoseDate: String? = null
    private var exemptionType: String? = null
    private var selectedDocumentUrlList: List<String>? = null
    private var covidOptionTypeEnum: CovidOptionTypeEnum? = null


    private val viewModel by lazy {
        ViewModelProvider(this)[CovidFormViewModel::class.java]
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        binding = FragmentExemptMedicalReligiousFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = arguments
        if (args != null) {
            if (args.getSerializable(Constants.SelectedCovidOptionTypeEnum) != null) {
                covidOptionTypeEnum = args.getSerializable(Constants.SelectedCovidOptionTypeEnum) as CovidOptionTypeEnum?
            }
            exemptionType = args.getString(Constants.exemption_type)
            johnsonDoseDate = args.getString(Constants.johnson_dose_date)
            doseDate1 = args.getString(Constants.dose_date1)
            doseDate2 = args.getString(Constants.dose_date2)
            selectedDocumentUrlList = args.getSerializable(Constants.supporting_image_name) as List<String>
        }
        initUI()
        setObservers()
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
            Api.getSignatureFile -> {
                if (success.data is ResponseBody) {
                    val array = success.data.bytes()
                    val bitmap = BitmapFactory.decodeByteArray(array, 0, array.size)
                    binding.ivEmployeeSignature.loadGlide(bitmap, ContextCompat.getDrawable(requireContext(), R.drawable.ic_avatar))
                }
            }
        }
    }


    fun newInstance(
        covidOptionTypeEnum: CovidOptionTypeEnum?,
        doseDate1: String?,
        doseDate2: String?,
        johnsonDoseDate: String?,
        exemptionType: String?,
        selectedDocumentUrlList: List<String?>?
    ): ExemptMedicalReligiousFormFragment {
        val fragment = ExemptMedicalReligiousFormFragment()
        // Supply index input as an argument.
        val args = Bundle()
        args.putSerializable(Constants.SelectedCovidOptionTypeEnum, covidOptionTypeEnum)
        args.putString(Constants.dose_date1, doseDate1)
        args.putString(Constants.dose_date2, doseDate2)
        args.putString(Constants.exemption_type, exemptionType)
        args.putString(Constants.johnson_dose_date, johnsonDoseDate)
        args.putSerializable(Constants.supporting_image_name, selectedDocumentUrlList as Serializable?)
        fragment.arguments = args
        return fragment
    }


    private fun initUI() {
        getSignatureFile()
//        if (exemptionType != null && exemptionType!!.isNotEmpty()) {
//            if (exemptionType == "Medical") {
//                religiousMedicalFormItemsModelArrayList = globals.getUserDetails().getCovidComplianceFormDetails().getMedicalFormItems()
//            } else if (exemptionType == "Religious") {
//                religiousMedicalFormItemsModelArrayList = globals.getUserDetails().getCovidComplianceFormDetails().getReligiousFormItems()
//            }
//        }

    }


    private fun getSignatureFile() {
        val body = RequestParameters.forGetSignatureFile(Preferences.instance!!.user!!.employeeSignature)
        viewModel.getSignatureFile(body)
    }

}