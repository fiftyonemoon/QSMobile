package com.quicksolveplus.qspmobile.clients


import android.app.SearchManager
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.google.gson.Gson
import com.quicksolveplus.api.Api
import com.quicksolveplus.api.RequestParameters
import com.quicksolveplus.api.ResponseStatus
import com.quicksolveplus.dialogs.QSFilterCheckBox
import com.quicksolveplus.modifiers.Actifiers.saveBitmapToCached
import com.quicksolveplus.qspmobile.R
import com.quicksolveplus.qspmobile.clients.adapter.ClientsAdapter
import com.quicksolveplus.qspmobile.clients.models.Clients
import com.quicksolveplus.qspmobile.clients.models.ClientsItem
import com.quicksolveplus.qspmobile.clients.viewmodel.ClientsViewModel
import com.quicksolveplus.qspmobile.databinding.ActivityClientsBinding
import com.quicksolveplus.utils.*
import okhttp3.ResponseBody

class ClientsActivity : AppCompatActivity() {

    lateinit var binding: ActivityClientsBinding
    private val viewModel: ClientsViewModel by viewModels()
    private var clientsAdapter: ClientsAdapter? = null
    private var isChecked: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityClientsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
        setObservers()
    }

    private fun initUI() {
        binding.toolbar.ivHome.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.toolbar.ivBack.setOnClickListener {
            setUIToolBar()
        }
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

    private fun getClientService() {
        if (Preferences.instance?.user?.branchOffices?.isNotEmpty() == true) {
            if (!QSPermissions.hasClientModuleAdminPermission() && (QSPermissions.hasViewTrainedClientsOnlyAccessPermission() || QSPermissions.hasViewTrainedClientsOnlyReadPermission() || QSPermissions.hasViewTrainedClientsOnlyUpdatePermission())) {
                getClientsTrainedByEmployee()
            } else if (!QSPermissions.hasClientModuleAdminPermission() && (QSPermissions.hasViewClientCaseloadOfAssignedSupervisorOnlyAccessPermission() || QSPermissions.hasViewClientCaseloadOfAssignedSupervisorOnlyReadPermission() || QSPermissions.hasViewClientCaseloadOfAssignedSupervisorOnlyUpdatePermission())) {
                getClientCaseloadOnly()
            } else if (!QSPermissions.hasClientModuleAdminPermission() && (QSPermissions.hasViewOfficeClientsOnlyAccessPermission() || QSPermissions.hasViewOfficeClientsOnlyReadPermission() || QSPermissions.hasViewOfficeClientsOnlyUpdatePermission())) {
                getClientsForEmployeeOffices()
            } else {
                getClientsData()
            }
        } else {
            getClientsData()
        }
    }

    private fun openFilterDialog() {
        QSFilterCheckBox(this, isChecked) { isChecked ->
            this.isChecked = isChecked
            if (isChecked) {
                getClientCaseloadOnly()
            } else {
                getClientService()
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
                clientsAdapter?.filter(newText)
                return true
            }
        })
    }

    private fun setObservers() {
        viewModel.responseStatus().observe(this) {
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

    private fun getClientsData() {
        Preferences.instance?.user?.run {
            val body = RequestParameters.forClientData(
                userSecurityLevel = securityLevel,
                userLevel1 = userLevel1,
                userLevel2 = userLevel2,
                userUID = uID
            )
            viewModel.getClientsData(body = body)
        }
    }

    private fun getClientCaseloadOnly() {
        Preferences.instance?.user?.run {
            val body = RequestParameters.forClientCaseloadOnly(uID)
            viewModel.getClientCaseloadOnly(body = body)
        }
    }

    private fun getClientsForEmployeeOffices() {
        Preferences.instance?.user?.run {
            val body = RequestParameters.forClientsForEmployeeOffices(uID)
            viewModel.getClientsForEmployeeOffices(body = body)
        }
    }

    private fun getClientsTrainedByEmployee() {
        Preferences.instance?.user?.run {
            val body = RequestParameters.forClientsTrainedByEmployee(uID)
            viewModel.getClientsTrainedByEmployee(body = body)
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
                        saveBitmapToCached(client.ClientProfilePic, bitmap)
                        clientsAdapter?.notifyItemChanged(position)
                    }
                }
            }
        }
    }

    private fun proceedClients(clients: Clients) {
        if (clients.isNotEmpty()) {
            binding.tvNoData.isGone = true
            binding.rvClients.isVisible = true
            setAdapter(clients)
        } else {
            binding.tvNoData.isVisible = true
            binding.tvNoData.text = getString(R.string.str_no_client_msg)
            binding.rvClients.isGone = true
        }
    }

    private fun setAdapter(clients: Clients) {
        clientsAdapter = ClientsAdapter(clients, viewModel) { position ->
            val clientData = Gson().toJson(clients[position])
            val intent = Intent(this@ClientsActivity, ClientProfileDetailActivity::class.java)
            intent.putExtra(Constants.clientData, clientData)
            startActivity(intent)
        }
        binding.rvClients.adapter = clientsAdapter
    }

    override fun onResume() {
        super.onResume()
        getClientService()
    }

}