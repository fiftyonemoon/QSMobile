package com.quicksolveplus.qspmobile.clients.profile

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.quicksolveplus.modifiers.Glidifiers.loadGlide
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.quicksolveplus.api.Api
import com.quicksolveplus.api.ResponseStatus
import com.quicksolveplus.dialogs.QSFilterItems
import com.quicksolveplus.dialogs.QSImagePicker
import com.quicksolveplus.dialogs.QSImageView
import com.quicksolveplus.dialogs.QSSuccess
import com.quicksolveplus.modifiers.Actifiers.cameraIntent
import com.quicksolveplus.modifiers.Actifiers.imageIntent
import com.quicksolveplus.modifiers.Actifiers.registerActivityResultLauncher
import com.quicksolveplus.qspmobile.R
import com.quicksolveplus.qspmobile.clients.models.ClientsItem
import com.quicksolveplus.qspmobile.clients.viewmodel.ClientsViewModel
import com.quicksolveplus.qspmobile.databinding.FragmentClientProfileBinding
import com.quicksolveplus.utils.*
import okhttp3.ResponseBody
import java.io.File

class ClientProfileFragment : Fragment(), PermissionListener, TextWatcher {

    private lateinit var binding: FragmentClientProfileBinding
    private var clientDataString: String = ""
    private var client: ClientsItem? = null
    private var isProfileReadOnly = true
    var currentStateIndex = 0
    private val viewModel by lazy {
        ViewModelProvider(this)[ClientsViewModel::class.java]
    }
    private var stateShortName: Array<String> = emptyArray()
    private var stateFullName: Array<String> = emptyArray()
    private var textWatcher: TextWatcher? = null
    var isEdited = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            clientDataString = it.getString(Constants.clientData).toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentClientProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String) = ClientProfileFragment().apply {
            arguments = Bundle().apply {
                putString(Constants.clientData, param1)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iniUI()
        initControls()
        makeScreenReadOnly()
        getData()
        setData()
        setObservers()
    }

    private fun initControls() {
        if (QSPermissions.hasClientModuleAdminPermission() || QSPermissions.hasClientScheduleViewReadPermission() || QSPermissions.hasClientScheduleViewAccessPermission()) {
            binding.btnSchedule.isVisible = true
        } else {
            binding.btnSchedule.isGone = true
        }
        binding.apply {
            etFirstName.addTextChangedListener(textWatcher)
            etLastName.addTextChangedListener(textWatcher)
            etAddress.addTextChangedListener(textWatcher)
            etAddress2.addTextChangedListener(textWatcher)
            etCity.addTextChangedListener(textWatcher)
            etZip.addTextChangedListener(textWatcher)
            tvStateName.addTextChangedListener(textWatcher)
            etHomePhone.addTextChangedListener(textWatcher)
            etCellPhone.addTextChangedListener(textWatcher)
            etOtherPhone.addTextChangedListener(textWatcher)
            etFaxNumber.addTextChangedListener(textWatcher)
            etFaxNumber.addTextChangedListener(textWatcher)
            tvUci.addTextChangedListener(textWatcher)
        }
    }

    private fun getData() {
        val gson = Gson()
        client = gson.fromJson(
            clientDataString, ClientsItem::class.java
        )
    }

    private fun iniUI() {
        textWatcher = this
        binding.flProfile.setOnClickListener {
            onProfileClick()
        }

        binding.etAddress.setOnClickListener {
            if (isProfileReadOnly) {
                openMapAddress()
            }
        }

        binding.ivLocator.setOnClickListener {
            openMapAddress()
        }

        binding.tvState.setOnClickListener {
            val stateList: ArrayList<String> = getDataForStateList(client?.State)
            QSFilterItems(
                context = requireContext(),
                fromState = true,
                title = getString(R.string.str_select_state),
                items = stateList,
                selectedItemPosition = currentStateIndex
            ) { position ->
                currentStateIndex = position
                client?.State = stateShortName[position]
                binding.tvStateName.text = stateShortName[position]
            }.show()
        }

        binding.tvStateName.setOnClickListener {
            val stateList: ArrayList<String> = getDataForStateList(client?.State)
            QSFilterItems(
                context = requireContext(),
                fromState = true,
                title = getString(R.string.str_select_state),
                items = stateList,
                selectedItemPosition = currentStateIndex
            ) { position ->
                currentStateIndex = position
                client?.State = stateShortName[position]
                binding.tvStateName.text = stateShortName[position]
            }.show()
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
        binding.etOtherPhone.setOnClickListener {
            openPhoneMessageDialog(binding.etOtherPhone.text.toString())
        }
        binding.tvOtherPhone.setOnClickListener {
            openPhoneMessageDialog(binding.etOtherPhone.text.toString())
        }
        binding.etFaxNumber.setOnClickListener {
            openPhoneMessageDialog(binding.etFaxNumber.text.toString())
        }
        binding.tvFaxNumber.setOnClickListener {
            openPhoneMessageDialog(binding.etFaxNumber.text.toString())
        }

        binding.tvEmail.setOnClickListener {
            sendEmail(
                requireActivity(), binding.etEmail.text.toString()
            )
        }

        binding.tvBirthDate.setOnClickListener {
            openDatePickerDialog(
                requireActivity(), binding.tvBirthDate, binding.tvBirthDate.text.toString()
            )
        }

        binding.btnContact.setOnClickListener {
            val intent = Intent(requireContext(), ViewEmergencyContactActivity::class.java)
            intent.putExtra(Constants.clientUID, client?.ClientUID)
            requireActivity().startActivity(intent)
        }

    }

    private fun onProfileClick() {
        if (isProfileReadOnly) {
            val intent = Intent(requireContext(), QSImageView::class.java)
            intent.putExtra(Constants.clientProfilePic, client?.ClientProfilePic)
            startActivity(intent)
        } else {
            clientProfilePicUpload()
        }
    }

    private fun openPhoneMessageDialog(phoneNumber: String) {
        val viewType = arrayListOf<String>(
            requireActivity().resources.getString(R.string.str_call),
            requireActivity().resources.getString(R.string.str_message)
        )

        QSFilterItems(
            context = requireContext(),
            title = resources.getString(R.string.call_phone_message),
            items = viewType
        ) { position ->
            when (position) {
                0 -> {
                    dialPhoneNumber(requireActivity(), phoneNumber)
                }
                1 -> {
                    composeMessage(requireActivity(), phoneNumber)
                }
            }

        }.show()
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
        viewModel.responseStatus().observe(viewLifecycleOwner) {
            when (it) {
                is ResponseStatus.Running -> {
                    showQSProgress(requireActivity())
                }
                is ResponseStatus.Success -> {
                    proceedResponse(it)
                    dismissQSProgress()
                }
                is ResponseStatus.Failed -> {
                    toast(requireContext(), it.msg)
                    dismissQSProgress()
                }
                else -> {
                    toast(requireContext(), "Something goes wrong")
                    dismissQSProgress()
                }
            }
        }
    }

    private fun proceedResponse(success: ResponseStatus.Success) {
        when (success.apiName) {
            Api.updateClient -> {
                if (success.data is ResponseBody) {
                    isEdited = false
                    QSSuccess(
                        requireContext(),
                        requireContext().resources.getString(R.string.str_client_save_success)
                    ) { ok ->
                        if (ok) {
                            requireActivity().finish()
                        }
                    }.show()
                }
            }
        }
    }

    private fun openMapAddress() {
        val locationMap = LinkedHashMap<String, String?>()
        if (!client?.Address.isNullOrEmpty() && !client?.Address.equals("")) {
            locationMap[Constants.address] = client?.Address
        }
        if (!client?.Address2.isNullOrEmpty() && !client?.Address2.equals("")) {
            locationMap[Constants.address2] = client?.Address2
        }
        if (!client?.City.isNullOrEmpty() && !client?.City.equals("")) {
            locationMap[Constants.city] = client?.City
        }
        if (!client?.State.isNullOrEmpty() && !client?.State.equals("")) {
            locationMap[Constants.state] = client?.State
        }
        if (!client?.Zip.isNullOrEmpty() && !client?.Zip.equals("")) {
            locationMap[Constants.zip] = client?.Zip
        }
        val location = createLocationAddressFromParameters(locationMap)
        openMapActivityFromAddress(requireActivity(), location)
    }

    private fun clientProfilePicUpload() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                TedPermission.create().setPermissionListener(this)
                    .setRationaleMessage(R.string.camera_message)
                    .setDeniedMessage(R.string.denied_message)
                    .setGotoSettingButtonText(R.string.str_ok).setPermissions(
                        Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES
                    ).check()
            } else {
                TedPermission.create().setPermissionListener(this)
                    .setRationaleMessage(R.string.camera_message)
                    .setDeniedMessage(R.string.denied_message)
                    .setGotoSettingButtonText(R.string.str_ok).setPermissions(
                        Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE
                    ).check()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun makeScreenReadOnly() {
        binding.apply {
            etFirstName.isFocusable = false
            etFirstName.isEnabled = false

            etLastName.isFocusable = false
            etLastName.isEnabled = false

            etAddress.isFocusable = false
            etAddress.isEnabled = false

            etAddress2.isFocusable = false
            etAddress2.isEnabled = false

            etCity.isFocusable = false
            etCity.isEnabled = false

            tvStateName.isFocusable = false
            tvStateName.isEnabled = false

            tvState.isFocusable = false
            tvState.isEnabled = false

            etZip.isFocusable = false
            etZip.isEnabled = false

            etHomePhone.isFocusable = false
            etHomePhone.isClickable = true

            etCellPhone.isFocusable = false
            etCellPhone.isClickable = true

            etOtherPhone.isFocusable = false
            etOtherPhone.isClickable = true

            etFaxNumber.isFocusable = false
            etFaxNumber.isClickable = true

            etEmail.isFocusable = false
            etEmail.isEnabled = false

            tvBirthDate.isEnabled = false

            etUci.isFocusable = false
            etUci.isEnabled = false
        }
    }

    private fun setData() {
        binding.apply {
            etFirstName.setText(client?.FirstName)
            etLastName.setText(client?.LastName)
            etAddress.setText(client?.Address)
            etAddress2.setText(client?.Address2)
            etCity.setText(client?.City)
            etZip.setText(client?.Zip)
            tvStateName.text = client?.State

            if (client?.HomePhone?.isNotEmpty() == true) {
                etHomePhone.setText(removeDelimiters(client?.HomePhone!!))
            }
            if (client?.CellPhone?.isNotEmpty() == true) {
                etCellPhone.setText(removeDelimiters(client?.CellPhone!!))
            }
            if (client?.OtherPhone?.isNotEmpty() == true) {
                etOtherPhone.setText(removeDelimiters(client?.OtherPhone!!))
            }
            if (client?.FaxNumber?.isNotEmpty() == true) {
                etFaxNumber.setText(removeDelimiters(client?.FaxNumber!!))
            }
            etEmail.setText(client?.Email)
            tvBirthDate.text = client?.DOB
            etUci.setText(client?.UCI)
            if (client?.SSN?.isNotEmpty() == true) {
                if (QSPermissions.hasPermissionClientSSNMaintenanceRead()) {
                    tvSSN.text =
                        if (client?.SSN?.isNotEmpty() == true && client?.SSN?.length!! > 4) client?.SSN?.substring(
                            client?.SSN?.length!! - 4
                        ) else ""
                }
            }
        }
        isEdited = false
        setProfilePicture()
    }

    private fun setProfilePicture() {
        client?.let {
            val file = File(requireContext().cacheDir, it.ClientProfilePic)
            binding.ivProfile.loadGlide(
                file, ContextCompat.getDrawable(requireContext(), R.drawable.ic_avatar)
            )
        }
    }

    fun makeScreenEditable() {
        isProfileReadOnly = false
        binding.apply {
            binding.etFirstName.isEnabled = true
            etFirstName.isFocusable = true
            etFirstName.isFocusableInTouchMode = true
            etFirstName.isClickable = false

            etLastName.isEnabled = true
            etLastName.isFocusable = true
            etLastName.isFocusableInTouchMode = true
            etLastName.isClickable = false

            etAddress.isEnabled = true
            etAddress.isFocusable = true
            etAddress.isFocusableInTouchMode = true
            etAddress.isClickable = false

            etAddress2.isEnabled = true
            etAddress2.isFocusable = true
            etAddress2.isFocusableInTouchMode = true
            etAddress2.isClickable = false

            ivLocator.isEnabled = true

            etCity.isEnabled = true
            etCity.isFocusable = true
            etCity.isFocusableInTouchMode = true
            etCity.isClickable = false

            tvStateName.isEnabled = true
            tvState.isEnabled = true

            etZip.isEnabled = true
            etZip.isFocusable = true
            etZip.isFocusableInTouchMode = true
            etZip.isClickable = false

            etHomePhone.isEnabled = true
            etHomePhone.isFocusable = true
            etHomePhone.isFocusableInTouchMode = true
            etHomePhone.isClickable = false

            etCellPhone.isEnabled = true
            etCellPhone.isFocusable = true
            etCellPhone.isFocusableInTouchMode = true
            etCellPhone.isClickable = false

            etOtherPhone.isEnabled = true
            etOtherPhone.isFocusable = true
            etOtherPhone.isFocusableInTouchMode = true
            etOtherPhone.isClickable = false

            etFaxNumber.isEnabled = true
            etFaxNumber.isFocusable = true
            etFaxNumber.isFocusableInTouchMode = true
            etFaxNumber.isClickable = false

            etEmail.isEnabled = true
            etEmail.isFocusable = true
            etEmail.isFocusableInTouchMode = true
            etEmail.isClickable = false

            tvBirthDate.isEnabled = true

            etUci.isEnabled = true
            etUci.isFocusable = true
            etUci.isFocusableInTouchMode = true
            etUci.isClickable = false
        }

    }

    fun saveData() {
        if (isValidFields() && (QSPermissions.hasPermissionClientInfoMaintenanceUpdate() || QSPermissions.hasClientModuleAdminPermission())) {
            client?.let {
                it.FirstName = binding.etFirstName.text.toString()
                it.LastName = binding.etLastName.text.toString()
                it.Address = binding.etAddress.text.toString()
                it.Address2 = binding.etAddress2.text.toString()
                it.City = binding.etCity.text.toString()
                it.Zip = binding.etZip.text.toString()
                it.HomePhone = binding.etHomePhone.text.toString()
                it.CellPhone = binding.etCellPhone.text.toString()
                it.OtherPhone = binding.etOtherPhone.text.toString()
                it.FaxNumber = binding.etFaxNumber.text.toString()
                it.Email = binding.etEmail.text.toString()
                it.State = binding.tvStateName.text.toString()
                it.DOB = binding.tvBirthDate.text.toString()
                it.UCI = binding.etUci.text.toString()
            }
            val jsonString = Gson().toJson(client)
            val body: JsonObject = JsonParser().parse(jsonString).asJsonObject
            body.let {
                viewModel.updateClient(it)
            }
        }
    }

    private fun isValidFields(): Boolean {
        Validation(context = requireContext()).run {
            return isFieldValid(
                value = binding.etFirstName.text.toString(),
                getString(R.string.error_MSG_ENTER_NAME)
            ) && isFieldValid(
                value = binding.etLastName.text.toString(),
                getString(R.string.error_MSG_ENTER_LAST_NAME)
            ) && phoneNumberValidation(
                requireActivity(), binding.etHomePhone.text.toString()
            ) && phoneNumberValidation(
                requireActivity(), binding.etCellPhone.text.toString()
            ) && phoneNumberValidation(
                requireActivity(), binding.etOtherPhone.text.toString()
            ) && phoneNumberValidation(
                requireActivity(), binding.etFaxNumber.text.toString()
            ) && isFieldValid(
                value = binding.etEmail.text.toString(),
                getString(R.string.reg_error_MSG_ENTER_VALID_MAIL)
            )
        }
    }

    private fun openQSImagePicker() {
        QSImagePicker(requireContext()) { isCamera ->
            if (isCamera) {
                cameraLauncher.launch(requireActivity().cameraIntent)
            } else {
                galleryLauncher.launch(requireActivity().imageIntent)
            }
        }
    }

    private val cameraLauncher = registerActivityResultLauncher {
        if (it.resultCode == AppCompatActivity.RESULT_OK) {
            val intent = it.data
            intent?.run {
                val photo: Bitmap = intent.extras!!.get("data") as Bitmap
                binding.ivProfile.setImageBitmap(photo)
            }
        }
    }

    private val galleryLauncher = registerActivityResultLauncher {
        if (it.resultCode == AppCompatActivity.RESULT_OK) {
            val intent = it.data
            intent?.run {
                binding.ivProfile.loadGlide(this.data)
            }
        }
    }

    override fun onPermissionGranted() {
        openQSImagePicker()
    }

    override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }

    override fun afterTextChanged(s: Editable?) {
        isEdited = true
    }

    override fun onResume() {
        super.onResume()
        QSCalendar.refresh(resources)
    }
}