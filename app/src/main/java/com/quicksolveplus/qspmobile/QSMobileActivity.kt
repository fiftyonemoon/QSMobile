package com.quicksolveplus.qspmobile

import android.os.Bundle
import android.view.Gravity
import com.quicksolveplus.dialogs.QSFilterItems
import com.quicksolveplus.modifiers.Actifiers.openActivity
import com.quicksolveplus.qsbase.Base
import com.quicksolveplus.qspmobile.adapter.QSMobileAdapter
import com.quicksolveplus.qspmobile.clients.ClientsActivity
import com.quicksolveplus.qspmobile.databinding.ActivityQsmobileBinding
import com.quicksolveplus.qspmobile.employee.EmployeeActivity
import com.quicksolveplus.qspmobile.model.DrawerItemType
import com.quicksolveplus.qspmobile.model.MenuItem
import com.quicksolveplus.qspmobile.schedule.ClientScheduleActivity
import com.quicksolveplus.qspmobile.schedule.EmployeeScheduleActivity
import com.quicksolveplus.utils.QSPermissions
import com.quicksolveplus.utils.showAlertDialog

class QSMobileActivity : Base(), QSMobileAdapter.OnMenuItemClick {

    private lateinit var binding: ActivityQsmobileBinding
    private var qsMobileAdapter: QSMobileAdapter? = null
    private var menuItemList: ArrayList<MenuItem> = arrayListOf()
    private var schedulePermitItems: ArrayList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityQsmobileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
        setData()
        checkUserPermissions()
    }

    private fun initUI() {
        binding.ivBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun setData() {
        /*remaining to set permission*/
        menuItemList.add(
            MenuItem(
                R.drawable.ic_menu_clients,
                0,
                getString(R.string.str_clients),
                DrawerItemType.Clients
            )
        )
        menuItemList.add(
            MenuItem(
                R.drawable.ic_menu_employee,
                0,
                getString(R.string.str_employees),
                DrawerItemType.Employees
            )
        )
        menuItemList.add(
            MenuItem(
                R.drawable.ic_menu_schedule,
                0,
                getString(R.string.str_schedule),
                DrawerItemType.Schedule
            )
        )
        menuItemList.add(
            MenuItem(
                R.drawable.ic_menu_dashboard,
                0,
                getString(R.string.str_dashboard),
                DrawerItemType.Dashboard
            )
        )
        menuItemList.add(
            MenuItem(
                R.drawable.ic_menu_mytimesheets,
                0,
                getString(R.string.str_timesheets),
                DrawerItemType.MyTimeSheets
            )
        )
        menuItemList.add(
            MenuItem(
                R.drawable.ic_service_record,
                0,
                getString(R.string.str_service_record),
                DrawerItemType.ServiceRecord
            )
        )
        menuItemList.add(
            MenuItem(
                R.drawable.ic_menu_openshifts,
                0,
                getString(R.string.str_open_shifts),
                DrawerItemType.OpenShifts
            )
        )
        menuItemList.add(
            MenuItem(
                R.drawable.ic_service_record,
                0,
                getString(R.string.str_daily_service_notes),
                DrawerItemType.ServiceRecord
            )
        )
        setAdapter()
    }

    private fun checkUserPermissions() {
        val drawerItem = resources.getStringArray(R.array.schedule_view_drawer_item)
        drawerItem.map {
            when (it) {
                getString(R.string.str_client_view) -> {
                    QSPermissions.run {
                        if (hasClientModuleAdminPermission() || hasClientScheduleViewAccessPermission()) {
                            schedulePermitItems.add(it)
                        }
                    }
                }
                getString(R.string.str_employee_view) -> {
                    QSPermissions.run {
                        if (hasEmployeeModuleAdminPermission() || hasEmployeeScheduleViewAccessPermission()) {
                            schedulePermitItems.add(it)
                        }
                    }
                }
            }
        }
    }

    private fun setAdapter() {
        binding.apply {
            qsMobileAdapter = QSMobileAdapter(menuItemList, this@QSMobileActivity)
            rvMenu.setHasFixedSize(false)
            rvMenu.adapter = qsMobileAdapter
        }
    }

    override fun onMenuItemClick(menuItem: MenuItem) {
        when (menuItem.drawerItemType) {
            DrawerItemType.Clients -> {
                QSPermissions.run {
                    isAdmin = hasClientModuleAdminPermission()
                }
                openActivity(ClientsActivity::class.java)
            }
            DrawerItemType.Employees -> {
                QSPermissions.run {
                    isAdmin = hasEmployeeModuleAdminPermission()
                    if (isAdmin || hasEmployeeModuleAccessPermission()) {
                        openActivity(EmployeeActivity::class.java)
                    } else {
                        showAlertDialog(
                            context = this@QSMobileActivity,
                            message = getString(R.string.str_no_permission_for_employee),
                            positiveButtonText = getString(R.string.str_ok)
                        )
                    }
                }
            }
            DrawerItemType.Schedule -> {
                QSPermissions.run {
                    isAdmin = hasScheduleModuleAdminPermission()
                }
                if (schedulePermitItems.isNotEmpty()) {
                    openScheduleDialog()
                } else {
                    showAlertDialog(
                        context = this@QSMobileActivity,
                        message = getString(R.string.str_no_permission_for_schedule),
                        positiveButtonText = getString(R.string.str_ok)
                    )
                }
            }
            else -> {}
        }
    }

    private fun openScheduleDialog() {
        QSFilterItems(
            context = this@QSMobileActivity,
            title = getString(R.string.str_select_view),
            items = schedulePermitItems,
            itemsTextGravity = Gravity.CENTER
        ) { position ->
            when (schedulePermitItems[position]) {
                getString(R.string.str_client_view) -> {
                    openActivity(ClientScheduleActivity::class.java)
                }
                getString(R.string.str_employee_view) -> {
                    openActivity(EmployeeScheduleActivity::class.java)
                }
            }
        }.show()
    }
}