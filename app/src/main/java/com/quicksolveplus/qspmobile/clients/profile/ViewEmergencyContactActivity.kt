package com.quicksolveplus.qspmobile.clients.profile

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatEditText
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
import com.quicksolveplus.qspmobile.clients.models.EmergencyContact
import com.quicksolveplus.qspmobile.clients.models.EmergencyContactItem
import com.quicksolveplus.qspmobile.clients.models.Relation
import com.quicksolveplus.qspmobile.clients.models.RelationItem
import com.quicksolveplus.qspmobile.clients.viewmodel.ClientsViewModel
import com.quicksolveplus.qspmobile.databinding.ActivityViewEmergContactBinding
import com.quicksolveplus.utils.*
import okhttp3.ResponseBody

class ViewEmergencyContactActivity : LocationBase(), TextWatcher {

    lateinit var binding: ActivityViewEmergContactBinding
    private var textWatcher: TextWatcher? = null
    private val viewModel: ClientsViewModel by viewModels()
    private var clientUId: Int = 0
    private var emergencyContact: EmergencyContactItem? = null
    private var stateShortName: Array<String> = emptyArray()
    private var stateFullName: Array<String> = emptyArray()
    private var relationData: ArrayList<RelationItem> = arrayListOf()
    var currentStateIndex = 0
    var currentRelationshipIndex = 0
    private var isNewContact: Boolean = false
    private var isEdited: Boolean = false

    override fun getLocation(mLastLocation: Location?) {
        if (canAccessLocation(this)) {
            Preferences.instance?.latitude = mLastLocation?.latitude.toString()
            Preferences.instance?.longitude = mLastLocation?.longitude.toString()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewEmergContactBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
        initBackPressed()
        setToolBar()
        getIntentData()
        initControls()
        makeScreenReadOnly()
        setObservers()
        getEmergencyContact()
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
                        if (isNewContact) {
                            addNewEmergencyContact()
                        } else {
                            updateEmergencyContact()
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

    private fun initUI() {
        textWatcher = this
        isEdited = false
        binding.toolbar.tvTitle.text = getString(R.string.str_emergency_contact)
        binding.toolbar.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.tvState.setOnClickListener {
            val stateList: ArrayList<String> = getDataForStateList(emergencyContact?.State)
            QSFilterItems(
                context = this@ViewEmergencyContactActivity,
                fromState = true,
                title = getString(R.string.str_select_state),
                items = stateList,
                selectedItemPosition = currentStateIndex
            ) { position ->
                currentStateIndex = position
                emergencyContact?.State = stateShortName[position]
                binding.tvState.text = stateShortName[position]
            }.show()
        }

        binding.etAddress.setOnClickListener {
            openMapAddress()
        }

        binding.ivLocator.setOnClickListener {
            openMapAddress()
        }

        binding.etHomePhone.setOnClickListener {
            openPhoneMessageDialog(binding.etHomePhone.text.toString())
        }
        binding.tvHomePhone.setOnClickListener {
            openPhoneMessageDialog(binding.etHomePhone.text.toString())
        }
        binding.etCellPhone.setOnClickListener {
            openPhoneMessageDialog(binding.etCellPhone.text.toString())
        }
        binding.tvCellPhone.setOnClickListener {
            openPhoneMessageDialog(binding.etCellPhone.text.toString())
        }
        binding.etWorkPhone.setOnClickListener {
            openPhoneMessageDialog(binding.etWorkPhone.text.toString())
        }
        binding.etWorkPhone.setOnClickListener {
            openPhoneMessageDialog(binding.etWorkPhone.text.toString())
        }
        binding.etFaxNumber.setOnClickListener {
            openPhoneMessageDialog(binding.etFaxNumber.text.toString())
        }
        binding.tvFaxNumber.setOnClickListener {
            openPhoneMessageDialog(binding.etFaxNumber.text.toString())
        }

        binding.tvEmail.setOnClickListener {
            sendEmail(
                this, binding.etEmail.text.toString()
            )
        }

        binding.tvRelation.setOnClickListener {
            if (relationData.isEmpty()) {
                getOptionListWithUID()
            } else {
                openRelationshipDialog(relationData)?.show()
            }
        }
    }

    private fun openRelationshipDialog(relationshipList: ArrayList<RelationItem>): Dialog? {
        val builder = AlertDialog.Builder(this@ViewEmergencyContactActivity)
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

    private fun openPhoneMessageDialog(phoneNumber: String) {
        val viewType = arrayListOf<String>(
            getString(R.string.str_call), getString(R.string.str_message)
        )

        QSFilterItems(
            context = this@ViewEmergencyContactActivity,
            title = resources.getString(R.string.call_phone_message),
            items = viewType
        ) { position ->
            when (position) {
                0 -> {
                    dialPhoneNumber(this@ViewEmergencyContactActivity, phoneNumber)
                }
                1 -> {
                    composeMessage(this@ViewEmergencyContactActivity, phoneNumber)
                }
            }

        }.show()
    }


    private fun openMapAddress() {
        val locationMap = LinkedHashMap<String, String?>()
        if (!emergencyContact?.Address.isNullOrEmpty() && !emergencyContact?.Address.equals("")) {
            locationMap[Constants.address] = emergencyContact?.Address
        }
        if (!emergencyContact?.City.isNullOrEmpty() && !emergencyContact?.City.equals("")) {
            locationMap[Constants.city] = emergencyContact?.City
        }
        if (!emergencyContact?.State.isNullOrEmpty() && !emergencyContact?.State.equals("")) {
            locationMap[Constants.state] = emergencyContact?.State
        }
        if (!emergencyContact?.Zip.isNullOrEmpty() && !emergencyContact?.Zip.equals("")) {
            locationMap[Constants.zip] = emergencyContact?.Zip
        }
        val location = createLocationAddressFromParameters(locationMap)
        openMapActivityFromAddress(this, location)
    }

    private fun setToolBar() {
        binding.toolbar.apply {
            if (isNewContact) {
                if (QSPermissions.hasPermissionClientInfoMaintenanceUpdate() || QSPermissions.hasClientModuleAdminPermission()) {
                    ivSave.isVisible = true
                } else {
                    ivSave.isGone = true
                }
                ivEditContact.isGone = true
            } else {
                ivSave.isGone = true
                if (QSPermissions.hasPermissionClientInfoMaintenanceUpdate() || QSPermissions.hasClientModuleAdminPermission()) {
                    ivEditContact.isVisible = true
                } else {
                    ivEditContact.isGone = true
                }
            }

            ivEditContact.setOnClickListener {
                makeScreenEditable()
                ivSave.isVisible = true
                ivEditContact.isGone = true
            }

            ivSave.setOnClickListener {
                isEdited = false
                ivSave.isGone = true
                ivEditContact.isVisible = true
                makeScreenReadOnly()
                if (isNewContact && (QSPermissions.hasPermissionClientInfoMaintenanceUpdate() || QSPermissions.hasClientModuleAdminPermission())) {
                    addNewEmergencyContact()
                } else {
                    if (QSPermissions.hasPermissionClientInfoMaintenanceUpdate() || QSPermissions.hasClientModuleAdminPermission()) {
                        updateEmergencyContact()
                    }
                }
            }
        }
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


    private fun getIntentData() {
        if (intent.hasExtra(Constants.clientUID)) {
            clientUId = intent.getIntExtra(Constants.clientUID, 0)
        }
    }

    private fun initControls() {
        binding.apply {
            etFirstName.addTextChangedListener(textWatcher)
            etLastName.addTextChangedListener(textWatcher)
            etAddress.addTextChangedListener(textWatcher)
            tvRelation.addTextChangedListener(textWatcher)
            etCity.addTextChangedListener(textWatcher)
            tvState.addTextChangedListener(textWatcher)
            etZip.addTextChangedListener(textWatcher)
            etHomePhone.addTextChangedListener(textWatcher)
            etCellPhone.addTextChangedListener(textWatcher)
            etWorkPhone.addTextChangedListener(textWatcher)
            etFaxNumber.addTextChangedListener(textWatcher)
            etEmail.addTextChangedListener(textWatcher)
        }
    }

    private fun makeScreenReadOnly() {
        binding.apply {
            etFirstName.keyListener = null
            etLastName.keyListener = null
            tvRelation.isEnabled = false
            etAddress.keyListener = null
            etCity.keyListener = null
            tvState.isEnabled = false
            etZip.keyListener = null
            etHomePhone.keyListener = null
            etCellPhone.keyListener = null
            etWorkPhone.keyListener = null
            etFaxNumber.keyListener = null
            etEmail.keyListener = null
        }
    }

    private fun makeScreenEditable() {
        binding.apply {
            etFirstName.keyListener =
                AppCompatEditText(this@ViewEmergencyContactActivity).keyListener
            etLastName.keyListener =
                AppCompatEditText(this@ViewEmergencyContactActivity).keyListener
            tvRelation.isEnabled = true
            etAddress.keyListener = AppCompatEditText(this@ViewEmergencyContactActivity).keyListener
            etCity.keyListener = AppCompatEditText(this@ViewEmergencyContactActivity).keyListener
            tvState.isEnabled = true
            etZip.keyListener = AppCompatEditText(this@ViewEmergencyContactActivity).keyListener
            etHomePhone.keyListener =
                AppCompatEditText(this@ViewEmergencyContactActivity).keyListener
            etCellPhone.keyListener =
                AppCompatEditText(this@ViewEmergencyContactActivity).keyListener
            etWorkPhone.keyListener =
                AppCompatEditText(this@ViewEmergencyContactActivity).keyListener
            etFaxNumber.keyListener =
                AppCompatEditText(this@ViewEmergencyContactActivity).keyListener
            etEmail.keyListener = AppCompatEditText(this@ViewEmergencyContactActivity).keyListener
        }
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

    private fun proceedFailure(it: ResponseStatus.Failed) {
        when (it.apiName) {
            Api.getEmergencyContact -> {
                isNewContact = true
                if (it.msg != getString(R.string.str_no_emrg_contact)) {
                    toast(context = this, message = it.msg)
                }
            }
        }
    }

    private fun proceedResponse(success: ResponseStatus.Success) {
        when (success.apiName) {
            Api.addNewEmergencyContact -> {
                isEdited = false
                QSSuccess(
                    this, getString(R.string.str_emergency_contact_saved_success)
                ) { ok ->
                    if (ok) {
                        finish()
                    }
                }.show()
            }
            Api.getEmergencyContact -> {
                if (success.data is EmergencyContact) {
                    isEdited = false
                    if (success.data.isNotEmpty()) {
                        emergencyContact = success.data[0]
                    }
                    processedData(success.data)
                    isNewContact = false
                }
            }

            Api.getOptionListWithUID -> {
                if (success.data is Relation) {
                    relationData = success.data
                    openRelationshipDialog(relationData)?.show()
                }
            }

            Api.updateEmergencyContact -> {
                if (success.data is ResponseBody) {
                    isEdited = false
                    QSSuccess(
                        this, getString(R.string.str_emergency_contact_saved_success)
                    ) { ok ->
                        if (ok) {
                            finish()
                        }
                    }.show()
                }
            }
        }
    }

    private fun getEmergencyContact() {
        val body = RequestParameters.forEmergencyContact(clientUId)
        viewModel.getEmergencyContact(body = body)
    }

    private fun getOptionListWithUID() {
        val body = RequestParameters.forOptionListWithUID(Constants.OptionListId)
        viewModel.getOptionListWithUID(body = body)
    }

    private fun updateEmergencyContact() {
        if (isValidFields()) {
            emergencyContact?.let {
                it.FirstName = binding.etFirstName.text.toString()
                it.LastName = binding.etLastName.text.toString()
                it.Relationship = binding.tvRelation.text.toString()
                it.Address = binding.etAddress.text.toString()
                it.City = binding.etCity.text.toString()
                it.Zip = binding.etZip.text.toString()
                it.HomePhone = binding.etHomePhone.text.toString()
                it.CellPhone = binding.etCellPhone.text.toString()
                it.WorkPhone = binding.etWorkPhone.text.toString()
                it.FaxNumber = binding.etFaxNumber.text.toString()
                it.Email = binding.etEmail.text.toString()
                it.State = binding.tvState.text.toString()
            }
            val jsonString = Gson().toJson(emergencyContact)
            val body: JsonObject = JsonParser().parse(jsonString).asJsonObject
            body.let {
                viewModel.updateEmergencyContact(it)
            }
        }
    }

    private fun addNewEmergencyContact() {
        if (isValidFields()) {
            val emergencyContactItem = EmergencyContactItem()
            emergencyContactItem.let {
                it.UID = 0
                it.ClientUID = clientUId
                it.FirstName = binding.etFirstName.text.toString()
                it.LastName = binding.etLastName.text.toString()
                it.Relationship = binding.tvRelation.text.toString()
                it.Address = binding.etAddress.text.toString()
                it.City = binding.etCity.text.toString()
                it.Zip = binding.etZip.text.toString()
                it.HomePhone = binding.etHomePhone.text.toString()
                it.CellPhone = binding.etCellPhone.text.toString()
                it.WorkPhone = binding.etWorkPhone.text.toString()
                it.FaxNumber = binding.etFaxNumber.text.toString()
                it.Email = binding.etEmail.text.toString()
                it.State = binding.tvState.text.toString()
            }
            val jsonString = Gson().toJson(emergencyContact)
            val body: JsonObject = JsonParser().parse(jsonString).asJsonObject
            body.let {
                viewModel.addNewEmergencyContact(it)
            }
        }
    }

    private fun isValidFields(): Boolean {
        Validation(context = this).run {
            return isFieldValid(
                value = binding.etFirstName.text.toString(),
                getString(R.string.error_MSG_ENTER_NAME)
            ) && isFieldValid(
                value = binding.etLastName.text.toString(),
                getString(R.string.error_MSG_ENTER_LAST_NAME)
            ) && phoneNumberValidation(
                this@ViewEmergencyContactActivity, binding.etHomePhone.text.toString()
            ) && phoneNumberValidation(
                this@ViewEmergencyContactActivity, binding.etCellPhone.text.toString()
            ) && phoneNumberValidation(
                this@ViewEmergencyContactActivity, binding.etWorkPhone.text.toString()
            ) && phoneNumberValidation(
                this@ViewEmergencyContactActivity, binding.etFaxNumber.text.toString()
            ) && isFieldValid(
                value = binding.etEmail.text.toString(),
                getString(R.string.reg_error_MSG_ENTER_VALID_MAIL)
            )
        }
    }

    private fun processedData(emergencyContact: EmergencyContact) {
        binding.apply {
            etFirstName.setText(emergencyContact[0].FirstName)
            etLastName.setText(emergencyContact[0].LastName)
            tvRelation.text = emergencyContact[0].Relationship
            etAddress.setText(emergencyContact[0].Address)
            etCity.setText(emergencyContact[0].City)
            tvState.text = emergencyContact[0].State
            etZip.setText(emergencyContact[0].Zip)
            etHomePhone.setText(removeDelimiters(emergencyContact[0].HomePhone))
            etCellPhone.setText(removeDelimiters(emergencyContact[0].CellPhone))
            etWorkPhone.setText(removeDelimiters(emergencyContact[0].WorkPhone))
            etFaxNumber.setText(removeDelimiters(emergencyContact[0].FaxNumber))
            etEmail.setText(emergencyContact[0].Email)
        }
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