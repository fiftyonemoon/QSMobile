package com.quicksolveplus.qspmobile.employee

import android.app.SearchManager
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.quicksolveplus.api.Api
import com.quicksolveplus.api.RequestParameters
import com.quicksolveplus.api.ResponseStatus
import com.quicksolveplus.dialogs.QSFilterCheckBox
import com.quicksolveplus.modifiers.Actifiers.saveBitmapToCached
import com.quicksolveplus.qspmobile.R
import com.quicksolveplus.qspmobile.databinding.ActivityEmployeeBinding
import com.quicksolveplus.qspmobile.employee.adapter.EmployeeAdapter
import com.quicksolveplus.qspmobile.employee.model.Employees
import com.quicksolveplus.qspmobile.employee.model.EmployeesItem
import com.quicksolveplus.qspmobile.employee.viewmodel.EmployeeViewModel
import com.quicksolveplus.utils.*
import okhttp3.ResponseBody

class EmployeeActivity : AppCompatActivity() {

    lateinit var binding: ActivityEmployeeBinding
    private val viewModel: EmployeeViewModel by viewModels()
    private var employeeAdapter: EmployeeAdapter? = null
    private var isChecked: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEmployeeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
        setObservers()
        getEmployeeService()
        getEmployeeService()
    }

    private fun getEmployeeService() {
        if (QSPermissions.hasEmployeeUserRelatedInfoOnlyAccessPermission() || QSPermissions.hasEmployeeUserRelatedInfoOnlyReadPermission() || QSPermissions.hasEmployeeUserRelatedInfoOnlyUpdatePermission()) {
            getWorker()
        } else if (!QSPermissions.hasEmployeeModuleAdminPermission() && (QSPermissions.hasViewSameSupervisedEmployeesUserAccessPermission() || QSPermissions.hasViewSameSupervisedEmployeesUserReadPermission() || QSPermissions.hasViewSameSupervisedEmployeesUserUpdatePermission()) && !Preferences.instance?.user?.branchOffices.isNullOrEmpty()) {
            getWorkersWithSameSupervisor()
        } else if (!QSPermissions.hasEmployeeModuleAdminPermission() && (QSPermissions.hasPermissionViewOfficeEmployeesOnlyAccess() || QSPermissions.hasPermissionViewOfficeEmployeesOnlyRead() || QSPermissions.hasPermissionViewOfficeEmployeesOnlyUpdate()) && !Preferences.instance?.user?.branchOffices.isNullOrEmpty()) {
            getWorkersForEmployeeOffices()
        } else {
            getEmployeeData()
        }
    }

    private fun initUI() {
        binding.toolbar.ivHome.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.toolbar.ivBack.setOnClickListener {
            setUIToolBar()
        }
        binding.toolbar.tvTitle.text = getString(R.string.str_select_employee)
        binding.toolbar.ivSearch.setOnClickListener { search() }
        binding.toolbar.ivFilter.setOnClickListener { openFilterDialog() }
    }

    private fun setUIToolBar() {
        binding.toolbar.apply {
            ivBack.isGone = true
            ivHome.isVisible = true
            searchView.isGone = true
            ivSearch.isVisible = true
            tvTitle.isVisible = true
        }
    }

    private fun openFilterDialog() {
        QSFilterCheckBox(this, isChecked) { isChecked ->
            this.isChecked = isChecked
            if (isChecked) {
                getWorkersByMyCaseload()
            } else {
                getEmployeeService()
            }
        }.show()
    }

    private fun search() {
        binding.toolbar.searchView.isVisible = true
        binding.toolbar.ivSearch.isGone = true
        binding.toolbar.tvTitle.isGone = true
        binding.toolbar.ivBack.isVisible = true
        binding.toolbar.ivHome.isGone = true

        val searchManager = getSystemService(SEARCH_SERVICE) as SearchManager
        binding.toolbar.searchView.maxWidth = Int.MAX_VALUE
        binding.toolbar.searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        binding.toolbar.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                employeeAdapter?.filter(newText)
                return true
            }
        })
    }

    private fun setObservers() {
        viewModel.responseStatus().observe(this) {
            when (it) {
                is ResponseStatus.Running -> {
                    if (it.apiName != Api.getWorkerProfilePic) {
                        showQSProgress(this)
                    }
                }
                is ResponseStatus.Success -> {
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

    private fun getEmployeeData() {
        Preferences.instance?.user?.run {
            val body = RequestParameters.forEmployeeData(securityLevel, userLevel1, userLevel2, uID)
            viewModel.getWorkers(body = body)
        }
    }

    private fun getWorkersByMyCaseload() {
        Preferences.instance?.user?.run {
            val body = RequestParameters.forWorkersByMyCaseload(false, uID)
            viewModel.getWorkersByMyCaseload(body = body)
        }
    }

    private fun getWorkersForEmployeeOffices() {
        Preferences.instance?.user?.run {
            val body = RequestParameters.forWorkersForEmployeeOffices(uID)
            viewModel.getWorkersForEmployeeOffices(body)
        }
    }

    private fun getWorkersWithSameSupervisor() {
        Preferences.instance?.user?.run {
            val body = RequestParameters.forWorkersWithSameSupervisor(uID)
            viewModel.getWorkersWithSameSupervisor(body)
        }
    }

    private fun getWorker() {
        Preferences.instance?.user?.run {
            val body = RequestParameters.forWorker(uID)
            viewModel.getWorker(body)
        }
    }

    private fun proceedResponse(success: ResponseStatus.Success) {
        when (success.apiName) {
            Api.getWorkers, Api.getWorkersByMyCaseload, Api.getWorker, Api.getWorkersWithSameSupervisor, Api.getWorkersForEmployeeOffices -> {
                if (success.data is Employees) {
                    proceedEmployee(success.data)
                }
            }
            Api.getWorkerProfilePic -> {
                if (success.data is ResponseBody) {
                    if (success.other is Pair<*, *>) {
                        val array = success.data.bytes()
                        val bitmap = BitmapFactory.decodeByteArray(array, 0, array.size)
                        val position = success.other.first as Int
                        val employee = success.other.second as EmployeesItem
                        saveBitmapToCached(employee.WorkerProfilePic, bitmap)
                        employeeAdapter?.notifyItemChanged(position)
                    }
                }
            }
        }
    }

    private fun proceedEmployee(employee: Employees) {
        if (employee.isNotEmpty()) {
            binding.tvNoData.isGone = true
            binding.rvEmployees.isVisible = true
            setAdapter(employee)
        } else {
            binding.tvNoData.isVisible = true
            binding.tvNoData.text = getString(R.string.str_no_employee_msg)
            binding.rvEmployees.isGone = true
        }
    }

    private fun setAdapter(employee: Employees) {
        employeeAdapter = EmployeeAdapter(employee, viewModel) { position ->
            //on employee click
        }
        binding.rvEmployees.adapter = employeeAdapter
    }

}