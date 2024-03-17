package com.quicksolveplus.covid.covid_form.declaration_form.fragments

import android.app.Activity
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
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
import com.quicksolveplus.qspmobile.databinding.FragmentSelectVaccineDateBinding
import com.quicksolveplus.utils.*
import okhttp3.ResponseBody

class SelectVaccineDateFragment : Fragment() {

    lateinit var binding: FragmentSelectVaccineDateBinding
    private var covidOptionTypeEnum: CovidOptionTypeEnum? = null
    private val viewModel by lazy {
        ViewModelProvider(this)[CovidFormViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSelectVaccineDateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = arguments
        if (args != null) {
            covidOptionTypeEnum =
                args.getSerializable(Constants.SelectedCovidOptionTypeEnum) as CovidOptionTypeEnum?
        }
        initUI()
        setObservers()
    }

    fun newInstance(covidOptionTypeEnum: CovidOptionTypeEnum?): SelectVaccineDateFragment {
        val fragment = SelectVaccineDateFragment()
        val args = Bundle()
        args.putSerializable(Constants.SelectedCovidOptionTypeEnum, covidOptionTypeEnum)
        fragment.arguments = args
        return fragment
    }

    private fun initUI() {
        getImageFile()
        manageDoseDateVisibility()
        manageNextButtonVisibility()
        binding.apply {
            tvDose1Date.setOnClickListener {
                openDatePickerDialog(
                    requireActivity(),
                    binding.tvDose1Date,
                    binding.tvDose1Date.text.toString()
                )
            }
            tvDose2Date.setOnClickListener {
                if (tvDose1Date.text.toString().isEmpty()) {
                    toast(requireContext(), getString(R.string.str_please_select_dose_1_date_first))
                } else {
                    openDatePickerDialog(
                        requireActivity(),
                        binding.tvDose2Date,
                        binding.tvDose2Date.text.toString()
                    )
                }
            }
            tvDoseDate.setOnClickListener {
                openDatePickerDialog(
                    requireActivity(),
                    binding.tvDoseDate,
                    binding.tvDoseDate.text.toString()
                )
            }
            btnNext.setOnClickListener {
                openUploadVaccineDocumentFragment()
            }
            btnBack.setOnClickListener {
                val activity: Activity? = activity
                if (activity is CovidVaccineDeclarationActivity) {
                    activity.onBackPressedDispatcher.onBackPressed()
                }
            }
        }
    }

    private fun manageDoseDateVisibility() {
        binding.apply {
            if (covidOptionTypeEnum == CovidOptionTypeEnum.JohnsonAndJohnson) {
                tvDoseDate.visibility = View.VISIBLE
                tvDoseDateLabel.visibility = View.VISIBLE
                tvDose1DateLabel.visibility = View.GONE
                tvDose1Date.visibility = View.GONE
                tvDose2DateLabel.visibility = View.GONE
                tvDose2Date.visibility = View.GONE
                tvDoYouWantToUploadProof.text =
                    getString(R.string.str_select_johnson_and_johnson_dose_date)
            } else {
                tvDoseDate.visibility = View.GONE
                tvDoseDateLabel.visibility = View.GONE
                tvDose1DateLabel.visibility = View.VISIBLE
                tvDose1Date.visibility = View.VISIBLE
                tvDose2DateLabel.visibility = View.VISIBLE
                tvDose2Date.visibility = View.VISIBLE
                tvDoYouWantToUploadProof.text =
                    getString(R.string.str_select_dates_of_first_and_second_dose)
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
                if (success.data is ResponseBody && !isDetached) {
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


    private fun openUploadVaccineDocumentFragment() {
        val fragmentManager = parentFragmentManager
        val uploadVaccineDocumentsFragment: Fragment = UploadVaccineDocumentFragment().newInstance(
            covidOptionTypeEnum!!,
            binding.tvDose1Date.text.toString(),
            binding.tvDose2Date.text.toString(),
            binding.tvDoseDate.text.toString(),
            ""
        )
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(
            R.id.flDeclaration,
            uploadVaccineDocumentsFragment,
            uploadVaccineDocumentsFragment.javaClass.name
        )
        fragmentTransaction.addToBackStack(uploadVaccineDocumentsFragment.javaClass.name)
        fragmentTransaction.commit()
    }

    private fun manageNextButtonVisibility() {
        if (covidOptionTypeEnum === CovidOptionTypeEnum.JohnsonAndJohnson) {
            binding.tvDoseDate.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s != null)
                        binding.btnNext.isVisible = true
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        } else {
            binding.tvDose2Date.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s != null)
                        binding.btnNext.isVisible = true
                }

                override fun afterTextChanged(s: Editable?) {}
            })

        }
    }


}