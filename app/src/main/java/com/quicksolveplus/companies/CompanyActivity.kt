package com.quicksolveplus.companies

import android.os.Bundle
import androidx.activity.viewModels
import android.widget.SearchView
import androidx.core.view.isVisible
import com.quicksolveplus.api.Api
import com.quicksolveplus.api.ResponseStatus
import com.quicksolveplus.companies.adapters.CompaniesAdapter
import com.quicksolveplus.companies.models.Companies
import com.quicksolveplus.companies.viewmodel.CompanyViewModel
import com.quicksolveplus.dialogs.QSAlert
import com.quicksolveplus.qsbase.Base
import com.quicksolveplus.qspmobile.R
import com.quicksolveplus.qspmobile.databinding.ActivityCompanyBinding
import com.quicksolveplus.utils.Preferences
import com.quicksolveplus.utils.dismissQSProgress
import com.quicksolveplus.utils.showQSProgress
import com.quicksolveplus.utils.toast

class CompanyActivity : Base() {

    private lateinit var binding: ActivityCompanyBinding
    private val viewModel: CompanyViewModel by viewModels()
    private var adapter: CompaniesAdapter? = null
    private val shortListedCompanies by lazy {
        Preferences.instance?.shortListedCompanies ?: arrayListOf()
    }

    companion object {
        var companies: Companies? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCompanyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
        setObservers()
        fetchCompanies()
    }

    private fun initUI() {
        binding.toolbarPanel.tvTitle.text = getString(R.string.str_select_your_company)
        binding.toolbarPanel.tvSave.isVisible = true
        binding.toolbarPanel.tvSave.setOnClickListener { proceedSave() }
        binding.toolbarPanel.ivBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.searchView.setOnQueryTextListener(queryTextListener)
    }

    private fun setObservers() {
        viewModel.getResponseStatus().observe(this) {
            when (it) {
                is ResponseStatus.Running -> {
                    showQSProgress(activity = this)
                }
                is ResponseStatus.Success -> {
                    proceedResponse(success = it)
                    dismissQSProgress()
                }
                is ResponseStatus.Failed -> {
                    toast(context = this, message = it.msg)
                    dismissQSProgress()
                }
                else -> {
                    toast(context = this, message = "Something goes wrong")
                    dismissQSProgress()
                }
            }
        }
    }

    private fun fetchCompanies() {
        if (companies == null) {
            viewModel.getQSMobileWebClients()
        } else proceedCompanies()
    }

    private fun proceedResponse(success: ResponseStatus.Success) {
        when (success.apiName) {
            Api.getQSMobileWebClients -> {
                if (success.data is Companies) {
                    companies = success.data
                    proceedCompanies()
                }
            }
        }
    }

    private fun proceedCompanies() {
        setRecyclerView()
        showSelectCompanyAlert()
    }

    private fun setRecyclerView() {
        val companies = companies?.qSPMobileWebClientList ?: arrayListOf()
        CompaniesAdapter(
            companies = ArrayList(companies), shortListedCompanies = shortListedCompanies
        ).apply {
            adapter = this
            binding.rvCompany.adapter = this
        }
    }

    private fun showSelectCompanyAlert() {
        QSAlert(
            context = this,
            title = getString(R.string.str_title_alert),
            message = getString(R.string.str_msg_select_company_code_associated),
            positiveButtonText = getString(R.string.str_ok),
            cancelable = false
        ).show()
    }

    private fun proceedSave() {
        val selectedCompaniesNames = StringBuilder()

        if (shortListedCompanies.size <= 0) {
            showSelectCompanyAlert()
            return
        }

        shortListedCompanies.mapIndexed { index, item ->
            selectedCompaniesNames.append(item.companyAbbreviation)
            if (index == shortListedCompanies.size - 1) {
                showSelectedCompaniesDialog(selectedCompaniesNames.toString())
            } else selectedCompaniesNames.append(", ")
        }
    }

    private fun showSelectedCompaniesDialog(selectedCompaniesNames: String) {
        QSAlert(
            context = this,
            message = String.format(
                getString(R.string.str_selected_company_continue_confirmation),
                selectedCompaniesNames
            ),
            positiveButtonText = getString(R.string.action_yes),
            negativeButtonText = getString(R.string.action_no),
            cancelable = false
        ) { isPositive ->
            if (isPositive) {
                Preferences.instance?.shortListedCompanies = shortListedCompanies
                finish()
            }
        }.show()
    }

    private val queryTextListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            return true
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            adapter?.filter(newText)
            return true
        }
    }
}
