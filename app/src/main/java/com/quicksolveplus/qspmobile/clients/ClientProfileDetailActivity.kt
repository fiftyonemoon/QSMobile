package com.quicksolveplus.qspmobile.clients

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ListView
import android.widget.PopupWindow
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.quicksolveplus.dialogs.QSAlert
import com.quicksolveplus.modifiers.Actifiers.onActivityBackPressed
import com.quicksolveplus.modifiers.Actifiers.openActivity
import com.quicksolveplus.modifiers.Actifiers.replaceFragment
import com.quicksolveplus.qsbase.LocationBase
import com.quicksolveplus.qspmobile.QSMobileActivity
import com.quicksolveplus.qspmobile.R
import com.quicksolveplus.qspmobile.adapter.DrawerAdapter
import com.quicksolveplus.qspmobile.clients.contacts.AddContactActivity
import com.quicksolveplus.qspmobile.clients.contacts.ContactsFragment
import com.quicksolveplus.qspmobile.clients.dr_visits.AddDoctorActivity
import com.quicksolveplus.qspmobile.clients.dr_visits.DrVisitsFragment
import com.quicksolveplus.qspmobile.clients.medication.AddMedicationActivity
import com.quicksolveplus.qspmobile.clients.medication.MedicationFragment
import com.quicksolveplus.qspmobile.clients.models.ClientsItem
import com.quicksolveplus.qspmobile.clients.profile.ClientProfileFragment
import com.quicksolveplus.qspmobile.databinding.ActivityClientProfileDetailBinding
import com.quicksolveplus.utils.Constants
import com.quicksolveplus.utils.Preferences
import com.quicksolveplus.utils.QSPermissions
import com.quicksolveplus.utils.canAccessLocation

class ClientProfileDetailActivity : LocationBase() {

    lateinit var binding: ActivityClientProfileDetailBinding
    private var popupWindow: PopupWindow? = null
    private val menuItems: ArrayList<String> = arrayListOf()
    private var client: ClientsItem? = null
    private var clientData = ""
    private var activeFragment: Fragment? = null

    override fun getLocation(mLastLocation: Location?) {
        if (canAccessLocation(this@ClientProfileDetailActivity)) {
            Preferences.instance?.latitude = mLastLocation?.latitude.toString()
            Preferences.instance?.longitude = mLastLocation?.longitude.toString()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClientProfileDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
        initBackPressed()
        initMenu()
    }

    private fun initBackPressed() {
        onActivityBackPressed {
            if (activeFragment is ClientProfileFragment) {
                if ((activeFragment as ClientProfileFragment).isEdited) {
                    QSAlert(
                        context = this,
                        title = resources.getString(R.string.str_save),
                        message = resources.getString(R.string.str_do_you_wish_save),
                        positiveButtonText = getString(R.string.action_yes),
                        negativeButtonText = getString(R.string.action_no)
                    ) { isPositive ->
                        if (isPositive) {
                            (activeFragment as ClientProfileFragment).saveData()
                        } else {
                            (activeFragment as ClientProfileFragment).isEdited = false
                            finish()
                        }

                    }.show()
                } else {
                    finish()
                }

            }
        }
    }

    private fun initUI() {
        if (intent.hasExtra(Constants.clientData)) {
            val gson = Gson()
            clientData = intent.getStringExtra(Constants.clientData).toString()
            client = gson.fromJson(
                clientData,
                ClientsItem::class.java
            )
        }
        activeFragment = ClientProfileFragment.newInstance(clientData)
        activeFragment?.let {
            replaceFragment(it)
        }

        setToolbar()
        binding.toolbar.ivMenu.setOnClickListener { openDrawer() }
    }

    private fun initMenu() {
        val drawerItem = resources.getStringArray(R.array.client_drawer_item)
        for (data in drawerItem) {
            if (data == resources.getString(R.string.str_cap_contacts)) {
                if (QSPermissions.hasClientModuleAdminPermission() || QSPermissions.hasPermissionClientContactsMaintenanceAccess()) {
                    menuItems.add(data)
                }
            } else if (data == resources.getString(R.string.str_cap_doctors_visit)) {
                if (QSPermissions.hasClientModuleAdminPermission() || QSPermissions.hasPermissionClientDoctorVisitMaintenanceAccess()) {
                    menuItems.add(data)
                }
            } else if (data == resources.getString(R.string.str_cap_rxs)) {
                if (QSPermissions.hasClientModuleAdminPermission() || QSPermissions.hasPermissionClientMedRxMaintenanceAccess()) {
                    menuItems.add(data)
                }
            } else if (data == resources.getString(R.string.str_cap_allergies)) {
                if (QSPermissions.hasClientModuleAdminPermission() || QSPermissions.hasPermissionClientAllergiesMaintenanceAccess()) {
                    menuItems.add(data)
                }
            } else if (data == resources.getString(R.string.str_cap_ihss_noa_pos)) {
                if (QSPermissions.hasClientModuleAdminPermission() || QSPermissions.hasPermissionClientPOSMaintenanceAccess()) {
                    menuItems.add(data)
                }
            } else if (data == resources.getString(R.string.str_cap_isp_objectives)) {
                if (QSPermissions.hasClientModuleAdminPermission() || QSPermissions.hasPermissionClientISPMaintenanceAccess()) {
                    menuItems.add(data)
                }
            } else if (data == resources.getString(R.string.str_cap_eap)) {
                if (QSPermissions.hasClientModuleAdminPermission() || QSPermissions.hasPermissionClientEAPMaintenanceAccess()) {
                    menuItems.add(data)
                }
            } else if (data == resources.getString(R.string.str_cap_fir)) {
                if (QSPermissions.hasClientModuleAdminPermission() || QSPermissions.hasPermissionClientFIRMaintenanceAccess()) {
                    menuItems.add(data)
                }
            } else if (data == resources.getString(R.string.str_cap_connect_form)) {
                if (QSPermissions.hasClientModuleAdminPermission() || QSPermissions.hasPermissionClientConnectMaintenanceAccess()) {
                    menuItems.add(data)
                }
            } else if (data == resources.getString(R.string.str_cap_qa)) {
                if (QSPermissions.hasClientModuleAdminPermission() || QSPermissions.hasPermissionClientQualityAssuranceMaintenanceAccess()) {
                    menuItems.add(data)
                }
            } else if (data == resources.getString(R.string.str_cap_documents)) {
                if (QSPermissions.hasClientModuleAdminPermission() || QSPermissions.hasPermissionClientDocumentMaintenanceAccess()) {
                    menuItems.add(data)
                }
            } else if (data == resources.getString(R.string.str_cap_training_guide)) {
                if (QSPermissions.hasClientModuleAdminPermission() || QSPermissions.hasPermissionClientTrainingGuideMaintenanceAccess()) {
                    menuItems.add(data)
                }
            } else {
                menuItems.add(data)
            }
        }
    }

    private fun openDrawer() {
        popupWindow =
            navigationDrawerWindow(this@ClientProfileDetailActivity, menuItems)
        popupWindow?.showAsDropDown(binding.toolbar.ivMenu, 1, 0)
    }

    @SuppressLint("InflateParams")
    private fun navigationDrawerWindow(
        activity: Activity,
        DrawerItems: ArrayList<String>
    ): PopupWindow {
        val drawerAdapter = DrawerAdapter(activity.baseContext, DrawerItems)
        val popupWindow = PopupWindow(this)
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = inflater.inflate(R.layout.drawer_layout, null)

        val drawer = view.findViewById<ListView>(R.id.lv_drawer)
        drawer.adapter = drawerAdapter

        popupWindow.isFocusable = true
        popupWindow.width = Toolbar.LayoutParams.WRAP_CONTENT
        popupWindow.height = Toolbar.LayoutParams.WRAP_CONTENT
        popupWindow.setBackgroundDrawable(
            ContextCompat.getDrawable(
                activity,
                R.drawable.rectangle_white_with_border
            )
        )
        popupWindow.contentView = view

        drawer.setOnItemClickListener { parent, view, position, id ->
            when (menuItems[position]) {
                getString(R.string.str_cap_home) -> {
                    finish()
                    openActivity(QSMobileActivity::class.java)
                }
                getString(R.string.str_cap_profile) -> {
                    ClientProfileFragment.newInstance(clientData).let {
                        activeFragment = it
                        replaceFragment(it)
                    }
                }
                getString(R.string.str_cap_contacts) -> {
                    ContactsFragment.newInstance(clientData).let {
                        activeFragment = it
                        replaceFragment(it)
                    }
                }
                getString(R.string.str_cap_doctors_visit) -> {
                    DrVisitsFragment.newInstance(clientData).let {
                        activeFragment = it
                        replaceFragment(it)
                    }
                }
                resources.getString(R.string.str_cap_rxs) -> {
                    MedicationFragment.newInstance(clientData).let {
                        activeFragment = it
                        replaceFragment(it)
                    }
                }
                getString(R.string.str_cap_change_client) -> {
                    finish()
                }
            }
            setToolbar()
            popupWindow.dismiss()
        }
        return popupWindow
    }

    private fun setToolbar() {
        if (activeFragment is ClientProfileFragment) {
            binding.apply {
                toolbar.tvSave.isGone = true
                toolbar.ivBack.isGone = true
                toolbar.ivAdd.isGone = true
                toolbar.ivSearch.isVisible = true
                if (QSPermissions.hasPermissionClientInfoMaintenanceUpdate() || QSPermissions.hasClientModuleAdminPermission()) {
                    toolbar.ivEdit.isVisible = true
                } else {
                    toolbar.ivEdit.isGone = true
                }
                toolbar.ivMenu.isVisible = true
                toolbar.tvTitle.text = client?.FirstName.plus(" ").plus(client?.LastName)
                toolbar.ivSearch.setOnClickListener {
                    finish()
                }
                toolbar.ivEdit.setOnClickListener {
                    toolbar.ivClientSave.isVisible = true
                    toolbar.ivEdit.isGone = true
                    (activeFragment as ClientProfileFragment).makeScreenEditable()
                }
                toolbar.ivClientSave.setOnClickListener {
                    toolbar.ivClientSave.isGone = true
                    toolbar.ivEdit.isVisible = true
                    (activeFragment as ClientProfileFragment).saveData()
                    (activeFragment as ClientProfileFragment).makeScreenReadOnly()
                    (activeFragment as ClientProfileFragment).isEdited = false
                }
            }
        } else if (activeFragment is ContactsFragment) {
            binding.apply {
                toolbar.ivEdit.isGone = true
                if (QSPermissions.hasPermissionClientContactsMaintenanceCreate() || QSPermissions.hasClientModuleAdminPermission()) {
                    toolbar.ivAdd.isVisible = true
                } else {
                    toolbar.ivAdd.isGone = false
                }
                toolbar.tvTitle.text = getString(R.string.str_cap_contacts)
                toolbar.ivAdd.setOnClickListener {
                    if (QSPermissions.hasPermissionClientContactsMaintenanceCreate() || QSPermissions.hasClientModuleAdminPermission()) {
                        val intent: Intent =
                            Intent(this@ClientProfileDetailActivity, AddContactActivity::class.java)
                        intent.putExtra(Constants.clientUID, client?.ClientUID)
                        startActivity(intent)
                    }
                }
            }
        } else if (activeFragment is DrVisitsFragment) {
            binding.apply {
                toolbar.ivEdit.isGone = true
                if (QSPermissions.hasClientModuleAdminPermission() || QSPermissions.hasPermissionClientDoctorVisitMaintenanceCreate()) {
                    toolbar.ivAdd.isVisible = true
                } else {
                    toolbar.ivAdd.isGone = true
                }
                toolbar.tvTitle.text = getString(R.string.str_norm_doctor_apts)
                toolbar.ivAdd.setOnClickListener {
                    if (QSPermissions.hasPermissionClientDoctorVisitMaintenanceCreate() || QSPermissions.hasClientModuleAdminPermission()) {
                        val intent: Intent =
                            Intent(this@ClientProfileDetailActivity, AddDoctorActivity::class.java)
                        intent.putExtra(Constants.clientUID, client?.ClientUID)
                        startActivity(intent)
                    }
                }
            }
        } else if (activeFragment is MedicationFragment) {
            binding.apply {
                toolbar.ivEdit.isGone = true

                if (QSPermissions.hasClientModuleAdminPermission() || QSPermissions.hasPermissionClientMedRxMaintenanceCreate()) {
                    toolbar.ivAdd.isVisible = true
                } else {
                    toolbar.ivAdd.isGone = true
                }
                toolbar.tvTitle.text = getString(R.string.str_medications)
                toolbar.ivAdd.setOnClickListener {
                    if (QSPermissions.hasClientModuleAdminPermission() || QSPermissions.hasPermissionClientMedRxMaintenanceCreate()) {
                        val intent: Intent =
                            Intent(
                                this@ClientProfileDetailActivity,
                                AddMedicationActivity::class.java
                            )
                        intent.putExtra(Constants.clientUID, client?.ClientUID)
                        startActivity(intent)
                    }
                }
            }
        }
    }
}