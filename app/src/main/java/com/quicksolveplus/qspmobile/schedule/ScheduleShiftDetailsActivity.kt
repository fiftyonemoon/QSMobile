package com.quicksolveplus.qspmobile.schedule

import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.quicksolveplus.api.Api
import com.quicksolveplus.api.RequestParameters
import com.quicksolveplus.api.ResponseStatus
import com.quicksolveplus.dialogs.QSAlert
import com.quicksolveplus.dialogs.QSFilterItems
import com.quicksolveplus.dialogs.QSSearchableListing
import com.quicksolveplus.modifiers.Actifiers.hideKeyboard
import com.quicksolveplus.qsbase.LocationBase
import com.quicksolveplus.qspmobile.R
import com.quicksolveplus.qspmobile.clients.models.Clients
import com.quicksolveplus.qspmobile.clients.models.ClientsItem
import com.quicksolveplus.qspmobile.clients.models.Relation
import com.quicksolveplus.qspmobile.databinding.ActivityScheduleShiftDetailsBinding
import com.quicksolveplus.qspmobile.employee.model.Employees
import com.quicksolveplus.qspmobile.employee.model.EmployeesItem
import com.quicksolveplus.qspmobile.schedule.dialogs.ShiftSubCodesDetails
import com.quicksolveplus.qspmobile.schedule.models.*
import com.quicksolveplus.qspmobile.schedule.viewmodel.ScheduleShiftDetailsViewModel
import com.quicksolveplus.utils.*
import java.text.DecimalFormat
import java.util.*

/**
 * 17/04/23.
 *
 * @author hardkgosai.
 */
class ScheduleShiftDetailsActivity : LocationBase() {

    private lateinit var binding: ActivityScheduleShiftDetailsBinding
    private val viewModel: ScheduleShiftDetailsViewModel by viewModels()

    private var client: ClientsItem? = null
    private var employee: EmployeesItem? = null
    private var scheduleItem: SchedulesItem? = null
    private var serviceTypeItem: ServiceTypeItem? = null
    private var attendanceTracking: AttendanceTracking? = null
    private var serviceTypeItems: ServiceType? = null
    private var relationItems: Relation? = null
    private var clients: Clients? = null
    private var employees: Employees? = null
    private var shiftSubCodes: ArrayList<ShiftSubCodesItem> = arrayListOf()
    private var shiftObjectives: ArrayList<ShiftObjectivesItem> = arrayListOf()
    private var ispObjectives: ArrayList<String> = arrayListOf()
    private var selectedLinkedClient: ArrayList<LinkedSchedulesItem> = arrayListOf()

    private var subCodeTextView: TextView? = null

    private var totalHours = ""
    private var currentClientId = -1
    private var currentClientLastName = ""
    private var currentClientFirstName = ""
    private var clientName = ""
    private var serviceType = ""
    private var startTime = ""
    private var endTime = ""
    private var startDate = ""
    private var endDate = ""
    private var employeeName = ""
    private var clockInLatitude = ""
    private var clockInLongitude = ""
    private var clockOutLatitude = ""
    private var clockOutLongitude = ""
    private var checkValidationsFor = ""
    private var reasonForCancelShift = ""
    private var serviceDeliveryRecord = 0
    private var timeSubcodesDiff = 0.0
    private var timeObjectiveDiff = 0.0
    private var timeDiff = 0.0

    private var attendancePSTRemaining = 0.0
    private var sleepHourValue = 0.0

    private var isEdited = false
    private var isClient = false
    private var isNewShift = false
    private var fromServiceType = false
    private var isAttendanceTrackingCalling = false
    private var isNeededToCheckSubCode = false
    private var isServiceTypeMethodAPICalledFromServiceType = false
    private var isServiceDeliveryMethodClear = false
    private var isEvvServiceType = false
    private var isChangeReasonVisible = false
    private var isShowMileage = false
    private var isServiceTypeChanged = false
    private var isWorkerChanged = false
    private var isClientChanged = false
    private var isShowProgress = false
    private var isShiftTimeChanged = false
    private var isMealTimeChanged = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityScheduleShiftDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getIntentData()
        initUI()
        setObservers()
    }

    private fun getIntentData() {
        intent.extras?.run {
            if (containsKey(Constants.isClient)) {
                isClient = getBoolean(Constants.isClient, false)
            }
            if (containsKey(Constants.newShift)) {
                isNewShift = getBoolean(Constants.newShift, false)
            }
            if (containsKey(Constants.schedule_map)) {
                scheduleItem = Gson().fromJson(
                    getString(Constants.schedule_map), object : TypeToken<SchedulesItem?>() {}.type
                )
            }
            if (isClient && containsKey(Constants.client_map)) { //from client
                client = Gson().fromJson(
                    getString(Constants.client_map), object : TypeToken<ClientsItem?>() {}.type
                )
            } else if (!isClient && containsKey(Constants.worker_map)) { //from worker
                employee = Gson().fromJson(
                    getString(Constants.worker_map), object : TypeToken<EmployeesItem?>() {}.type
                )
            }
        }
    }

    private fun initUI() {
        updateVisibilities()
        initClickListeners()
        initTextChangedListener()

        if (isNewShift) {
            QSPermissions.run {
                if (isAdmin || hasScheduleModificationCreatePermission() || hasScheduleModificationUpdatePermission()) {
                    setDetails()
                    makeIndividualFieldsSectionReadOnly()
                } else {
                    setDetails()
                    makeScreenReadOnly()
                }
            }
        } else {
            if (isClient) {
                getAttendanceTracking()
            } else {
                fromServiceType = false
                isAttendanceTrackingCalling = false
                getClientRemainingPOS(
                    clientID = scheduleItem?.clientUID, serviceType = scheduleItem?.taskName
                )
            }
        }
    }

    private fun updateVisibilities() {
        binding.tvClockDetails.isVisible = false
        binding.clOTExempt.isVisible = false
        binding.clSleepHours.isVisible = false
        binding.tvPOSHoursRemaining.isVisible = !isClient && !isNewShift

        if (!isNewShift && (scheduleItem != null && !scheduleItem!!.isCustomEvent)) {
            if (Preferences.instance?.user?.enableAttendanceTracking == true) {
                QSPermissions.run {
                    val hasTrackPermission = hasTrackAttendanceAccessPermission()
                    if (isAdmin && hasTrackPermission) {
                        binding.tvTrackAttendance.isVisible = true
                    } else if (!isAdmin && hasTrackPermission) {
                        binding.tvTrackAttendance.isVisible = true
                    } else {
                        binding.tvTrackAttendance.isGone = true
                    }
                }
            } else {
                binding.tvTrackAttendance.isGone = true
            }
        } else {
            binding.tvTrackAttendance.isGone = true
        }

        QSPermissions.run {
            binding.toolbar.ivDelete.isVisible =
                !isNewShift && (isAdmin || hasScheduleModificationDeletePermission()) && (Preferences.instance?.user?.securityLevel
                    ?: 6) < 6 //if user is null return false that's why we take number 6 as a default
            binding.toolbar.ivSave.isVisible =
                isAdmin || hasScheduleModificationUpdatePermission() || hasScheduleModificationCreatePermission() || hasServiceNoteModificationUpdatePermission() || hasScheduleNoteModificationUpdatePermission() || hasScheduleMileageModificationUpdatePermission() || hasScheduleObjectiveModificationUpdatePermission()
        }
    }

    private fun initClickListeners() {
        binding.toolbar.ivBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.toolbar.ivDelete.setOnClickListener { }
        binding.toolbar.ivLink.setOnClickListener { }
        binding.toolbar.ivSave.setOnClickListener { }

        binding.cbOTExempt.setOnClickListener {
            it.isSelected = !it.isSelected
            binding.clSleepHours.isVisible = it.isSelected
        }

        binding.cbRestPeriod.setOnClickListener {
            isEdited = true
            it.isSelected = !it.isSelected
            binding.tvTimeOut.isEnabled = it.isSelected
            binding.tvTimeIn.isEnabled = it.isSelected
        }
    }

    private fun initTextChangedListener() {
        binding.tvClientName.addTextChangedListener(textWatcher)
        binding.tvServiceType.addTextChangedListener(textWatcher)
        binding.tvStartTime.addTextChangedListener(textWatcher)
        binding.tvTotalHours.addTextChangedListener(textWatcher)
        binding.tvEndTime.addTextChangedListener(textWatcher)
        binding.tvTimeOut.addTextChangedListener(textWatcher)
        binding.tvTimeIn.addTextChangedListener(textWatcher)
        binding.tvStartDate.addTextChangedListener(textWatcher)
        binding.tvEndDate.addTextChangedListener(textWatcher)
        binding.tvEmployeeName.addTextChangedListener(textWatcher)
        binding.etMiles.addTextChangedListener(textWatcher)
        binding.etFrom.addTextChangedListener(textWatcher)
        binding.etDestination1.addTextChangedListener(textWatcher)
        binding.etDestination2.addTextChangedListener(textWatcher)
        binding.etDestination3.addTextChangedListener(textWatcher)
        binding.etDestination4.addTextChangedListener(textWatcher)
        binding.etDestination5.addTextChangedListener(textWatcher)
        binding.etDestination6.addTextChangedListener(textWatcher)
        binding.etScheduleNotes.addTextChangedListener(textWatcher)
        binding.etServiceNotes.addTextChangedListener(textWatcher)

        binding.etSleepHours.apply {
            filters = arrayOf(DecimalDigitsInputFilter(2))
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    sleepHourValue = if (s.toString().isNotEmpty()) s.toString().toDouble() else 0.0
                }
            })
        }

        makeEditTextScrollable(binding.etFrom)
        makeEditTextScrollable(binding.etDestination1)
        makeEditTextScrollable(binding.etDestination2)
        makeEditTextScrollable(binding.etDestination3)
        makeEditTextScrollable(binding.etDestination4)
        makeEditTextScrollable(binding.etDestination5)
        makeEditTextScrollable(binding.etDestination6)
        makeEditTextScrollable(binding.etScheduleNotes)
        makeEditTextScrollable(binding.etServiceNotes)
    }

    private fun setDetails() {
        binding.tvTimeIn.isEnabled = binding.cbRestPeriod.isSelected
        binding.tvTimeInLabel.isEnabled = binding.cbRestPeriod.isSelected
        binding.tvTimeOut.isEnabled = binding.cbRestPeriod.isSelected
        binding.tvTimeOutLabel.isEnabled = binding.cbRestPeriod.isSelected

        if (isNewShift) {
            val date = QSCalendar.formatDate(
                QSCalendar.calendarDate,
                QSCalendar.DateFormats.DDMMYYYY.label,
                QSCalendar.DateFormats.MMDDYYYY.label
            )
            binding.tvStartDate.text = date
            binding.tvEndDate.text = date

            if (isClient) {
                binding.tvClientName.text = String.format(
                    "%s %s", client?.FirstName, client?.LastName
                )
                getCustomClientServiceTypes()
            } else {
                binding.tvEmployeeName.text = String.format(
                    "%s %s", employee?.FirstName, employee?.LastName
                )
                getCustomEmployeeServiceTypes()
            }
        } else {
            scheduleItem?.run {
                client = ClientsItem(
                    Address = clientAddress1,
                    Address2 = clientAddress2,
                    ClientUID = clientUID,
                    FirstName = clientFirstName,
                    LastName = clientLastName,
                    HomePhone = clientHomePhone,
                    CellPhone = clientCellPhone,
                    City = clientCity,
                    State = clientState,
                    Zip = clientZip,
                    AllowMoreThanOneEmployeeToSchedule = allowMoreThanOneEmployeeToSchedule,
                    ApplyClock = clientApplyClock
                )

                employee = EmployeesItem(
                    UsersUID = staffUID, FirstName = staffFirstName, LastName = staffLastName
                )

                if (taskName == "Doc Apt" || recordType == "3") {
                    binding.tvServiceType.isEnabled = false
                    binding.tvServiceTypeLabel.isEnabled = false
                    binding.tvClientName.isEnabled = false
                    binding.tvClientNameLabel.isEnabled = false
                }

                if (isCustomEvent && isClient) {
                    binding.tvEmployeeName.isEnabled = false
                    binding.tvEmployeeNameLabel.isEnabled = false
                }

                if (isCustomEvent && !isClient) {
                    binding.tvClientName.isEnabled = false
                    binding.tvClientNameLabel.isEnabled = false
                }

                //todo init location list

                currentClientId = clientUID
                currentClientLastName = clientLastName
                currentClientFirstName = clientFirstName
                serviceType = taskName
                serviceDeliveryRecord = shiftMethod

                clientName = "$clientFirstName $clientLastName"
                binding.tvClientName.text = clientName
                binding.tvServiceType.text = serviceType

                isServiceDeliveryMethodClear = false

                QSPermissions.run {
                    if (isAdmin || hasQSClockInfoAccessPermission() || hasQSClockInfoUpdatePermission()) {

                        binding.tvClockDetails.isVisible =
                            !isNewShift && client?.ApplyClock == 1 && scheduleItem?.isCustomEvent != true

                    } else {
                        binding.tvClockDetails.isVisible = false
                    }
                }

                QSCalendar.run {
                    val fillStartDate = formatDate(
                        scheduleItem?.schedDate,
                        QSCalendar.DateFormats.MMDDYY.label,
                        QSCalendar.DateFormats.MMDDYYYY.label
                    )
                    startDate = fillStartDate
                    binding.tvStartDate.text = fillStartDate

                    val fillEndDate = formatDate(
                        scheduleItem?.schedDateEnd,
                        QSCalendar.DateFormats.MMDDYY.label,
                        QSCalendar.DateFormats.MMDDYYYY.label
                    )
                    endDate = fillEndDate
                    binding.tvEndDate.text = fillEndDate

                    totalHours = hours.toString()
                    binding.tvTotalHours.text = hours.toString()

                    this@ScheduleShiftDetailsActivity.startTime =
                        getFormattedTimings(startTime) ?: ""
                    binding.tvStartTime.text = getFormattedTimings(startTime)

                    this@ScheduleShiftDetailsActivity.endTime = getFormattedTimings(endTime) ?: ""
                    binding.tvEndDate.text = getFormattedTimings(endTime)

                    if (hasMealBreak == 1) {
                        binding.cbRestPeriod.isSelected = true
                    }
                    if (mealOut.trim().isNotEmpty()) {
                        binding.tvTimeOut.text = getFormattedTimings(mealOut)
                    }
                    if (mealIn.trim().isNotEmpty()) {
                        binding.tvTimeIn.text = getFormattedTimings(mealIn)
                    }
                    if (clockIn.isNotEmpty()) {
                        binding.clockDetails.tvClockInTime.text = getFormattedTimings(clockIn)
                    }
                    if (clockOut.isNotEmpty()) {
                        binding.clockDetails.tvClockOutTime.text = getFormattedTimings(clockOut)
                    }
                    if (clockInLatitude != "No Latitude Recorded") {
                        this@ScheduleShiftDetailsActivity.clockInLatitude = clockInLatitude
                        binding.clockDetails.tvClockInLat.text = clockInLatitude
                    }
                    if (clockOutLatitude != "No Latitude Recorded") {
                        this@ScheduleShiftDetailsActivity.clockOutLatitude = clockOutLatitude
                        binding.clockDetails.tvClockOutLat.text = clockOutLatitude
                    }
                    if (clockInLongitude != "No Longitude Recorded") {
                        this@ScheduleShiftDetailsActivity.clockInLongitude = clockInLongitude
                        binding.clockDetails.tvClockInLng.text = clockInLongitude
                    }
                    if (clockOutLongitude != "No Longitude Recorded") {
                        this@ScheduleShiftDetailsActivity.clockOutLongitude = clockOutLongitude
                        binding.clockDetails.tvClockOutLng.text = clockOutLongitude
                    }
                    if (sleepHours.isNotEmpty()) {
                        binding.etSleepHours.setText(sleepHours)
                    }
                }

                employeeName = "$staffFirstName $staffLastName"
                binding.tvEmployeeName.text = employeeName

                binding.etMiles.setText(mileage.toString())
                binding.etFrom.setText(travelFrom)
                binding.etDestination1.setText(travelTo)
                binding.etDestination2.setText(travelTo2)
                binding.etDestination3.setText(travelTo3)
                binding.etDestination4.setText(travelTo4)
                binding.etDestination5.setText(travelTo5)
                binding.etDestination6.setText(travelTo6)
                binding.etScheduleNotes.setText(comments)
                binding.etServiceNotes.setText(serviceNotes)

                selectedLinkedClient.addAll(linkedSchedules)

                binding.tvTrackAttendance.background = if (attendanceTracking != null) {
                    ContextCompat.getDrawable(
                        this@ScheduleShiftDetailsActivity, R.drawable.btn_blue_rounded_corner_bg
                    )
                } else {
                    ContextCompat.getDrawable(
                        this@ScheduleShiftDetailsActivity, R.drawable.btn_green_rounded
                    )
                }

                if (isClient) {
                    getCustomClientServiceTypes()
                } else {
                    getCustomEmployeeServiceTypes()
                }

                makeIndividualFieldsSectionReadOnly()
                isShowMileage = false
                visibleShowMileage(false)
            }
        }
    }

    private fun makeScreenReadOnly() {
        disablePrimaryViews()
        disableScheduleSubCodesPanel()
        QSPermissions.run {
            if (!hasScheduleMileageModificationUpdatePermission()) {
                disableScheduleMileagePanel()
                binding.ivLocation.isEnabled = false
                binding.ivLocation1.isEnabled = false
                binding.ivLocation2.isEnabled = false
                binding.ivLocation3.isEnabled = false
                binding.ivLocation4.isEnabled = false
                binding.ivLocation5.isEnabled = false
                binding.ivLocation6.isEnabled = false
                binding.tvAddMileage.text = getString(R.string.str_view_mileage)
            }
            if (!hasServiceNoteModificationUpdatePermission()) {
                disableServiceNotesPanel()
            }
            if (!hasScheduleNoteModificationUpdatePermission()) {
                disableScheduleNotesPanel()
            }
            if (!hasScheduleObjectiveModificationUpdatePermission()) {
                disableScheduleObjectivePanel()
            }
        }
    }

    private fun makeIndividualFieldsSectionReadOnly() {
        QSPermissions.run {
            if (!isAdmin) {
                if (!hasScheduleModificationUpdatePermission() && scheduleItem != null) {
                    disablePrimaryViews()
                }
                if (!hasServiceNoteModificationUpdatePermission()) {
                    disableServiceNotesPanel()
                }
                if (!hasScheduleNoteModificationUpdatePermission()) {
                    disableScheduleNotesPanel()
                }
                if (!hasScheduleMileageModificationUpdatePermission()) {
                    disableScheduleMileagePanel()
                }
                if (!hasScheduleObjectiveModificationUpdatePermission()) {
                    disableScheduleObjectivePanel()
                }
            }
        }
    }

    private fun disablePrimaryViews() {
        val disabledColor = ContextCompat.getColor(
            this@ScheduleShiftDetailsActivity, R.color.border
        )
        binding.cbRestPeriod.apply {
            isEnabled = false
            alpha = 0.5f
        }
        binding.cbOTExempt.apply {
            isEnabled = false
            alpha = 0.5f
        }
        binding.tvClientName.apply {
            isEnabled = false
            setTextColor(disabledColor)
        }
        binding.tvClientNameLabel.apply {
            isEnabled = false
            setTextColor(disabledColor)
        }
        binding.tvServiceType.apply {
            isEnabled = false
            setTextColor(disabledColor)
        }
        binding.tvServiceTypeLabel.apply {
            isEnabled = false
            setTextColor(disabledColor)
        }
        binding.tvStartTime.apply {
            isEnabled = false
            setTextColor(disabledColor)
        }
        binding.tvStartTimeLabel.apply {
            isEnabled = false
            setTextColor(disabledColor)
        }
        binding.tvTotalHours.apply {
            isEnabled = false
            setTextColor(disabledColor)
        }
        binding.tvTotalHoursLabel.apply {
            isEnabled = false
            setTextColor(disabledColor)
        }
        binding.tvEndTime.apply {
            isEnabled = false
            setTextColor(disabledColor)
        }
        binding.tvEndTimeLabel.apply {
            isEnabled = false
            setTextColor(disabledColor)
        }
        binding.tvTimeOut.apply {
            isEnabled = false
            setTextColor(disabledColor)
        }
        binding.tvTimeOutLabel.apply {
            isEnabled = false
            setTextColor(disabledColor)
        }
        binding.tvRestPeriodLabel.apply {
            isEnabled = false
            setTextColor(disabledColor)
        }
        binding.tvTimeIn.apply {
            isEnabled = false
            setTextColor(disabledColor)
        }
        binding.tvTimeInLabel.apply {
            isEnabled = false
            setTextColor(disabledColor)
        }
        binding.tvStartDate.apply {
            isEnabled = false
            setTextColor(disabledColor)
        }
        binding.tvStartDateLabel.apply {
            isEnabled = false
            setTextColor(disabledColor)
        }
        binding.tvEndDate.apply {
            isEnabled = false
            setTextColor(disabledColor)
        }
        binding.tvEndDateLabel.apply {
            isEnabled = false
            setTextColor(disabledColor)
        }
        binding.tvEmployeeName.apply {
            isEnabled = false
            setTextColor(disabledColor)
        }
        binding.tvEmployeeNameLabel.apply {
            isEnabled = false
            setTextColor(disabledColor)
        }
        binding.etSleepHours.apply {
            isEnabled = false
            setTextColor(disabledColor)
        }

        binding.tvPlus.isEnabled = false
        binding.tvMinus.isEnabled = false
    }

    private fun disableScheduleObjectivePanel() {
        binding.lvShiftObjectives.isEnabled = false
        binding.tvAddObjectives.apply {
            isEnabled = false
            setBackgroundColor(
                ContextCompat.getColor(
                    this@ScheduleShiftDetailsActivity, R.color.border
                )
            )
        }
    }

    private fun disableScheduleSubCodesPanel() {
        binding.lvShiftSubCodes.isEnabled = false
        binding.tvAddSubCodes.apply {
            isEnabled = false
            setBackgroundColor(
                ContextCompat.getColor(
                    this@ScheduleShiftDetailsActivity, R.color.border
                )
            )
        }
    }

    private fun disableScheduleNotesPanel() {
        val disabledColor = ContextCompat.getColor(
            this@ScheduleShiftDetailsActivity, R.color.border
        )
        binding.etScheduleNotes.apply {
            keyListener = null
            isFocusable = true
            isCursorVisible = false
            setTextColor(disabledColor)
            binding.tvScheduleNotesLabel.setTextColor(disabledColor)
        }
    }

    private fun disableServiceNotesPanel() {
        val disabledColor = ContextCompat.getColor(
            this@ScheduleShiftDetailsActivity, R.color.border
        )
        binding.etServiceNotes.apply {
            keyListener = null
            isFocusable = true
            isCursorVisible = false
            setTextColor(disabledColor)
            binding.tvServiceNotesLabel.setTextColor(disabledColor)
        }
    }

    private fun disableScheduleMileagePanel() {
        val disabledColor = ContextCompat.getColor(
            this@ScheduleShiftDetailsActivity, R.color.border
        )
        binding.etMiles.apply {
            isEnabled = false
            setTextColor(disabledColor)
            binding.tvMilesLabel.setTextColor(disabledColor)
        }
        binding.etFrom.apply {
            isEnabled = false
            setTextColor(disabledColor)
            binding.tvFromLabel.setTextColor(disabledColor)
        }
        binding.etDestination1.apply {
            isEnabled = false
            setTextColor(disabledColor)
            binding.tvDestination1Label.setTextColor(disabledColor)
        }
        binding.etDestination2.apply {
            isEnabled = false
            setTextColor(disabledColor)
            binding.tvDestination2Label.setTextColor(disabledColor)
        }
        binding.etDestination3.apply {
            isEnabled = false
            setTextColor(disabledColor)
            binding.tvDestination3Label.setTextColor(disabledColor)
        }
        binding.etDestination4.apply {
            isEnabled = false
            setTextColor(disabledColor)
            binding.tvDestination4Label.setTextColor(disabledColor)
        }
        binding.etDestination5.apply {
            isEnabled = false
            setTextColor(disabledColor)
            binding.tvDestination5Label.setTextColor(disabledColor)
        }
        binding.etDestination6.apply {
            isEnabled = false
            setTextColor(disabledColor)
            binding.tvDestination6Label.setTextColor(disabledColor)
        }
    }

    private fun setObservers() {
        viewModel.getResponseStatus().observe(this) {
            when (it) {
                is ResponseStatus.Running -> {
                    if (it.apiName == Api.getShiftSubCodes || it.apiName == Api.getShiftObjectives) {
                        if (isShowProgress) {
                            isShowProgress = false
                            showQSProgress(this)
                        }
                    } else showQSProgress(this)
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

    private fun proceedResponse(success: ResponseStatus.Success) {
        when (success.apiName) {
            Api.getCustomClientServiceTypes, Api.getCustomEmployeeServiceTypes -> {
                if (success.data is ServiceType) {
                    proceedCustomServiceTypes(success.data)
                }
            }
            Api.getOptionListWithUID -> {
                if (success.data is Relation) {
                    proceedOptionListWithUID(success.data)
                }
            }
            Api.getAttendanceTracking -> {
                if (success.data is AttendanceTracking) {
                    proceedAttendanceTracking(success.data)
                }
            }
            Api.getClientRemainingPOS -> {
                if (success.data is String) {
                    proceedClientRemainingPOS(success.data)
                }
            }
            Api.getClientsForShiftPopup -> {
                if (success.data is Clients) {
                    proceedClientsForShiftPopup(success.data)
                }
            }
            Api.getWorkersForShiftPopup -> {
                if (success.data is Employees) {
                    proceedEmployeesForShiftPopup(success.data)
                }
            }
            Api.getShiftSubCodes -> {
                if (success.data is ShiftSubCodes) {
                    proceedShiftSubCodes(success.data)
                }
            }
            Api.getShiftObjectives -> {
                if (success.data is ShiftObjectives) {
                    proceedShiftObjectives(success.data)
                }
            }
            Api.getISPObjectivesForSchedule -> {
                if (success.data is ISPObjectives) {
                    proceedISPObjectivesForSchedule(success.data)
                }
            }
        }
    }

    private fun proceedFailure(failed: ResponseStatus.Failed) {
        when (failed.apiName) {
            Api.getOptionListWithUID -> {
                proceedOptionListWithUID(null)
            }
            Api.getAttendanceTracking -> {
                proceedAttendanceTracking(null)
            }
            Api.getClientRemainingPOS -> {
                proceedClientRemainingPOS(null)
            }
            Api.getShiftSubCodes -> {
                proceedShiftSubCodes(null)
            }
            Api.getShiftObjectives -> {
                proceedShiftObjectives(null)
            }
            Api.getISPObjectivesForSchedule -> {
                proceedISPObjectivesForSchedule(null)
            }
        }
    }

    private fun proceedCustomServiceTypes(serviceType: ServiceType) {
        serviceTypeItems?.clear()
        serviceType.map {
            if (it.text != getString(R.string.lbl_select)) {
                serviceTypeItems?.add(it)
            }
        }

        if (!isNewShift) {
            serviceTypeItems?.run {
                for (it in this) {
                    if (scheduleItem?.taskName == it.text) {
                        this@ScheduleShiftDetailsActivity.serviceType = it.uID.toString()
                        binding.clServiceDeliveryMethod.isVisible = it.method
                        isEvvServiceType = it.isEvvServiceType
                        break
                    }
                }
            }

            updateChangeReasonLayoutVisibility()
            binding.tvServiceDeliveryMethod.text = ""
            serviceDeliveryRecord = 0
            isServiceTypeMethodAPICalledFromServiceType = true
            getOptionListWithUID(this@ScheduleShiftDetailsActivity.serviceType)
        }
    }

    private fun proceedOptionListWithUID(relation: Relation?) {

        //if it's null it means the response is failed or maybe method called from failure side
        relation?.run {
            relationItems?.clear()
            relationItems?.addAll(relation)

            if (!isNewShift && !isServiceDeliveryMethodClear) {
                relationItems?.run {
                    for (it in this) {
                        if (scheduleItem?.shiftMethod.toString() == it.Value) {
                            binding.tvServiceDeliveryMethod.text = it.Text
                            if (it.Value.isNotEmpty()) {
                                serviceDeliveryRecord = it.Value.toInt()
                            }
                            break
                        }
                    }
                }
            }
        }

        if (isServiceTypeMethodAPICalledFromServiceType) {
            isServiceTypeMethodAPICalledFromServiceType = false
            if (!isNewShift) {
                serviceTypeItems?.map {
                    if (scheduleItem?.taskName == it.text) {
                        this.serviceTypeItem = it
                    }
                }
            }
        } else {
            serviceTypeItem?.run {
                if (!isClient) {
                    binding.tvPOSHoursRemaining.isVisible = true
                    fromServiceType = true
                    isAttendanceTrackingCalling = false
                    //todo
                } else {
                    //todo
                }
            }
        }
    }

    private fun proceedAttendanceTracking(attendanceTracking: AttendanceTracking?) {
        attendanceTracking?.let {
            this@ScheduleShiftDetailsActivity.attendanceTracking = it
        }
        QSPermissions.run {
            if (isAdmin || hasScheduleModificationCreatePermission() || hasScheduleModificationUpdatePermission()) {
                setDetails()
                makeIndividualFieldsSectionReadOnly()
            } else {
                setDetails()
                makeScreenReadOnly()
            }
            isEdited = false
        }
    }

    private fun proceedClientRemainingPOS(result: String?) {
        result?.let {
            val hours = "${getString(R.string.str_pos_hours_remaining)}: ${result.ifEmpty { 0 }}"
            binding.tvPOSHoursRemaining.text = hours

            if (isNewShift) {
                if (fromServiceType) {
                    fromServiceType = false
                    serviceTypeConfig(serviceTypeItem)
                }
            } else {
                if (isAttendanceTrackingCalling) {
                    getAttendanceTracking()
                } else {
                    if (fromServiceType) {
                        fromServiceType = false
                        serviceTypeConfig(serviceTypeItem)
                    }
                }
            }
        }
    }

    private fun proceedClientsForShiftPopup(clients: Clients) {
        this.clients?.clear()
        this.clients?.addAll(clients)

        if (isServiceTypeChanged) {
            var matched = false
            for (it in clients) {
                if (it.ClientUID == client?.ClientUID) {
                    matched = true
                    QSPermissions.run {
                        if (isAdmin || hasQSClockInfoUpdatePermission() || hasQSClockInfoAccessPermission()) {
                            binding.tvClockDetails.isVisible =
                                !isNewShift && it.ApplyClock == 1 && serviceTypeItem?.custom != true
                        } else {
                            binding.tvClockDetails.isVisible = false
                        }
                    }
                    break
                }
            }
            if (!matched) {
                clearClientSelection()
            }
        } else {
            QSSearchableListing(
                context = this@ScheduleShiftDetailsActivity,
                title = getString(R.string.str_select_client),
                isClient = true,
                clients = clients,
                selectedItemName = binding.tvClientName.text.toString()
            ) { position ->
                val client = clients[position]
                this@ScheduleShiftDetailsActivity.client = client

                val newName = "${client.FirstName} ${client.LastName}"
                val oldName = binding.tvClientName.text.toString()

                if (newName != oldName) {
                    isClientChanged = true
                }
                binding.tvClientName.text = newName

                if (!isClient && binding.tvServiceType.text.isNotEmpty()) {
                    binding.tvPOSHoursRemaining.isVisible = true
                    fromServiceType = false
                    isAttendanceTrackingCalling = false
                    getClientRemainingPOS(client.ClientUID, binding.tvServiceType.text.toString())
                }
            }.show()
        }
    }

    private fun proceedEmployeesForShiftPopup(employees: Employees) {
        this.employees?.clear()
        this.employees?.addAll(employees)

        if (isServiceTypeChanged) {
            var matched = false
            for (it in employees) {
                if (it.UsersUID == employee?.UsersUID) {
                    matched = true
                    break
                }
            }
            if (!matched) {
                clearWorkerSelection()
            }
        } else {
            QSSearchableListing(
                context = this@ScheduleShiftDetailsActivity,
                title = getString(R.string.str_select_employee),
                isClient = false,
                employees = employees,
                selectedItemName = binding.tvEmployeeName.text.toString()
            ) { position ->
                val employee = employees[position]
                this@ScheduleShiftDetailsActivity.employee = employee

                val newName = "${employee.FirstName} ${employee.LastName}"
                val oldName = binding.tvEmployeeName.text.toString()

                if (newName != oldName) {
                    isWorkerChanged = true
                }
                binding.tvEmployeeName.text = newName
            }.show()
        }
    }

    private fun proceedShiftSubCodes(subCodes: ShiftSubCodes?) {
        timeSubcodesDiff = 0.0

        if (subCodes != null) {
            binding.tvNoSubCodes.isVisible = subCodes.isEmpty()
            binding.lvShiftSubCodes.isVisible = subCodes.isNotEmpty()

            if (subCodes.isNotEmpty()) {
                shiftSubCodes.clear()
                shiftSubCodes.addAll(subCodes)
                setShiftSubCodesAdapter()
            }
        } else {
            shiftSubCodes.clear()
            setShiftSubCodesAdapter()
            binding.tvNoSubCodes.isVisible = true
            binding.lvShiftSubCodes.isVisible = false
        }
    }

    private fun proceedShiftObjectives(objectives: ShiftObjectives?) {
        timeObjectiveDiff = 0.0
        if (objectives != null) {
            binding.tvNoObjectives.isVisible = objectives.isEmpty()
            binding.lvShiftObjectives.isVisible = objectives.isNotEmpty()

            if (objectives.isNotEmpty()) {
                shiftObjectives.clear()
                shiftObjectives.addAll(objectives)
                setShiftObjectivesAdapter()
            }
        } else {
            shiftObjectives.clear()
            setShiftObjectivesAdapter()
            binding.tvNoObjectives.isVisible = true
            binding.lvShiftObjectives.isVisible = false
        }
    }

    private fun proceedISPObjectivesForSchedule(ispObjectives: ISPObjectives?) {
        this.ispObjectives.clear()
        ispObjectives?.map {
            it.objective?.let { value ->
                if (value.isNotEmpty()) {
                    this.ispObjectives.add(value)
                }
            }
            it.objective2?.let { value ->
                if (value.isNotEmpty()) {
                    this.ispObjectives.add(value)
                }
            }
            it.objective3?.let { value ->
                if (value.isNotEmpty()) {
                    this.ispObjectives.add(value)
                }
            }
            it.objective4?.let { value ->
                if (value.isNotEmpty()) {
                    this.ispObjectives.add(value)
                }
            }
            it.objective5?.let { value ->
                if (value.isNotEmpty()) {
                    this.ispObjectives.add(value)
                }
            }
            it.objective6?.let { value ->
                if (value.isNotEmpty()) {
                    this.ispObjectives.add(value)
                }
            }
            it.objective7?.let { value ->
                if (value.isNotEmpty()) {
                    this.ispObjectives.add(value)
                }
            }
            it.objective8?.let { value ->
                if (value.isNotEmpty()) {
                    this.ispObjectives.add(value)
                }
            }
            it.objective9?.let { value ->
                if (value.isNotEmpty()) {
                    this.ispObjectives.add(value)
                }
            }
            it.objective10?.let { value ->
                if (value.isNotEmpty()) {
                    this.ispObjectives.add(value)
                }
            }
            it.objective11?.let { value ->
                if (value.isNotEmpty()) {
                    this.ispObjectives.add(value)
                }
            }
            it.objective12?.let { value ->
                if (value.isNotEmpty()) {
                    this.ispObjectives.add(value)
                }
            }
            it.objective13?.let { value ->
                if (value.isNotEmpty()) {
                    this.ispObjectives.add(value)
                }
            }
        }

        QSFilterItems(
            context = this,
            title = getString(R.string.str_schedule_select_objective),
            items = this.ispObjectives,
            showError = true
        ) { position ->
            subCodeTextView?.text = this.ispObjectives[position]
        }.show()
    }

    private fun getCustomClientServiceTypes() {
        client?.run {
            val body = RequestParameters.forCustomClientServiceTypes(clientID = ClientUID)
            viewModel.getCustomClientServiceTypes(body)
        }
    }

    private fun getCustomEmployeeServiceTypes() {
        employee?.run {
            val body = RequestParameters.forCustomEmployeeServiceTypes(employeeId = UsersUID)
            viewModel.getCustomEmployeeServiceTypes(body)
        }
    }

    private fun getOptionListWithUID(serviceType: String) {
        val body = RequestParameters.forOptionListWithUID(
            optionListId = 161, shiftMethod = serviceType
        )
        viewModel.getOptionListWithUID(body)
    }

    private fun getAttendanceTracking() {
        scheduleItem?.run {
            val body = RequestParameters.forAttendanceTracking(scheduleId = uID)
            viewModel.getAttendanceTracking(body)
        }
    }

    private fun getClientRemainingPOS(clientID: Int?, serviceType: String?) {
        val scheduleDate = if (isNewShift) {
            binding.tvStartDate.text.toString()
        } else {
            QSCalendar.formatDate(
                scheduleItem?.schedDate,
                QSCalendar.DateFormats.MMDDYY.label,
                QSCalendar.DateFormats.MMDDYYYY.label
            )
        }
        val body = RequestParameters.forClientRemainingPOS(
            clientID = clientID, serviceType = serviceType, scheduleDate = scheduleDate
        )
        viewModel.getClientRemainingPOS(body)
    }

    private fun getClientsForShiftPopup() {
        Preferences.instance?.user?.run {
            val body = RequestParameters.forClientsForShiftPopup(
                userUID = uID,
                userLevel1 = userLevel1,
                userLevel2 = userLevel2,
                userSecurityLevel = securityLevel,
                serviceType = binding.tvServiceType.text.toString()
            )
            viewModel.getClientsForShiftPopup(body)
        }
    }

    private fun getWorkersForShiftPopup() {
        Preferences.instance?.user?.run {
            val body = RequestParameters.forWorkersForShiftPopup(
                staffID = uID,
                userLevel1 = userLevel1,
                userLevel2 = userLevel2,
                userSecurityLevel = securityLevel,
                serviceType = binding.tvServiceType.text.toString(),
                isDoctorRecord = scheduleItem?.taskName == "Doc Apt" || scheduleItem?.recordType == "3"
            )
            viewModel.getWorkersForShiftPopup(body)
        }
    }

    private fun getShiftSubCodes(showProgress: Boolean = true) {
        scheduleItem?.run {
            isShowProgress = showProgress
            val body = RequestParameters.forShiftSubCodes(scheduleUID = uID)
            viewModel.getShiftSubCodes(body)
        }
    }

    private fun getShiftObjectives(showProgress: Boolean = true) {
        scheduleItem?.run {
            isShowProgress = showProgress
            val body = RequestParameters.forShiftObjectives(scheduleUID = uID)
            viewModel.getShiftObjectives(body)
        }
    }

    private fun getISPObjectivesForSchedule() {

        if (this.ispObjectives.isNotEmpty()) {
            QSFilterItems(
                context = this,
                title = getString(R.string.str_schedule_select_objective),
                items = this.ispObjectives,
                showError = true
            ) { position ->
                subCodeTextView?.text = this.ispObjectives[position]
            }.show()
        } else {
            client?.run {
                val body = RequestParameters.forISPObjectivesForSchedule(
                    clientID = ClientUID,
                    serviceType = binding.tvServiceType.text.toString(),
                    schedDate = binding.tvStartDate.text.toString()
                )
                viewModel.getShiftObjectives(body)
            }
        }
    }

    private fun setShiftSubCodesAdapter() {
        timeSubcodesDiff = 0.0

        if (shiftSubCodes.isNotEmpty()) {
            binding.tvClientName.isEnabled = false
            binding.tvClientNameLabel.isEnabled = false
            binding.tvStartDate.isEnabled = false
            binding.tvStartDateLabel.isEnabled = false

            shiftSubCodes.map {
                if (it.subCodeTime.isNotEmpty()) {
                    timeSubcodesDiff += it.subCodeTime.toDouble()
                }
            }

            val adapter = getShiftSubCodesAdapter()
            binding.lvShiftSubCodes.adapter = adapter
            binding.lvShiftSubCodes.setOnItemClickListener { parent, view, position, id ->
                if (isValidTime()) {
                    val subCodesItem = shiftSubCodes[position]
                    if (client?.ClientUID != -1) {
                        showShiftSubCodesDetails(position, true, subCodesItem)
                    } else {
                        QSAlert(
                            context = this,
                            title = getString(R.string.str_error),
                            message = getString(R.string.str_select_client_err),
                            positiveButtonText = getString(R.string.str_ok)
                        ).show()
                    }
                }
            }
        }
    }

    private fun showShiftSubCodesDetails(
        position: Int, isEditable: Boolean, subCodesItem: ShiftSubCodesItem
    ) {
        ShiftSubCodesDetails(activity = this,
            title = getString(R.string.str_subcodes),
            isEditable = true,
            shiftSubCodesItem = subCodesItem,
            onDeleteClick = { dialog ->
                QSAlert(
                    context = this@ScheduleShiftDetailsActivity,
                    title = getString(R.string.str_delete),
                    message = getString(R.string.delete_shift_subcode_dialog_subtitle),
                    positiveButtonText = getString(R.string.action_yes),
                    negativeButtonText = getString(R.string.action_no)
                ) { isPositive ->
                    if (isPositive) {
                        if (isNewShift) {
                            shiftSubCodes.removeAt(position)
                            (binding.lvShiftSubCodes.adapter as ArrayAdapter<*>).notifyDataSetChanged()
                            dialog.dismiss()
                        } else {
                            //todo delete api call - old method-> deleteShiftSubCode(subcodeUID)
                        }
                    }
                }
            },
            onSubCodesClick = { subCodeTextView ->
                hideKeyboard()
                this.subCodeTextView = subCodeTextView
                getISPObjectivesForSchedule() //todo change here, see old project
            },
            onAddClick = { subCode, time, notes ->
                hideKeyboard()
                addSubCodes(position, isEditable, subCode, time, notes, subCodesItem)
            },
            onCancelClick = {
                hideKeyboard()
            }).show()
    }

    private fun addSubCodes(
        position: Int,
        isEditable: Boolean,
        subCode: String,
        time: String,
        notes: String,
        subCodesItem: ShiftSubCodesItem
    ) {
        val currentObjectivesTime: Double
        var totalObjectivesTime: Double

        if (position != -1) {
            currentObjectivesTime = subCodesItem.subCodeTime.toDouble()
            totalObjectivesTime = timeSubcodesDiff - currentObjectivesTime + time.toDouble()
        } else {
            totalObjectivesTime = timeSubcodesDiff + time.toDouble()
        }

        val totalMinute = totalObjectivesTime * 60
        totalObjectivesTime = totalMinute / 60

        val diff = trimFloatValue(timeDiff).toDouble()

        if (totalObjectivesTime > diff) {
            QSAlert(
                context = this,
                title = getString(R.string.str_error),
                message = getString(R.string.str_total_time_spent_subcodes_not_match_total_time_staff),
                positiveButtonText = getString(R.string.str_ok)
            ).show()
        } else {
            if (isEditable) {
                if (isNewShift) {
                    subCodesItem.subCode = subCode
                    subCodesItem.subCodeTime = time
                    subCodesItem.subCodeNotes = notes
                    shiftSubCodes[position] = subCodesItem
                    setShiftSubCodesAdapter()
                } else {
                    //todo update
                }
            } else {
                if (isNewShift) {
                    val stringTokenizer = StringTokenizer(binding.tvStartDate.text.toString(), "/")
                    val month = stringTokenizer.nextToken()
                    val day = stringTokenizer.nextToken()
                    val year = stringTokenizer.nextToken()

                    val obj = ShiftSubCodesItem(
                        uID = 0,
                        clientUID = client?.ClientUID ?: -1,
                        staffUID = employee?.UsersUID ?: -1,
                        scheduleUID = -1,
                        level1 = Preferences.instance?.user?.userLevel1 ?: 0,
                        level2 = Preferences.instance?.user?.userLevel2 ?: 0,
                        subCode = subCode,
                        subCodeTime = time,
                        subCodeNotes = notes,
                        schedDate = binding.tvStartDate.text.toString(),
                        subCodeDetail = "",
                        dDay = day.toInt(),
                        dMonth = month.toInt(),
                        dYear = year.toInt()
                    )
                    shiftSubCodes.add(obj)
                    setShiftSubCodesAdapter()
                } else {
                    //todo add to server
                }
            }
        }
    }

    private fun setShiftObjectivesAdapter() {
        timeObjectiveDiff = 0.0

        if (shiftObjectives.isNotEmpty()) {
            binding.tvClientName.isEnabled = false
            binding.tvClientNameLabel.isEnabled = false
            binding.tvStartDate.isEnabled = false
            binding.tvStartDateLabel.isEnabled = false

            shiftObjectives.map {
                if (it.objectiveTime.isNotEmpty()) {
                    timeObjectiveDiff += it.objectiveTime.toDouble()
                }
            }

            val adapter = getShiftObjectivesAdapter()
            binding.lvShiftObjectives.adapter = adapter
            binding.lvShiftObjectives.setOnItemClickListener { parent, view, position, id ->
                if (isValidTime()) {
                    val shiftObjectivesItem = shiftObjectives[position]
                    if (client?.ClientUID != -1) {
                        //todo
                    } else {
                        QSAlert(
                            context = this,
                            title = getString(R.string.str_error),
                            message = getString(R.string.str_select_client_err),
                            positiveButtonText = getString(R.string.str_ok)
                        ).show()
                    }
                }
            }
        }
    }

    private fun serviceTypeConfig(serviceTypeItem: ServiceTypeItem?) {
        if (serviceTypeItem == null) {
            return
        }

        isServiceTypeChanged = true
        scheduleItem?.run {
            binding.cbOTExempt.isSelected = sleepShift == 1
            binding.clOTExempt.isVisible = serviceTypeItem.oTExempt
            binding.clSleepHours.isVisible = serviceTypeItem.oTExempt && sleepShift == 1
        }

        isShowMileage = false
        visibleShowMileage(false)

        binding.toolbar.ivLink.isVisible = serviceTypeItem.multiClient
        binding.clShiftSubCodes.isVisible = serviceTypeItem.isSubCoded
        binding.clTimeInOut.isVisible = serviceTypeItem.restPeriods
        binding.clMileage.isVisible = serviceTypeItem.mileage
        binding.clMilesDriven.isVisible = serviceTypeItem.mileage
        binding.clFrom.isVisible = serviceTypeItem.mileage
        binding.clDestination1.isVisible = serviceTypeItem.mileage
        binding.clDestination2.isVisible = serviceTypeItem.mileage
        binding.clDestination3.isVisible = serviceTypeItem.mileage
        binding.clDestination4.isVisible = serviceTypeItem.mileage
        binding.clDestination5.isVisible = serviceTypeItem.mileage
        binding.clDestination6.isVisible = serviceTypeItem.mileage
        binding.clShiftObjective.isVisible = serviceTypeItem.objectiveNotes
        binding.clServiceNotes.isVisible = serviceTypeItem.serviceNotes
        binding.clScheduleNotes.isVisible = serviceTypeItem.scheduleNotes

        if (serviceTypeItem.isSubCoded) {
            checkForShiftSubCodes(false)
        }
        if (serviceTypeItem.objectiveNotes) {
            checkForShiftObjectives(false)
        }

        QSPermissions.run {
            if (isAdmin || hasScheduleModificationCreatePermission() || hasScheduleModificationUpdatePermission()) {
                if (isClient) {
                    binding.tvEmployeeName.isEnabled = !serviceTypeItem.custom
                    binding.tvEmployeeNameLabel.isEnabled = !serviceTypeItem.custom
                    if (serviceTypeItem.custom) {
                        clearWorkerSelection()
                    } else {
                        getWorkersForShiftPopup()
                    }
                } else {
                    binding.tvClientName.isEnabled = !serviceTypeItem.custom
                    binding.tvClientNameLabel.isEnabled = !serviceTypeItem.custom
                    if (serviceTypeItem.custom) {
                        clearClientSelection()
                    } else {
                        getClientsForShiftPopup()
                    }
                }
            }
        }
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }

        override fun afterTextChanged(s: Editable?) {
            isEdited = true

            if (binding.tvStartDate.text.hashCode() == s.hashCode() || binding.tvEndDate.text.hashCode() == s.hashCode() || binding.tvStartTime.text.hashCode() == s.hashCode() || binding.tvEndTime.text.hashCode() == s.hashCode()) {
                //isOtherFieldsEdited = true;
                if (binding.tvStartDate.text.toString()
                        .isNotEmpty() && binding.tvEndDate.text.toString()
                        .isNotEmpty() && binding.tvStartTime.text.toString()
                        .isNotEmpty() || binding.tvEndTime.text.toString().isNotEmpty()
                ) {
                    totalHours = calculateTotalHours().toString()
                    binding.tvTotalHours.text = totalHours
                }
            }
        }
    }

    private fun calculateTotalHours(): Double {
        val startTime = binding.tvStartTime.text.toString()
        val endTime = binding.tvEndTime.text.toString()
        val startDate = binding.tvStartDate.text.toString()
        val endDate = binding.tvEndDate.text.toString()

        QSCalendar.run {
            val startDateTime = formatDate(
                "$startDate $startTime", QSCalendar.DateFormats.MMDDYYYYHHMMA.label
            )
            val endDateTime = formatDate(
                "$endDate $endTime", QSCalendar.DateFormats.MMDDYYYYHHMMA.label
            )

            var totalMinute = 0.0
            if (endDateTime != null && startDateTime != null) {
                totalMinute = timeDifferenceInMinutes(endDateTime, startDateTime).toDouble()
            }

            return DecimalFormat("#.##").format(totalMinute / 60).toDouble()
        }
    }

    private fun updateChangeReasonLayoutVisibility() {
        if (isEvvServiceType) {
            binding.clockDetails.llChangeReason.isVisible = true
            isChangeReasonVisible = true
        } else {
            binding.clockDetails.llChangeReason.isVisible = false
            isChangeReasonVisible = false
        }
    }

    private fun visibleShowMileage(visible: Boolean) {
        binding.clMileage.isVisible = visible
        binding.ivDropDown.rotation = if (visible) 180f else 360f
        this.isShowMileage = !visible
    }

    private fun checkForShiftSubCodes(showProgress: Boolean = true) {
        binding.clShiftSubCodes.isVisible = true
        if (!isNewShift) {
            getShiftSubCodes(showProgress)
        }
    }

    private fun checkForShiftObjectives(showProgress: Boolean = true) {
        binding.clShiftObjective.isVisible = true
        if (!isNewShift) {
            getShiftObjectives(showProgress)
        }
    }

    private fun clearWorkerSelection() {
        isWorkerChanged = true
        employee = null
        if (binding.tvEmployeeName.text.trim().isNotEmpty()) {
            binding.tvEmployeeName.text = ""
        }
    }

    private fun clearClientSelection() {
        isClientChanged = true
        if (binding.tvClientName.text.trim().isNotEmpty()) {
            binding.tvClientName.text = ""
        }
    }

    private fun isValidTime(): Boolean {
        val startTime = binding.tvStartTime.text.toString().trim()
        val endTime = binding.tvEndTime.text.toString().trim()

        if (startTime.isEmpty()) {
            QSAlert(
                context = this,
                title = getString(R.string.str_error),
                message = getString(R.string.str_enter_valid_start_time),
                positiveButtonText = getString(R.string.str_ok)
            ).show()
            return false
        }
        if (endTime.isEmpty()) {
            QSAlert(
                context = this,
                title = getString(R.string.str_error),
                message = getString(R.string.str_enter_valid_end_time),
                positiveButtonText = getString(R.string.str_ok)
            ).show()
            return false
        }

        val startDate = binding.tvStartDate.text.toString().trim()
        val endDate = binding.tvEndDate.text.toString().trim()

        QSCalendar.run {
            val startDateTime =
                formatDate("$startDate $startTime", QSCalendar.DateFormats.MMDDYYYYHHMMA.label)
            val endDateTime =
                formatDate("$endDate $endTime", QSCalendar.DateFormats.MMDDYYYYHHMMA.label)

            timeDiff = calculateTotalHours()
            if (timeDiff == 0.0) {
                timeDiff = 24.0
            }

            if (endDateTime != null && startDateTime != null && endDateTime.time <= startDateTime.time) {
                QSAlert(
                    context = this@ScheduleShiftDetailsActivity,
                    title = getString(R.string.str_error),
                    message = getString(R.string.str_end_time_not_earlier_start_time),
                    positiveButtonText = getString(R.string.str_ok)
                ).show()
                return false
            }

            scheduleItem?.run {
                val originalStartTime = getFormattedTimings(this.startTime)
                val originalEndTime = getFormattedTimings(this.endTime)
                val originalStartDate = this.schedDate
                val originalEndDate = this.schedDateEnd

                val originalStartDateTime = formatDate(
                    "$originalStartDate $originalStartTime",
                    QSCalendar.DateFormats.MMDDYYYYHHMMA.label
                )
                val originalEndDateTime = formatDate(
                    "$originalEndDate $originalEndTime", QSCalendar.DateFormats.MMDDYYYYHHMMA.label
                )

                if (startDateTime?.time != originalStartDateTime?.time || endDateTime?.time != originalEndDateTime?.time) {
                    isShiftTimeChanged = true
                }

                if (!isNewShift) {
                    if (this.hasMealBreak == 1) {
                        val mIn = getFormattedTimings(this.mealIn)
                        val mOut = getFormattedTimings(this.mealOut)

                        if (binding.tvTimeOut.text.toString() != mOut || binding.tvTimeIn.text.toString() != mIn) {
                            isMealTimeChanged = true
                        }
                    } else if (binding.cbRestPeriod.isSelected) {
                        isMealTimeChanged = true
                    }
                }

                isServiceTypeChanged =
                    !isNewShift && (this.taskName != binding.tvServiceType.text.toString())
            }
        }

        return true
    }

    private fun getShiftSubCodesAdapter(): ArrayAdapter<ShiftSubCodesItem> {
        return object : ArrayAdapter<ShiftSubCodesItem>(
            /* context = */ this,
            /* resource = */ R.layout.layout_item_list,
            /* textViewResourceId = */ R.id.tv,
            /* objects = */ shiftSubCodes
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val textView = view.findViewById<TextView>(R.id.tv)
                val subCode = shiftSubCodes[position]
                val text = "${subCode.subCode} - ${subCode.subCodeTime}"
                textView.text = text
                return view
            }
        }
    }

    private fun getShiftObjectivesAdapter(): ArrayAdapter<ShiftObjectivesItem> {
        return object : ArrayAdapter<ShiftObjectivesItem>(
            /* context = */ this,
            /* resource = */ R.layout.layout_item_list,
            /* textViewResourceId = */ R.id.tv,
            /* objects = */ shiftObjectives
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val textView = view.findViewById<TextView>(R.id.tv)
                val objective = shiftObjectives[position]
                val text = "${objective.objective} - ${objective.objectiveTime}"
                textView.text = text
                return view
            }
        }
    }

    override fun getLocation(mLastLocation: Location?) {
        TODO("Not yet implemented")
    }
}