package com.quicksolveplus.qspmobile.schedule

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.AdapterView.OnItemClickListener
import android.widget.PopupWindow
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.quicksolveplus.QSMobile
import com.quicksolveplus.api.Api
import com.quicksolveplus.api.RequestParameters
import com.quicksolveplus.api.ResponseStatus
import com.quicksolveplus.dialogs.QSFilterItems
import com.quicksolveplus.modifiers.Actifiers.getPopupWindow
import com.quicksolveplus.modifiers.Actifiers.openActivity
import com.quicksolveplus.modifiers.Actifiers.replaceFragment
import com.quicksolveplus.qsbase.Base
import com.quicksolveplus.qspmobile.R
import com.quicksolveplus.qspmobile.clients.models.ClientsItem
import com.quicksolveplus.qspmobile.dashboard.pos.POSViewActivity
import com.quicksolveplus.qspmobile.databinding.ActivityScheduleBinding
import com.quicksolveplus.qspmobile.employee.model.EmployeesItem
import com.quicksolveplus.qspmobile.schedule.fragments.MonthlyShiftFragment
import com.quicksolveplus.qspmobile.schedule.fragments.WeeklyShiftFragment
import com.quicksolveplus.qspmobile.schedule.models.Dashboard
import com.quicksolveplus.qspmobile.schedule.viewmodel.ScheduleViewModel
import com.quicksolveplus.utils.*

/**
 * 30/03/23.
 *
 * @author hardkgosai.
 */
class ScheduleActivity : Base() {

    private lateinit var binding: ActivityScheduleBinding
    private val viewModel: ScheduleViewModel by viewModels()

    private var fragment: Fragment? = null
    private var client: ClientsItem? = null
    private var employee: EmployeesItem? = null
    private var from: String = ""
    private var scheduleScreenFrom: String = ""
    private val drawerItems = ArrayList<String>()

    companion object {
        var isClient = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getIntentData()
        initUI()
        setupDrawer()
        setObservers()
        loadMonthlyShiftFragment()
    }

    private fun getIntentData() {
        intent.extras?.run {
            if (containsKey(Constants.from)) {
                from = getString(Constants.from, "")
                isClient = from == Constants.client
                scheduleScreenFrom = getString(Constants.scheduleScreenFrom, "")

                if (isClient && containsKey(Constants.client_map)) { //from client

                    client = Gson().fromJson(
                        getString(Constants.client_map), object : TypeToken<ClientsItem?>() {}.type
                    )

                    binding.toolbar.tvTitle.text =
                        String.format("%s %s", client?.FirstName, client?.LastName)

                } else if (!isClient && containsKey(Constants.worker_map)) { //from worker

                    employee = Gson().fromJson(
                        getString(Constants.worker_map),
                        object : TypeToken<EmployeesItem?>() {}.type
                    )

                    binding.toolbar.tvTitle.text =
                        String.format("%s %s", employee?.FirstName, employee?.LastName)
                }
            }
        }
    }

    private fun initUI() {
        binding.toolbar.ivBack.setOnClickListener { finish() }
        binding.toolbar.ivMenu.setOnClickListener { openDrawer() }
        binding.toolbar.ivSearch.setOnClickListener { }
        binding.toolbar.ivAdd.setOnClickListener { }
        binding.toolbar.ivCalendar.setOnClickListener {
            if (fragment is WeeklyShiftFragment) loadMonthlyShiftFragment()
            else loadWeeklyShiftFragment()
        }

        binding.toolbar.ivCalendar.isVisible = true
        QSPermissions.run {
            binding.toolbar.ivAdd.isVisible =
                hasScheduleModificationCreatePermission() || hasScheduleModuleAdminPermission()
        }

        if (scheduleScreenFrom == Constants.scheduleScreenFromClientProfile || scheduleScreenFrom == Constants.scheduleScreenFromEmployeeProfile) {
            binding.toolbar.ivBack.isVisible = true
            binding.toolbar.ivMenu.isVisible = false
            binding.toolbar.ivSearch.isVisible = false
        } else {
            binding.toolbar.ivBack.isVisible = false
            binding.toolbar.ivMenu.isVisible = true
            binding.toolbar.ivSearch.isVisible = true
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
            Api.getDashboard -> {
                if (success.data is Dashboard) {
                    openOTTrackerDialog(dashboard = success.data)
                }
            }
        }
    }

    private fun setupDrawer() {
        val drawerArray = resources.getStringArray(R.array.schedule_drawer_item)
        Preferences.instance?.user?.run {
            drawerArray.map {
                when (it) {
                    getString(R.string.str_cap_employee_view) -> {
                        QSPermissions.run {
                            if (isAdmin || hasEmployeeScheduleViewAccessPermission() || hasEmployeeScheduleViewReadPermission()) {
                                drawerItems.add(it)
                            }
                        }
                    }
                    getString(R.string.str_cap_client_view) -> {
                        QSPermissions.run {
                            if (isAdmin || hasClientScheduleViewAccessPermission() || hasClientScheduleViewReadPermission()) {
                                drawerItems.add(it)
                            }
                        }
                    }
                    getString(R.string.str_cap_ot_tracker) -> {
                        QSPermissions.run {
                            if (from == Constants.worker) {
                                if (isAdmin || hasEmployeeOTDashboardAccessPermission() || hasEmployeeOTDashboardReadPermission()) {
                                    drawerItems.add(it)
                                }
                            }
                        }
                    }
                    getString(R.string.str_cap_schedule_preference) -> {
                        from.run {
                            if (this == Constants.worker) {
                                drawerItems.add(it)
                            }
                        }
                    }
                    getString(R.string.str_cap_pos_dashboard), getString(R.string.str_cap_trained_employees) -> {
                        from.run {
                            if (this == Constants.client) {
                                drawerItems.add(it)
                            }
                        }
                    }
                    else -> {
                        drawerItems.add(it)
                    }
                }
            }
        }
    }

    @SuppressLint("InflateParams")
    private fun openDrawer() {
        var popupWindow: PopupWindow? = null
        popupWindow =
            getPopupWindow(drawerItems, OnItemClickListener { parent, view, position, id ->
                when (drawerItems[position]) {
                    getString(R.string.str_cap_home) -> {
                        setResult(RESULT_OK)
                        finish()
                    }
                    getString(R.string.str_cap_employee_view) -> {
                        setResult(RESULT_OK)
                        openActivity(EmployeeScheduleActivity::class.java)
                        finish()
                    }
                    getString(R.string.str_cap_client_view) -> {
                        setResult(RESULT_OK)
                        openActivity(ClientScheduleActivity::class.java)
                        finish()
                    }
                    getString(R.string.str_cap_ot_tracker) -> {
                        requestDashboard()
                    }
                    getString(R.string.str_cap_schedule_preference) -> {
                        drawerItems[position] = getString(R.string.str_cap_schedule_view)
                        loadWeeklyShiftFragment()
                    }
                    getString(R.string.str_cap_schedule_view) -> {
                        drawerItems[position] = getString(R.string.str_cap_schedule_preference)
                        loadMonthlyShiftFragment()
                    }
                    getString(R.string.str_cap_pos_dashboard) -> {
                        navigateToPOSDashboard()
                    }
                    getString(R.string.str_cap_trained_employees) -> {
                        openActivity(TrainedEmployeesActivity::class.java) {
                            putString(Constants.client_map, Gson().toJson(client))
                        }
                    }
                    getString(R.string.str_cap_log_off) -> {
                        (application as QSMobile).logout(this)
                    }
                    else -> {

                    }
                }

                if (popupWindow?.isShowing == true) {
                    popupWindow?.dismiss()
                }
            })
        popupWindow.showAsDropDown(binding.toolbar.ivMenu, 1, 0)
    }

    private fun loadMonthlyShiftFragment(fromPreferences: Boolean = false) {
        fragment = MonthlyShiftFragment(
            isClient = isClient,
            fromPreferences = fromPreferences,
            client = client,
            employee = employee
        )
        replaceFragment(fragment as MonthlyShiftFragment)
    }

    private fun loadWeeklyShiftFragment(fromPreferences: Boolean = false) {
        fragment = WeeklyShiftFragment(
            isClient = isClient,
            fromPreferences = fromPreferences,
            client = client,
            employee = employee
        )
        replaceFragment(fragment as WeeklyShiftFragment)
    }

    private fun navigateToPOSDashboard() {
        QSPermissions.run {
            if (isAdmin || hasClientPOSIHSSDashboardAccessPermission()) {
                if (client?.ClientServiceTypes?.isNotEmpty() == true) {
                    openActivity(POSViewActivity::class.java) {
                        putString(Constants.client_map, Gson().toJson(client))
                    }
                } else {
                    showAlertDialog(
                        context = this@ScheduleActivity,
                        message = getString(R.string.str_no_pos_service_type_available),
                        positiveButtonText = getString(R.string.str_ok)
                    )
                }
            } else {
                showAlertDialog(
                    context = this@ScheduleActivity,
                    message = getString(R.string.str_no_permission_for_posTracking),
                    positiveButtonText = getString(R.string.str_ok)
                )
            }
        }
    }

    private fun requestDashboard() {
        employee?.run {
            val scheduleDate = QSCalendar.formatDate(
                QSCalendar.calendarDate,
                QSCalendar.DateFormats.DDMMYYYY.label,
                QSCalendar.DateFormats.MMDDYYYY.label
            )
            val body =
                RequestParameters.forDashboard(staffID = UsersUID, scheduleDate = scheduleDate)
            viewModel.getDashboard(body)
        }
    }

    private fun openOTTrackerDialog(dashboard: Dashboard) {
        val items = arrayListOf<String>()
        dashboard.map {
            if (it.description.startsWith("OT")) {
                items.add("${getString(R.string.str_scheduled)} : ${it.hoursAvailable}")
                items.add("${getString(R.string.str_hrs_b4_cap)} : ${it.totalPercent2}")
            }
        }
        QSFilterItems(context = this, title = getString(R.string.str_weekly), items = items).show()
    }
}