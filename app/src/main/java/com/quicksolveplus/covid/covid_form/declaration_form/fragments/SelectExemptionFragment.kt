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
import com.quicksolveplus.authentication.models.CovidComplianceFormDetails
import com.quicksolveplus.covid.covid_form.declaration_form.CovidVaccineDeclarationActivity
import com.quicksolveplus.covid.covid_form.declaration_form.enums.CovidOptionTypeEnum
import com.quicksolveplus.covid.covid_form.declaration_form.viewmodel.CovidFormViewModel
import com.quicksolveplus.modifiers.Glidifiers.loadGlide
import com.quicksolveplus.qspmobile.R
import com.quicksolveplus.qspmobile.databinding.FragmentSelectExemptionBinding
import com.quicksolveplus.utils.*
import okhttp3.ResponseBody

class SelectExemptionFragment : Fragment() {

    lateinit var binding: FragmentSelectExemptionBinding
    private var exemptionType: String? = null
    var covidOptionTypeEnum: CovidOptionTypeEnum? = null

    private val viewModel by lazy {
        ViewModelProvider(this)[CovidFormViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSelectExemptionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = arguments
        if (args != null) {
            if (args.containsKey(Constants.SelectedCovidOptionTypeEnum)) {
                covidOptionTypeEnum =
                    args.getSerializable(Constants.SelectedCovidOptionTypeEnum) as CovidOptionTypeEnum?
            }
        }
        initUI()
        setObservers()
    }

    private fun initUI() {
        getImageFile()
        manageNextButtonVisibility()
        binding.apply {
            btnNext.setOnClickListener {
                manageNextButtonClick()
            }
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
            if (isRemoving) return@observe
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
                    binding.ivBanner.loadGlide(
                        bitmap,
                        ContextCompat.getDrawable(requireContext(), R.drawable.ic_avatar)
                    )
                }
            }
        }
    }


    private fun manageNextButtonClick() {
        val covidComplianceFormDetails: CovidComplianceFormDetails =
            Preferences.instance?.user?.covidComplianceFormDetails!!
        if (binding.rbMedical.isChecked) {
            exemptionType = "Medical"
            if (covidComplianceFormDetails.requireMedicalDocumentation) {
                openCovidVaccineWantToUploadExemptionProofFragment()
                return
            } else if (covidComplianceFormDetails.hasCustomMedicalForm && Preferences.instance?.user?.covidComplianceFormDetails!!.medicalFormItems.isNotEmpty()) {
                openExemptMedicalReligiousFormFragment()
                return
            }
        } else if (binding.rbReligious.isChecked) {
            exemptionType = "Religious"
            if (covidComplianceFormDetails.requireReligiousDocumentation) {
                openCovidVaccineWantToUploadExemptionProofFragment()
                return
            } else if (covidComplianceFormDetails.hasCustomReligiousForm && Preferences.instance?.user?.covidComplianceFormDetails!!.religiousFormItems.isNotEmpty()) {
                openExemptMedicalReligiousFormFragment()
                return
            }
        }
//        doRequestForSubmitCovidComplianceForm()
    }

    private fun openExemptMedicalReligiousFormFragment() {
        val fragmentManager = parentFragmentManager
        val exemptMedicalReligiousFormFragment: Fragment =
            ExemptMedicalReligiousFormFragment().newInstance(
                covidOptionTypeEnum,
                "",
                "",
                "",
                exemptionType,
                ArrayList<String>()
            )
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(
            R.id.flDeclaration,
            exemptMedicalReligiousFormFragment,
            exemptMedicalReligiousFormFragment.javaClass.name
        )
        fragmentTransaction.addToBackStack(exemptMedicalReligiousFormFragment.javaClass.name)
        fragmentTransaction.commit()
    }

    private fun openCovidVaccineWantToUploadExemptionProofFragment() {
//        val fragmentManager = parentFragmentManager
//        val covidVaccineWantToUploadExemptionProofFragment: Fragment = CovidVaccineWantToUploadExemptionProofFragment.newInstance(Constants.covidOptionTypeEnum, exemptionType)
//        val fragmentTransaction = fragmentManager.beginTransaction()
//        fragmentTransaction.add(R.id.flDeclaration, covidVaccineWantToUploadExemptionProofFragment, covidVaccineWantToUploadExemptionProofFragment.javaClass.name)
//        fragmentTransaction.addToBackStack(covidVaccineWantToUploadExemptionProofFragment.javaClass.name)
//        fragmentTransaction.commit()
    }

    private fun manageNextButtonVisibility() {
        binding.rgVaccine.setOnCheckedChangeListener { _, checkedId ->
            if (binding.rbMedical.isChecked || binding.rbReligious.isChecked) {
                binding.btnNext.visibility = View.VISIBLE
            } else {
                binding.btnNext.visibility = View.INVISIBLE
            }
        }
    }

    fun newInstance(covidOptionTypeEnum: CovidOptionTypeEnum?): SelectExemptionFragment {
        val fragment = SelectExemptionFragment()
        val args = Bundle()
        args.putSerializable(Constants.SelectedCovidOptionTypeEnum, covidOptionTypeEnum)
        fragment.arguments = args
        return fragment
    }


}