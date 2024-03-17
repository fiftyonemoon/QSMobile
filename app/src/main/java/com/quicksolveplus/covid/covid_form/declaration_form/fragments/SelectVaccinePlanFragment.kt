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
import com.quicksolveplus.qspmobile.databinding.FragmentSelectVaccinePlanBinding
import com.quicksolveplus.utils.*
import okhttp3.ResponseBody
import java.util.*

class SelectVaccinePlanFragment : Fragment() {

    lateinit var binding: FragmentSelectVaccinePlanBinding
    private val viewModel by lazy {
        ViewModelProvider(this)[CovidFormViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSelectVaccinePlanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        setObservers()
    }

    private fun initUI() {
        getImageFile()
        manageNextButtonVisibility()
        binding.apply {
            val currentDate = Calendar.getInstance().time
            val requiredDate: Date =
                QSCalendar.formatDate("11/30/2021", QSCalendar.DateFormats.MMDDYYYY.label)!!

            if (currentDate.before(requiredDate)) {
                rbUncertain.visibility = View.VISIBLE
            } else {
                rbUncertain.visibility = View.GONE
            }

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

    private fun manageNextButtonVisibility() {
        binding.rgVaccine.setOnCheckedChangeListener { _, checkedId ->
            if (binding.rbCovidVaccine.isChecked || binding.rbUncertain.isChecked || binding.rbExemption.isChecked) {
                binding.btnNext.visibility = View.VISIBLE
            } else {
                binding.btnNext.visibility = View.INVISIBLE
            }
        }
    }


    private fun manageNextButtonClick() {
        if (binding.rbCovidVaccine.isChecked) {
            openCovidMessageFragment(CovidOptionTypeEnum.Covid19Vaccine)
        } else if (binding.rbUncertain.isChecked) {
            openCovidMessageFragment(CovidOptionTypeEnum.Uncertain)
        } else if (binding.rbExemption.isChecked) {
            openSelectExemptionFragment(CovidOptionTypeEnum.Exemption)
        }
    }

    private fun openCovidMessageFragment(covidOptionTypeEnum: CovidOptionTypeEnum) {
        val fragmentManager = parentFragmentManager
        val covidVaccineMessageFragment: Fragment =
            CovidVaccineMessageFragment().newInstance(covidOptionTypeEnum)
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(
            R.id.flDeclaration,
            covidVaccineMessageFragment,
            covidVaccineMessageFragment.javaClass.name
        )
        fragmentTransaction.addToBackStack(covidVaccineMessageFragment.javaClass.name)
        fragmentTransaction.commit()
    }

    private fun openSelectExemptionFragment(covidOptionTypeEnum: CovidOptionTypeEnum) {
        val fragmentManager = parentFragmentManager
        val covidVaccineMessageFragment: Fragment =
            SelectExemptionFragment().newInstance(covidOptionTypeEnum)
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(
            R.id.flDeclaration,
            covidVaccineMessageFragment,
            covidVaccineMessageFragment.javaClass.name
        )
        fragmentTransaction.addToBackStack(covidVaccineMessageFragment.javaClass.name)
        fragmentTransaction.commit()
    }


    fun newInstance(): SelectVaccinePlanFragment {
        return SelectVaccinePlanFragment()
    }


}