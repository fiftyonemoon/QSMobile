package com.quicksolveplus.qspmobile.clients.contacts

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.quicksolveplus.api.Api
import com.quicksolveplus.api.RequestParameters
import com.quicksolveplus.api.ResponseStatus
import com.quicksolveplus.dialogs.QSAlert
import com.quicksolveplus.dialogs.QSFilterItems
import com.quicksolveplus.dialogs.QSSuccess
import com.quicksolveplus.modifiers.Actifiers.onActivityBackPressed
import com.quicksolveplus.qsbase.LocationBase
import com.quicksolveplus.qspmobile.R
import com.quicksolveplus.qspmobile.clients.contacts.model.ContactsItem
import com.quicksolveplus.qspmobile.clients.contacts.viewmodel.ContactsViewModel
import com.quicksolveplus.qspmobile.clients.models.Relation
import com.quicksolveplus.qspmobile.clients.models.RelationItem
import com.quicksolveplus.qspmobile.databinding.ActivityAddContactBinding
import com.quicksolveplus.utils.*

class AddContactActivity : LocationBase(), TextWatcher {

    lateinit var binding: ActivityAddContactBinding
    private var contact: ContactsItem? = null
    private var contactData = ""
    private var isEditMode: Boolean = false
    private var textWatcher: TextWatcher? = null
    private var clientUID = 0
    private var UID1 = 0
    private var contactType = 0
    private var optionListId = 0
    private var currentStateIndex = 0
    var currentRelationshipIndex = 0
    private val viewModel: ContactsViewModel by viewModels()
    private var stateShortName: Array<String> = emptyArray()
    private var stateFullName: Array<String> = emptyArray()
    private var isProfileReadOnly = true
    private var isEdited = false

    override fun getLocation(mLastLocation: Location?) {
        if (canAccessLocation(this)) {
            Preferences.instance?.latitude = mLastLocation?.latitude.toString()
            Preferences.instance?.longitude = mLastLocation?.longitude.toString()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddContactBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
        initBackPressed()
        initControls()
        setObservers()
    }

    private fun initUI() {
        textWatcher = this
        if (intent.hasExtra(Constants.contactData)) {
            val gson = Gson()
            contactData = intent.getStringExtra(Constants.contactData).toString()
            contact = gson.fromJson(
                contactData, ContactsItem::class.java
            )
        }
        if (intent.hasExtra(Constants.isEdit)) {
            isEditMode = intent.getBooleanExtra(Constants.isEdit, false)
        }
        if (intent.hasExtra(Constants.clientUID)) {
            clientUID = intent.getIntExtra(Constants.clientUID, 0)
        }
        setToolbar()

        binding.clContactType.setOnClickListener {
            openContactTypeListPopup(binding.tvContactType)
        }
        binding.clRelation.setOnClickListener {
            isEdited = true
            if (optionListId != 0) {
                getOptionList()
            } else {
                //open simple dialog
            }
        }
        binding.tvState.setOnClickListener {
            isEdited = true
            val stateList: ArrayList<String> = getDataForStateList(contact?.State)
            QSFilterItems(
                context = this@AddContactActivity,
                fromState = true,
                title = getString(R.string.str_select_state),
                items = stateList,
                selectedItemPosition = currentStateIndex
            ) { position ->
                currentStateIndex = position
                contact?.State = stateShortName[position]
                binding.tvState.text = stateShortName[position]
            }.show()
        }
        binding.tvCompState.setOnClickListener {
            isEdited = true
            val stateList: ArrayList<String> = getDataForStateList(contact?.State)
            QSFilterItems(
                context = this@AddContactActivity,
                fromState = true,
                title = getString(R.string.str_select_state),
                items = stateList,
                selectedItemPosition = currentStateIndex
            ) { position ->
                currentStateIndex = position
                contact?.State = stateShortName[position]
                binding.tvState.text = stateShortName[position]
            }.show()
        }

        binding.etAddress1.setOnClickListener {
            if (isProfileReadOnly && isEditMode) {
                contact?.Address1?.let { it1 -> openMapAddress(it1) }
            }
        }
        binding.etAddress2.setOnClickListener {
            if (isProfileReadOnly && isEditMode) {
                contact?.Address2?.let { it1 -> openMapAddress(it1) }
            }
        }
        binding.ivAddress1.setOnClickListener {
            contact?.Address1?.let { it1 -> openMapAddress(it1) }
        }

        binding.ivAddress2.setOnClickListener {
            contact?.Address2?.let { it1 -> openMapAddress(it1) }
        }

        binding.etCompAddress1.setOnClickListener {
            if (isProfileReadOnly && isEditMode) {
                contact?.CompanyAddress1?.let { it1 -> openCompanyMapAddress(it1) }
            }
        }

        binding.etCompAddress2.setOnClickListener {
            if (isProfileReadOnly && isEditMode) {
                contact?.CompanyAddress2?.let { it1 -> openCompanyMapAddress(it1) }
            }
        }

        binding.ivCompAddress1.setOnClickListener {
            contact?.CompanyAddress1?.let { it1 -> openMapAddress(it1) }
        }

        binding.ivCompAddress2.setOnClickListener {
            contact?.CompanyAddress2?.let { it1 -> openMapAddress(it1) }
        }

        binding.etHomePhone.setOnClickListener {
            if (isProfileReadOnly && isEditMode) {
                openPhoneMessageDialog(binding.etHomePhone.text.toString())
            }
        }
        binding.tvHomePhone.setOnClickListener {
            if (isProfileReadOnly && isEditMode) {
                openPhoneMessageDialog(binding.etHomePhone.text.toString())
            }
        }
        binding.etCellPhone.setOnClickListener {
            if (isProfileReadOnly && isEditMode) {
                openPhoneMessageDialog(binding.etCellPhone.text.toString())
            }
        }
        binding.tvCellPhone.setOnClickListener {
            if (isProfileReadOnly && isEditMode) {
                openPhoneMessageDialog(binding.etCellPhone.text.toString())
            }
        }
        binding.etWorkPhone.setOnClickListener {
            if (isProfileReadOnly && isEditMode) {
                openPhoneMessageDialog(binding.etWorkPhone.text.toString())
            }
        }
        binding.etWorkPhone.setOnClickListener {
            if (isProfileReadOnly && isEditMode) {
                openPhoneMessageDialog(binding.etWorkPhone.text.toString())
            }
        }
        binding.etFaxNumber.setOnClickListener {
            if (isProfileReadOnly && isEditMode) {
                openPhoneMessageDialog(binding.etFaxNumber.text.toString())
            }
        }
        binding.tvFaxNumber.setOnClickListener {
            if (isProfileReadOnly && isEditMode) {
                openPhoneMessageDialog(binding.etFaxNumber.text.toString())
            }
        }
        binding.etFaxCompNumber.setOnClickListener {
            if (isProfileReadOnly && isEditMode) {
                openPhoneMessageDialog(binding.etFaxCompNumber.text.toString())
            }
        }
        binding.tvFaxCompNumber.setOnClickListener {
            if (isProfileReadOnly && isEditMode) {
                openPhoneMessageDialog(binding.etFaxCompNumber.text.toString())
            }
        }
        binding.etOtherCompPhone.setOnClickListener {
            if (isProfileReadOnly && isEditMode) {
                openPhoneMessageDialog(binding.etOtherCompPhone.text.toString())
            }
        }
        binding.tvOtherCompPhone.setOnClickListener {
            if (isProfileReadOnly && isEditMode) {
                openPhoneMessageDialog(binding.etOtherCompPhone.text.toString())
            }
        }
        binding.tvEmail.setOnClickListener {
            if (isProfileReadOnly && isEditMode) {
                sendEmail(
                    this, binding.etEmail.text.toString()
                )
            }
        }
        binding.tvCompEmail.setOnClickListener {
            if (isProfileReadOnly && isEditMode) {
                sendEmail(
                    this, binding.etEmail.text.toString()
                )
            }
        }

    }

    private fun initBackPressed() {
        onActivityBackPressed {
            if (isEdited) {
                QSAlert(
                    context = this,
                    title = resources.getString(R.string.str_save),
                    message = resources.getString(R.string.str_do_you_wish_save),
                    positiveButtonText = getString(R.string.action_yes),
                    negativeButtonText = getString(R.string.action_no)
                ) { isPositive ->
                    if (isPositive) {
                        if (isValidFields() && ((QSPermissions.hasClientModuleAdminPermission() || QSPermissions.hasPermissionClientContactsMaintenanceUpdate() || QSPermissions.hasPermissionClientContactsMaintenanceCreate()))) {
                            addNewClientContact()
                        }
                    } else {
                        isEdited = false
                        finish()
                    }
                }.show()
            } else {
                finish()
            }
        }

    }

    private fun openPhoneMessageDialog(phoneNumber: String) {
        val viewType = arrayListOf(
            getString(R.string.str_call), getString(R.string.str_message)
        )

        QSFilterItems(
            context = this@AddContactActivity,
            title = getString(R.string.call_phone_message),
            itemsTextGravity = Gravity.CENTER,
            items = viewType
        ) { position ->
            when (position) {
                0 -> {
                    dialPhoneNumber(this@AddContactActivity, phoneNumber)
                }
                1 -> {
                    composeMessage(this@AddContactActivity, phoneNumber)
                }
            }

        }.show()
    }

    private fun openMapAddress(address: String) {
        val locationMap = LinkedHashMap<String, String?>()
        if (address.isNotEmpty() && address != "") {
            locationMap[Constants.address] = address
        }
        if (!contact?.City.isNullOrEmpty() && !contact?.City.equals("")) {
            locationMap[Constants.city] = contact?.City
        }
        if (!contact?.State.isNullOrEmpty() && !contact?.State.equals("")) {
            locationMap[Constants.state] = contact?.State
        }
        if (!contact?.Zip.isNullOrEmpty() && !contact?.Zip.equals("")) {
            locationMap[Constants.zip] = contact?.Zip
        }
        val location = createLocationAddressFromParameters(locationMap)
        openMapActivityFromAddress(this, location)
    }

    private fun openCompanyMapAddress(address: String) {
        val locationMap = LinkedHashMap<String, String?>()
        if (address.isNotEmpty() && address != "") {
            locationMap[Constants.address] = address
        }
        if (!contact?.CompanyCity.isNullOrEmpty() && !contact?.CompanyCity.equals("")) {
            locationMap[Constants.city] = contact?.CompanyCity
        }
        if (!contact?.CompanyState.isNullOrEmpty() && !contact?.CompanyState.equals("")) {
            locationMap[Constants.state] = contact?.CompanyState
        }
        if (!contact?.CompanyZip.isNullOrEmpty() && !contact?.CompanyZip.equals("")) {
            locationMap[Constants.zip] = contact?.CompanyZip
        }
        val location = createLocationAddressFromParameters(locationMap)
        openMapActivityFromAddress(this, location)
    }

    private fun getDataForStateList(savedState: String?): ArrayList<String> {
        val stateList = ArrayList<HashMap<String, String>>()
        stateShortName = resources.getStringArray(R.array.state_short_name)
        stateFullName = resources.getStringArray(R.array.state_full_name)
        for (i in stateShortName.indices) {
            val state = HashMap<String, String>()
            state[stateShortName[i]] = stateFullName[i]
            stateList.add(state)
        }

        val stringArrayList = ArrayList<String>()
        val count = stateList.size
        for (i in 0 until count) {
            val state = stateList[i]
            val key = state.keys.toList()[0]
            val stateName = stateList[i][key] + " - " + key
            stringArrayList.add(stateName)
            if (key == savedState) {
                currentStateIndex = i
            }
        }
        return stringArrayList
    }

    private fun setObservers() {
        viewModel.responseStatus().observe(this) {
            when (it) {
                is ResponseStatus.Running -> {
                    showQSProgress(this)
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
            Api.getOptionListWithUID -> {
                if (success.data is Relation) {
                    openRelationshipDialog(success.data)?.show()
                }
            }
            Api.addNewClientContact -> {
                QSSuccess(
                    this, getString(R.string.str_contact_saved_success)
                ) { ok ->
                    if (ok) {
                        finish()
                    }
                }.show()
            }
            Api.updateClientContact -> {
                QSSuccess(
                    this, getString(R.string.str_contact_updated_success)
                ) { ok ->
                    if (ok) {
                        finish()
                    }
                }.show()
            }
        }
    }

    private fun openRelationshipDialog(relationshipList: ArrayList<RelationItem>): Dialog? {
        isEdited = true
        val builder = AlertDialog.Builder(this@AddContactActivity)
        val stringArrayList = java.util.ArrayList<String>()
        val count = relationshipList.size
        for (i in 0 until count) {
            val temp: String = relationshipList[i].Text
            stringArrayList.add(temp)
            if (temp == binding.tvRelation.text.toString()) {
                currentRelationshipIndex = i
            }
        }
        val relationshipNameArray = stringArrayList.toTypedArray()
        builder.setTitle(getString(R.string.str_select_relationship)).setSingleChoiceItems(
            relationshipNameArray, currentRelationshipIndex
        ) { dialog: DialogInterface, which: Int ->
            dialog.dismiss()
            val relationship: String = relationshipList[which].Text
            if (currentRelationshipIndex != which) {
                currentRelationshipIndex = which
                binding.tvRelation.text = relationship
            }
        }
        return builder.create()
    }

    private fun getOptionList() {
        val body = RequestParameters.forOptionListWithUID(optionListId)
        viewModel.getOptionListWithUID(body = body)
    }

    private fun setToolbar() {
        binding.toolbar.apply {
            if (QSPermissions.hasClientModuleAdminPermission() || QSPermissions.hasPermissionClientContactsMaintenanceUpdate() || QSPermissions.hasPermissionClientContactsMaintenanceCreate()) {
                if (isEditMode) {
                    tvTitle.text = getString(R.string.str_update_contact)
                    tvSave.isGone = true
                    if (!contact?.ContactTypeDesc.equals("Agency")) {
                        ivEditContact.isVisible = true
                    } else {
                        ivEditContact.isGone = true
                    }
                } else {
                    ivSave.isVisible = true
                    ivEditContact.isGone = true
                    tvTitle.text = getString(R.string.str_add_contact)
                }
                ivBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
            } else {
                ivSave.isVisible = true
            }

            ivSave.setOnClickListener {
                if (isValidFields() && ((QSPermissions.hasClientModuleAdminPermission() || QSPermissions.hasPermissionClientContactsMaintenanceUpdate() || QSPermissions.hasPermissionClientContactsMaintenanceCreate()))) {
                    addNewClientContact()
                    makeScreenReadOnly()
                    ivEditContact.isVisible = true
                    ivSave.isGone = true
                }
            }

            ivEditContact.setOnClickListener {
                ivSave.isVisible = true
                ivEditContact.isGone = true
                makeScreenEditable()
                isProfileReadOnly = false
            }
        }
    }

    private fun addNewClientContact() {
        val contactItem: ContactsItem? = if (isEditMode) {
            this.contact
        } else {
            ContactsItem()
        }
        contactItem?.run {
            ContactType = contactType
            val contactTypeDesc: String = if (binding.tvContactType.text.toString().equals(
                    getString(R.string.str_agency), ignoreCase = true
                )
            ) {
                "Agency"
            } else if (binding.tvContactType.text.toString().equals(
                    getString(R.string.str_important), ignoreCase = true
                )
            ) {
                "Important"
            } else if (binding.tvContactType.text.toString().equals(
                    getString(R.string.str_medical), ignoreCase = true
                )
            ) {
                "Medical"
            } else {
                binding.tvContactType.text.toString()
            }
            ContactTypeDesc = contactTypeDesc
            ClientUID = clientUID
            Relationship = binding.tvRelation.text.toString()
            FirstName = binding.etFirstName.text.toString()
            LastName = binding.etLastName.text.toString()
            MiddleName = binding.etMiddleName.text.toString()
            Address1 = binding.etAddress1.text.toString()
            Address2 = binding.etAddress2.text.toString()
            City = binding.etCity.text.toString()
            Zip = binding.etZip.text.toString()
            HomePhone = binding.etHomePhone.text.toString()
            CellPhone = binding.etCellPhone.text.toString()
            WorkPhone = binding.etWorkPhone.text.toString()
            FaxNumber = binding.etFaxNumber.text.toString()
            Email = binding.etEmail.text.toString()
            State = binding.tvState.text.toString()
            CompanyName = binding.etCompany.text.toString()
            CompanyAddress1 = binding.etCompAddress1.text.toString()
            CompanyAddress2 = binding.etCompAddress2.text.toString()
            CompanyCity = binding.etCompCity.text.toString()
            CompanyZip = binding.etCompZip.text.toString()
            CompanyState = binding.tvCompState.text.toString()
            CompanyPhone = binding.etOtherCompPhone.text.toString()
            CompanyFaxNumber = binding.etFaxCompNumber.text.toString()
            if (UID1 != 0) {
                UID = UID1
            }
            val jsonString = Gson().toJson(contactItem)
            val body: JsonObject = JsonParser().parse(jsonString).asJsonObject
            body.let {
                if (isEditMode && (QSPermissions.hasClientModuleAdminPermission() || QSPermissions.hasPermissionClientContactsMaintenanceUpdate())) {
                    viewModel.updateClientContact(body)

                } else if (QSPermissions.hasClientModuleAdminPermission() || QSPermissions.hasPermissionClientContactsMaintenanceCreate()) {
                    viewModel.addNewClientContact(body)
                }
            }
        }
    }

    private fun initControls() {
        binding.apply {
            etFirstName.addTextChangedListener(textWatcher)
            etLastName.addTextChangedListener(textWatcher)
            etMiddleName.addTextChangedListener(textWatcher)
            etAddress1.addTextChangedListener(textWatcher)
            etAddress2.addTextChangedListener(textWatcher)
            etCity.addTextChangedListener(textWatcher)
            etZip.addTextChangedListener(textWatcher)
            etHomePhone.addTextChangedListener(textWatcher)
            etCellPhone.addTextChangedListener(textWatcher)
            etWorkPhone.addTextChangedListener(textWatcher)
            etFaxNumber.addTextChangedListener(textWatcher)
            etEmail.addTextChangedListener(textWatcher)
            etCompany.addTextChangedListener(textWatcher)
            etCompAddress1.addTextChangedListener(textWatcher)
            etCompAddress2.addTextChangedListener(textWatcher)
            etCompCity.addTextChangedListener(textWatcher)
            etCompZip.addTextChangedListener(textWatcher)
            etWorkPhone.addTextChangedListener(textWatcher)
            etFaxCompNumber.addTextChangedListener(textWatcher)
            etCompEmail.addTextChangedListener(textWatcher)
        }
        if (isEditMode) {
            if (QSPermissions.hasClientModuleAdminPermission() || QSPermissions.hasPermissionClientContactsMaintenanceAccess() || QSPermissions.hasPermissionClientContactsMaintenanceRead() || QSPermissions.hasPermissionClientContactsMaintenanceUpdate()) {
                setContactData()
                makeScreenReadOnly()
            }
        }
    }

    private fun makeScreenReadOnly() {
        binding.apply {
            clContactType.isEnabled = false
            clRelation.isEnabled = false
            etFirstName.keyListener = null
            etMiddleName.keyListener = null
            etLastName.keyListener = null
            etAddress1.keyListener = null
            etAddress2.keyListener = null
            etCity.keyListener = null
            tvState.isEnabled = false
            etZip.keyListener = null
            etHomePhone.keyListener = null
            etCellPhone.keyListener = null
            etWorkPhone.keyListener = null
            etFaxNumber.keyListener = null
            etEmail.keyListener = null

            clCompany.isEnabled = false
            etCompAddress1.keyListener = null
            etCompAddress2.keyListener = null
            etCompCity.keyListener = null
            tvCompState.isEnabled = false
            etCompZip.keyListener = null
            etOtherCompPhone.keyListener = null
            etFaxCompNumber.keyListener = null
            etCompEmail.keyListener = null

        }
    }

    private fun makeScreenEditable() {
        binding.apply {
            clContactType.isEnabled = true
            clRelation.isEnabled = true
            etFirstName.keyListener = AppCompatEditText(this@AddContactActivity).keyListener
            etLastName.keyListener = AppCompatEditText(this@AddContactActivity).keyListener
            etMiddleName.keyListener = AppCompatEditText(this@AddContactActivity).keyListener
            etAddress1.keyListener = AppCompatEditText(this@AddContactActivity).keyListener
            etAddress2.keyListener = AppCompatEditText(this@AddContactActivity).keyListener
            etCity.keyListener = AppCompatEditText(this@AddContactActivity).keyListener
            tvState.isEnabled = true
            etZip.keyListener = AppCompatEditText(this@AddContactActivity).keyListener
            etHomePhone.keyListener = AppCompatEditText(this@AddContactActivity).keyListener
            etCellPhone.keyListener = AppCompatEditText(this@AddContactActivity).keyListener
            etWorkPhone.keyListener = AppCompatEditText(this@AddContactActivity).keyListener
            etFaxNumber.keyListener = AppCompatEditText(this@AddContactActivity).keyListener
            etEmail.keyListener = AppCompatEditText(this@AddContactActivity).keyListener

            clCompany.isEnabled = true
            tvCompState.isEnabled = true
            etCompAddress1.keyListener = AppCompatEditText(this@AddContactActivity).keyListener
            etCompAddress2.keyListener = AppCompatEditText(this@AddContactActivity).keyListener
            etCompCity.keyListener = AppCompatEditText(this@AddContactActivity).keyListener
            etCompZip.keyListener = AppCompatEditText(this@AddContactActivity).keyListener
            etOtherCompPhone.keyListener = AppCompatEditText(this@AddContactActivity).keyListener
            etFaxCompNumber.keyListener = AppCompatEditText(this@AddContactActivity).keyListener
            etCompEmail.keyListener = AppCompatEditText(this@AddContactActivity).keyListener
        }
    }

    private fun setContactData() {
        contact?.run {
            UID1 = UID
            contactType = ContactType
            var contactType: String = ""
            when (ContactTypeDesc) {
                "Agency" -> {
                    optionListId = 68
                    contactType = getString(R.string.str_agency);
                    readOnlyWhenAgencyType()
                }
                "Important" -> {
                    optionListId = 69
                    contactType = getString(R.string.str_important)
                }
                "Medical" -> {
                    optionListId = 70
                    contactType = getString(R.string.str_medical)
                }
                else -> {
                    contactType = ContactTypeDesc
                }
            }
            binding.tvContactType.text = contactType
            binding.tvRelation.text = Relationship
            binding.etFirstName.setText(FirstName)
            binding.etLastName.setText(LastName)
            binding.etMiddleName.setText(MiddleName)
            binding.etAddress1.setText(Address1)
            binding.etAddress2.setText(Address2)
            binding.etCity.setText(City)
            binding.tvState.text = State
            binding.etZip.setText(Zip)
            binding.etHomePhone.setText(removeDelimiters(HomePhone))
            binding.etCellPhone.setText(removeDelimiters(CellPhone))
            binding.etWorkPhone.setText(removeDelimiters(WorkPhone))
            binding.etFaxNumber.setText(removeDelimiters(FaxNumber))
            binding.etEmail.setText(Email)
            if (PrimaryEmployee) {
                binding.cbEmployee.isChecked = true
                binding.tvEmployee.text = getString(R.string.str_primary_employee)
            } else if (BackupEmployee) {
                binding.cbEmployee.isChecked = true
                binding.tvEmployee.text = getString(R.string.str_backup_employee)
            } else {
                binding.clCheckEmployee.isGone = true
            }
            binding.etCompany.setText(CompanyName)
            binding.etCompAddress1.setText(CompanyAddress1)
            binding.etCompAddress2.setText(CompanyAddress2)
            binding.etCompCity.setText(CompanyCity)
            binding.tvCompState.text = CompanyState
            binding.etCompZip.setText(CompanyZip)
            binding.etOtherCompPhone.setText(removeDelimiters(CompanyPhone))
            binding.etFaxCompNumber.setText(removeDelimiters(CompanyPhone))
            binding.etCompEmail.setText(CompanyEmail)
        }
    }

    private fun readOnlyWhenAgencyType() {
        binding.apply {
            clContactType.isEnabled = false
            clRelation.isEnabled = false
            etFirstName.keyListener = null
            etMiddleName.keyListener = null
            etLastName.keyListener = null
            clCheckEmployee.isVisible = true
            tvEmployee.isEnabled = false
            cbEmployee.isEnabled = false
            clAddress1.isGone = true
            clAddress2.isGone = true
            clCity.isGone = true
            clEmail.isGone = true
            tvBusiness.isGone = true
            clCompany.isGone = true
            clCompAddress1.isGone = true
            clCompAddress2.isGone = true
            clCompCity.isGone = true
            clOtherCompNumber.isGone = true
            clCompEmail.isGone = true
        }
    }

    private fun isValidFields(): Boolean {
        Validation(context = this).run {
            return isValidOptionId(
                optionListId, getString(R.string.str_please_select_contact_type)
            ) && isFieldValid(
                binding.tvRelation.text.toString(),
                getString(R.string.str_please_select_relationship)
            ) && isFieldValid(
                value = binding.etFirstName.text.toString(),
                getString(R.string.error_MSG_ENTER_NAME)
            ) && isFieldValid(
                value = binding.etLastName.text.toString(),
                getString(R.string.error_MSG_ENTER_LAST_NAME)
            ) && phoneNumberValidation(
                this@AddContactActivity, binding.etHomePhone.text.toString()
            ) && phoneNumberValidation(
                this@AddContactActivity, binding.etCellPhone.text.toString()
            ) && phoneNumberValidation(
                this@AddContactActivity, binding.etWorkPhone.text.toString()
            ) && phoneNumberValidation(
                this@AddContactActivity, binding.etFaxNumber.text.toString()
            )

                    && phoneNumberValidation(
                this@AddContactActivity, binding.etOtherCompPhone.text.toString()
            ) && phoneNumberValidation(
                this@AddContactActivity, binding.etFaxCompNumber.text.toString()
            )
        }
    }

    private fun openContactTypeListPopup(textView: AppCompatTextView) {
        isEdited = true
        contactType = 0
        var list: ArrayList<String> = arrayListOf()
        list.add(getString(R.string.str_important))
        list.add(getString(R.string.str_medical))
        QSFilterItems(
            context = this@AddContactActivity,
            items = list,
            title = getString(R.string.str_select_relationship)
        ) { position ->
            when (position) {
                0 -> {
                    contactType = 1
                    optionListId = 69
                    textView.text = list[0]
                }
                1 -> {
                    contactType = 2
                    optionListId = 70
                    textView.text = list[1]
                }
            }

        }.show()

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }

    override fun afterTextChanged(s: Editable?) {
        if (binding.toolbar.ivSave.isVisible) {
            isEdited = true
        }
    }
}