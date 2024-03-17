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
import com.quicksolveplus.dialogs.QSFilterCheckBox
import com.quicksolveplus.modifiers.Actifiers.onActivityBackPressed
import com.quicksolveplus.modifiers.Actifiers.openActivityForResult
import com.quicksolveplus.modifiers.Actifiers.registerActivityResultLauncher
import com.quicksolveplus.modifiers.Actifiers.saveBitmapToCached
import com.quicksolveplus.qsbase.Base
import com.quicksolveplus.qspmobile.R
import com.quicksolveplus.qspmobile.clients.models.Clients
import com.quicksolveplus.qspmobile.clients.models.ClientsItem
import com.quicksolveplus.qspmobile.databinding.ActivityClientScheduleBinding
import com.quicksolveplus.qspmobile.schedule.adapters.ClientsScheduleAdapter
import com.quicksolveplus.qspmobile.schedule.viewmodel.ClientScheduleViewModel
import com.quicksolveplus.utils.*
import okhttp3.ResponseBody

/**
 * 31/03/23.
 *
 * @author hardkgosai.
 */
class ClientScheduleActivity : Base() {

    private lateinit var binding: ActivityClientScheduleBinding
    private val viewModel: ClientScheduleViewModel by viewModels()

    private var clientsAdapter: ClientsScheduleAdapter? = null
    private var isFilterChecked: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityClientScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
        initBackPressed()
        setObservers()
        getClientsData()
    }

    private fun initUI() {
        binding.toolbar.ivHome.isVisible = false
        binding.toolbar.ivBack.isVisible = true
        binding.toolbar.tvTitle.text = getString(R.string.str_select_client)
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
                    if (it.apiName != Api.getClientProfilePic) {
                        showQSProgress(this)
                    }
                }
                is ResponseStatus.Success -> {
                    proceedResponse(it)
                    dismissQSProgress()
                }
                is ResponseStatus.Failed -> {
                    if (it.msg == getString(R.string.msg_server_error)) {
                        toast(this, it.msg)
                    }
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
            Api.getClients, Api.getClientCaseloadOnly, Api.getClientsForEmployeeOffices, Api.getClientsTrainedByEmployee -> {
                if (success.data is Clients) {
                    proceedClients(success.data)
                }
            }
            Api.getClientProfilePic -> {
                if (success.data is ResponseBody) {
                    if (success.other is Pair<*, *>) {
                        val array = success.data.bytes()
                        val bitmap = BitmapFactory.decodeByteArray(array, 0, array.size)
                        val position = success.other.first as Int
                        val client = success.other.second as ClientsItem
                        saveBitmapToCached(filename = client.ClientProfilePic, bitmap = bitmap)
                        clientsAdapter?.notifyItemChanged(position)
                    }
                }
            }
        }
    }

    private fun getClientsData() {
        Preferences.instance?.user?.run {
            if (branchOffices.isNotEmpty()) {
                QSPermissions.run {
                    if (!isAdmin && (hasViewTrainedClientsOnlyAccessPermission() || hasViewTrainedClientsOnlyReadPermission())) {
                        getClientsTrainedByEmployee()
                    } else if (!isAdmin && (hasViewClientCaseloadOfAssignedSupervisorOnlyReadPermission() || hasViewClientCaseloadOfAssignedSupervisorOnlyAccessPermission())) {
                        getClientCaseloadOnly()
                    } else if (!isAdmin && (hasViewOfficeClientsOnlyReadPermission() || hasViewOfficeClientsOnlyAccessPermission())) {
                        getClientsForEmployeeOffices()
                    } else {
                        getAllClients()
                    }
                }
            } else {
                getAllClients()
            }
        }
    }

    private fun getAllClients() {
        Preferences.instance?.user?.run {
            val body = RequestParameters.forClientData(
                userUID = uID,
                userLevel1 = userLevel1,
                userLevel2 = userLevel2,
                userSecurityLevel = securityLevel
            )
            viewModel.getClientsData(body)
        }
    }

    private fun getClientsForEmployeeOffices() {
        Preferences.instance?.user?.run {
            val body = RequestParameters.forClientsForEmployeeOffices(userUID = uID)
            viewModel.getClientsForEmployeeOffices(body)
        }
    }

    private fun getClientCaseloadOnly() {
        Preferences.instance?.user?.run {
            val body = RequestParameters.forClientCaseloadOnly(userUID = uID)
            viewModel.getClientCaseloadOnly(body)
        }
    }

    private fun getClientsTrainedByEmployee() {
        Preferences.instance?.user?.run {
            val body = RequestParameters.forClientsTrainedByEmployee(userUID = uID)
            viewModel.getClientsTrainedByEmployee(body)
        }
    }

    private fun proceedClients(clients: Clients) {
        binding.tvWarn.isVisible = clients.isEmpty()
        binding.rv.isVisible = clients.isNotEmpty()
        if (clients.isNotEmpty()) {
            setAdapter(clients)
        }
    }

    private fun setAdapter(clients: Clients) {
        clientsAdapter = ClientsScheduleAdapter(clients, viewModel) { position ->
            val client = clients[position]
            openActivityForResult(ScheduleActivity::class.java, launcher) {
                putString(Constants.client_map, Gson().toJson(client))
                putString(Constants.from, Constants.client)
            }
        }
        binding.rv.adapter = clientsAdapter
    }

    private fun openFilterDialog() {
        QSFilterCheckBox(this, isFilterChecked) { isChecked ->
            this.isFilterChecked = isChecked
            if (isChecked) {
                getClientCaseloadOnly()
            } else {
                getClientsData()
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
                clientsAdapter?.filter(newText)
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