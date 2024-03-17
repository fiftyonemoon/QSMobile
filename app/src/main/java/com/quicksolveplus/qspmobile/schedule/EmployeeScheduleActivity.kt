package com.quicksolveplus.qspmobile.schedule

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.google.gson.Gson
import com.quicksolveplus.api.Api
import com.quicksolveplus.api.RequestParameters
import com.quicksolveplus.api.ResponseStatus
import com.quicksolveplus.dashboard.models.Worker
import com.quicksolveplus.dialogs.QSFilterCheckBox
import com.quicksolveplus.modifiers.Actifiers.onActivityBackPressed
import com.quicksolveplus.modifiers.Actifiers.openActivityForResult
import com.quicksolveplus.modifiers.Actifiers.registerActivityResultLauncher
import com.quicksolveplus.modifiers.Actifiers.saveBitmapToCached
import com.quicksolveplus.qsbase.Base
import com.quicksolveplus.qspmobile.R
import com.quicksolveplus.qspmobile.databinding.ActivityEmployeesScheduleBinding
import com.quicksolveplus.qspmobile.employee.model.Employees
import com.quicksolveplus.qspmobile.employee.model.EmployeesItem
import com.quicksolveplus.qspmobile.schedule.adapters.EmployeeScheduleAdapter
import com.quicksolveplus.qspmobile.schedule.viewmodel.EmployeeScheduleViewModel
import com.quicksolveplus.utils.*
import okhttp3.ResponseBody

/**
 * 31/03/23.
 *
 * @author hardkgosai.
 */
class EmployeeScheduleActivity : Base() {

    private lateinit var binding: ActivityEmployeesScheduleBinding
    private val viewModel: EmployeeScheduleViewModel by viewModels()

    private var employeeScheduleAdapter: EmployeeScheduleAdapter? = null
    private var isFilterChecked: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEmployeesScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
        initBackPressed()
        setObservers()
        getEmployeesData()
    }

    private fun initUI() {
        binding.toolbar.ivHome.isVisible = false
        binding.toolbar.ivBack.isVisible = true
        binding.toolbar.tvTitle.text = getString(R.string.str_select_employee)
        binding.toolbar.ivFilter.setOnClickListener { openFilterDialog() }
        binding.toolbar.ivSearch.setOnClickListener { openSearch() }
        binding.toolbar.ivBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun initBackPressed() {
        onActivityBackPressed {
            if (binding.toolbar.searchView.visibility == View.VISIBLE) {
                binding.toolbar.searchView.setQuery("", true)
                binding.toolbar.searchView.clearFocus()
                binding.toolbar.searchView.isGone = true
                binding.toolbar.ivSearch.isVisible = true
                binding.toolbar.tvTitle.isVisible = true
            } else {
                finish()
            }
        }
    }

    private fun setObservers() {
        viewModel.getResponseStatus().observe(this) {
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

    private fun proceedResponse(success: ResponseStatus.Success) {
        when (success.apiName) {
            Api.getWorkers, Api.getWorkersForEmployeeOffices, Api.getWorkersWithSameSupervisor -> {
                if (success.data is Employees) {
                    proceedEmployees(success.data)
                }
            }
            Api.getWorker -> {
                if (success.data is Worker) {

                }
            }
            Api.getWorkerProfilePic -> {
                if (success.data is ResponseBody) {
                    if (success.other is Pair<*, *>) {
                        val array = success.data.bytes()
                        val bitmap = BitmapFactory.decodeByteArray(array, 0, array.size)
                        val position = success.other.first as Int
                        val employee = success.other.second as EmployeesItem
                        saveBitmapToCached(filename = employee.WorkerProfilePic, bitmap = bitmap)
                        employeeScheduleAdapter?.notifyItemChanged(position)
                    }
                }
            }
        }
    }

    private fun getEmployeesData() {
        Preferences.instance?.user?.run {
            QSPermissions.run {
                if (hasViewUserScheduleOnlyReadPermission() || hasViewUserScheduleOnlyAccessPermission() || hasViewUserScheduleOnlyUpdatePermission() || hasViewUserScheduleOnlyCreatePermission() || hasViewUserScheduleOnlyDeletePermission()) {
                    getWorker()
                } else if (!isAdmin && branchOffices.isNotEmpty() && (hasViewSameSupervisedEmployeesAsUserReadPermission() || hasViewSameSupervisedEmployeesAsUserAccessPermission() || hasViewSameSupervisedEmployeesAsUserUpdatePermission() || hasViewSameSupervisedEmployeesAsUserCreatePermission() || hasViewSameSupervisedEmployeesAsUserDeletePermission())) {
                    getWorkersWithSameSupervisor()
                } else if (!isAdmin && branchOffices.isNotEmpty() && (hasViewOfficeEmployeesOnlyReadPermission() || hasViewOfficeEmployeesOnlyAccessPermission() || hasViewOfficeEmployeesOnlyUpdatePermission() || hasViewOfficeEmployeesOnlyCreatePermission() || hasViewOfficeEmployeesOnlyDeletePermission())) {
                    getWorkersForEmployeeOffices()
                } else {
                    getWorkers()
                }
            }
        }
    }

    private fun getWorkers() {
        Preferences.instance?.user?.run {
            val body = RequestParameters.forEmployeeData(
                staffID = uID,
                userLevel1 = userLevel1,
                userLevel2 = userLevel2,
                userSecurityLevel = securityLevel
            )
            viewModel.getWorkers(body)
        }
    }

    private fun getWorker() {
        Preferences.instance?.user?.run {
            val body = RequestParameters.forWorker(staffID = uID)
            viewModel.getWorker(body)
        }
    }

    private fun getWorkersForEmployeeOffices() {
        Preferences.instance?.user?.run {
            val body = RequestParameters.forWorkersForEmployeeOffices(staffID = uID)
            viewModel.getWorkersForEmployeeOffices(body)
        }
    }

    private fun getWorkersWithSameSupervisor() {
        Preferences.instance?.user?.run {
            val body = RequestParameters.forWorkersWithSameSupervisor(staffID = uID)
            viewModel.getWorkersWithSameSupervisor(body)
        }
    }

    private fun proceedEmployees(employees: Employees) {
        binding.tvWarn.isVisible = employees.isEmpty()
        binding.rv.isVisible = employees.isNotEmpty()
        if (employees.isNotEmpty()) {
            setAdapter(employees)
        }
    }

    private fun setAdapter(employees: Employees) {
        employeeScheduleAdapter = EmployeeScheduleAdapter(employees, viewModel) { position ->
            val employee = employees[position]
            openActivityForResult(ScheduleActivity::class.java, launcher) {
                putString(Constants.worker_map, Gson().toJson(employee))
                putString(Constants.from, Constants.worker)
            }
        }
        binding.rv.adapter = employeeScheduleAdapter
    }

    private fun openFilterDialog() {
        QSFilterCheckBox(this, isFilterChecked) { isChecked ->
            this.isFilterChecked = isChecked
            if (isChecked) {
                getWorkersWithSameSupervisor()
            } else {
                getEmployeesData()
            }
        }.show()
    }

    private fun openSearch() {
        binding.toolbar.searchView.isVisible = true
        binding.toolbar.ivSearch.isGone = true
        binding.toolbar.tvTitle.isGone = true

        binding.toolbar.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                employeeScheduleAdapter?.filter(newText)
                return true
            }
        })
        binding.toolbar.searchView.findViewById<View>(androidx.appcompat.R.id.search_button)
            .performClick()
    }

    private val launcher = registerActivityResultLauncher {
        if (it.resultCode == RESULT_OK) {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        QSCalendar.refresh(resources)
    }
}