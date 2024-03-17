package com.quicksolveplus.qspmobile.schedule

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.quicksolveplus.api.Api
import com.quicksolveplus.api.RequestParameters
import com.quicksolveplus.api.ResponseStatus
import com.quicksolveplus.modifiers.Actifiers.onActivityBackPressed
import com.quicksolveplus.modifiers.Actifiers.openActivity
import com.quicksolveplus.qsbase.Base
import com.quicksolveplus.qspmobile.R
import com.quicksolveplus.qspmobile.clients.models.ClientsItem
import com.quicksolveplus.qspmobile.databinding.ActivityTrainedEmployeesBinding
import com.quicksolveplus.qspmobile.employee.model.Employees
import com.quicksolveplus.qspmobile.employee.model.EmployeesItem
import com.quicksolveplus.qspmobile.schedule.adapters.TrainedEmployeesAdapter
import com.quicksolveplus.qspmobile.schedule.viewmodel.TrainedEmployeesViewModel
import com.quicksolveplus.utils.*

/**
 * 11/04/23.
 *
 * @author hardkgosai.
 */
class TrainedEmployeesActivity : Base() {

    private lateinit var binding: ActivityTrainedEmployeesBinding
    private val viewModel: TrainedEmployeesViewModel by viewModels()

    private var adapter: TrainedEmployeesAdapter? = null
    private var client: ClientsItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTrainedEmployeesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getIntentData()
        initUI()
        initBackPressed()
        setObservers()
        fetchTrainedEmployees()
    }

    private fun getIntentData() {
        intent.extras?.run {
            if (containsKey(Constants.client_map)) {
                client = Gson().fromJson(
                    getString(Constants.client_map), object : TypeToken<ClientsItem?>() {}.type
                )
            }
        }
    }

    private fun initUI() {
        binding.toolbar.ivHome.isGone = true
        binding.toolbar.ivFilter.isGone = true
        binding.toolbar.ivBack.isVisible = true
        binding.toolbar.ivSearch.isVisible = true
        binding.toolbar.tvTitle.text = getString(R.string.str_trained_employee)
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
                    showQSProgress(this)
                }
                is ResponseStatus.Success -> {
                    proceedResponse(it)
                    dismissQSProgress()
                }
                is ResponseStatus.Failed -> {
                    if (it.msg == getString(R.string.msg_server_error)) {
                        toast(this, it.msg)
                    }
                    proceedFailure(it)
                    dismissQSProgress()
                }
                else -> {
                    toast(this, "Something goes wrong")
                    dismissQSProgress()
                }
            }
        }
    }

    private fun fetchTrainedEmployees() {
        client?.run {
            val body = RequestParameters.forClientTrainedEmployees(clientID = ClientUID)
            viewModel.getClientTrainedEmployees(body)
        }
    }

    private fun proceedResponse(success: ResponseStatus.Success) {
        when (success.apiName) {
            Api.getClientTrainedEmployees -> {
                if (success.data is Employees) {
                    proceedClientTrainedEmployees(success.data)
                }
            }
        }
    }

    private fun proceedFailure(failed: ResponseStatus.Failed) {
        when (failed.apiName) {
            Api.getClientTrainedEmployees -> {
                binding.tvWarn.isVisible = true
            }
        }
    }

    private fun proceedClientTrainedEmployees(employees: Employees) {
        binding.tvWarn.isVisible = employees.isEmpty()
        TrainedEmployeesAdapter(employees, viewModel) {
            navigateToSchedule(employees[it])
        }.apply {
            binding.rv.adapter = this
        }
    }


    private fun navigateToSchedule(employeesItem: EmployeesItem) {
        QSPermissions.run {
            if (!hasViewUserScheduleOnlyReadPermission()) {
                openActivity(ScheduleActivity::class.java) {
                    putString(Constants.worker_map, Gson().toJson(employeesItem))
                    putString(Constants.from, Constants.worker)
                }
            }
        }
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
                adapter?.filter(newText)
                return true
            }
        })
        binding.toolbar.searchView.findViewById<View>(androidx.appcompat.R.id.search_button)
            .performClick()
    }
}