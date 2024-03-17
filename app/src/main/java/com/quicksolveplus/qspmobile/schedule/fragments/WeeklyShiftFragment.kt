package com.quicksolveplus.qspmobile.schedule.fragments

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.gson.Gson
import com.quicksolveplus.api.Api
import com.quicksolveplus.api.RequestParameters
import com.quicksolveplus.api.ResponseStatus
import com.quicksolveplus.modifiers.Actifiers.openActivityForResult
import com.quicksolveplus.modifiers.Actifiers.registerActivityResultLauncher
import com.quicksolveplus.qspmobile.R
import com.quicksolveplus.qspmobile.clients.models.ClientsItem
import com.quicksolveplus.qspmobile.databinding.FragmentWeeklyShiftBinding
import com.quicksolveplus.qspmobile.employee.model.EmployeesItem
import com.quicksolveplus.qspmobile.schedule.ScheduleShiftDetailsActivity
import com.quicksolveplus.qspmobile.schedule.models.*
import com.quicksolveplus.qspmobile.schedule.viewmodel.WeeklyShiftViewModel
import com.quicksolveplus.utils.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * 12/04/23.
 *
 * @author hardkgosai.
 */
class WeeklyShiftFragment(
    private val isClient: Boolean = false,
    private val fromPreferences: Boolean = false,
    private val client: ClientsItem? = null,
    private val employee: EmployeesItem? = null
) : Fragment() {

    private lateinit var binding: FragmentWeeklyShiftBinding
    private val viewModel: WeeklyShiftViewModel by viewModels()

    private val weekDateArray = arrayListOf<String>()
    private val schedulesByWeekItems = arrayListOf<SchedulesItem>()
    private val torScheduleByWeekItems = arrayListOf<TORScheduleItem>()
    private val preferenceScheduleByWeekItems = arrayListOf<PreferenceScheduleItem>()
    private val weeklyCalendar = Calendar.getInstance()
    private var currentWeek = 0
    private var currentYear = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentWeeklyShiftBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        setObservers()
        getCurrentWeekDays()
        fetchScheduleService()
    }

    private fun initUI() {
        binding.fbAdd.setOnClickListener { }
        binding.titleLayout.ivPrevious.setOnClickListener { changeCalendar(false) }
        binding.titleLayout.ivNext.setOnClickListener { changeCalendar(true) }
        binding.tvSunday.apply {
            setOnClickListener {
                if (isCurrentDay(weekDateArray[0])) {
                    updateCalendarDates(weekDateArray[0])
                    updateWeekHeader(it)
                }
            }
        }
        binding.tvMonday.apply {
            setOnClickListener {
                if (isCurrentDay(weekDateArray[1])) {
                    updateCalendarDates(weekDateArray[1])
                    updateWeekHeader(it)
                }
            }
        }
        binding.tvTuesday.apply {
            setOnClickListener {
                if (isCurrentDay(weekDateArray[2])) {
                    updateCalendarDates(weekDateArray[2])
                    updateWeekHeader(it)
                }
            }
        }
        binding.tvWednesday.apply {
            setOnClickListener {
                if (isCurrentDay(weekDateArray[3])) {
                    updateCalendarDates(weekDateArray[3])
                    updateWeekHeader(it)
                }
            }
        }
        binding.tvThursday.apply {
            setOnClickListener {
                if (isCurrentDay(weekDateArray[4])) {
                    updateCalendarDates(weekDateArray[4])
                    updateWeekHeader(it)
                }
            }
        }
        binding.tvFriday.setOnClickListener {
            if (isCurrentDay(weekDateArray[5])) {
                updateCalendarDates(weekDateArray[5])
                updateWeekHeader(it)
            }
        }
        binding.tvSaturday.apply {
            setOnClickListener {
                if (isCurrentDay(weekDateArray[6])) {
                    updateCalendarDates(weekDateArray[6])
                    updateWeekHeader(it)
                }
            }
        }

        QSPermissions.run {
            if (isAdmin || hasScheduleModificationCreatePermission()) {
                binding.fbAdd.show()
            } else {
                binding.fbAdd.hide()
            }
        }
    }

    private fun changeCalendar(isNext: Boolean) {
        QSCalendar.run {
            val currentDate = formatCurrentDate(Date(), QSCalendar.DateFormats.MMYYYY.label)
            if (currentDate != null) {

                val calendar = Calendar.getInstance()
                calendar.time = currentDate
                calendar.add(Calendar.MONTH, if (isNext) 2 else -1)
                val newDate = formatCurrentDate(
                    calendar.time, QSCalendar.DateFormats.MMYYYY.label
                )

                val selectedDate = formatDate(
                    weekDateArray[if (isNext) 6 else 0], QSCalendar.DateFormats.MMDDYY.label
                )

                QSPermissions.run {
                    if (isAdmin || !hasPriorCurrentNextMonthViewOnlyReadPermission()) {
                        getWeekDays(forNext = isNext)
                    } else if (selectedDate != null) {
                        if (isNext) {
                            if (currentDate == selectedDate || selectedDate.before(newDate)) {
                                getWeekDays(true)
                            }
                        } else {
                            if (currentDate == selectedDate || selectedDate.after(newDate)) {
                                getWeekDays(false)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun isCurrentDay(date: String): Boolean {
        QSCalendar.run {
            val currentDate = formatCurrentDate(Date(), QSCalendar.DateFormats.MMYYYY.label)
            if (currentDate != null) {
                val previousCalendar = Calendar.getInstance()
                previousCalendar.time = currentDate
                previousCalendar.add(Calendar.MONTH, -1)
                val previousDate = formatCurrentDate(
                    previousCalendar.time, QSCalendar.DateFormats.MMDDYY.label
                )

                val nextCalendar = Calendar.getInstance()
                nextCalendar.time = currentDate
                nextCalendar.add(Calendar.MONTH, 2)
                nextCalendar.add(Calendar.DATE, -1)
                val nextDate = formatCurrentDate(
                    nextCalendar.time, QSCalendar.DateFormats.MMDDYY.label
                )

                val selectedDate = formatDate(date, QSCalendar.DateFormats.MMDDYY.label)
                if (selectedDate != null) {
                    if (selectedDate == previousDate || selectedDate == nextDate || (selectedDate.after(
                            previousDate
                        ) && selectedDate.before(nextDate))
                    ) {
                        return true
                    } else {
                        QSPermissions.run {
                            return (isAdmin || hasPriorCurrentNextMonthViewOnlyReadPermission())
                        }
                    }
                }
            }
        }
        return false
    }

    private fun setObservers() {
        viewModel.getResponseStatus().observe(viewLifecycleOwner) {
            when (it) {
                is ResponseStatus.Running -> {
                    if (it.apiName != Api.getClientProfilePic) {
                        showQSProgress(requireActivity())
                    }
                }
                is ResponseStatus.Success -> {
                    proceedResponse(it)
                    dismissQSProgress()
                }
                is ResponseStatus.Failed -> {
                    if (it.msg == getString(R.string.msg_server_error)) {
                        toast(requireContext(), it.msg)
                    }
                    proceedFailure(it)
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
            Api.getStaffPreferenceForScheduleByWeek -> {
                if (success.data is PreferenceSchedule) {
                    proceedStaffPreferenceForScheduleByWeek(success.data)
                }
            }
            Api.getStaffTORForScheduleByWeek -> {
                if (success.data is TORSchedule) {
                    getSchedulesByWeek() //after tor fetched, call scheduled by weeks.
                    proceedStaffTORForScheduleByWeek(success.data)
                }
            }
            Api.getSchedulesByWeek -> {
                if (success.data is Schedules) {
                    proceedSchedulesByWeek(success.data)
                }
            }
        }
    }

    private fun proceedFailure(failure: ResponseStatus.Failed) {
        when (failure.apiName) {
            Api.getStaffTORForScheduleByWeek -> {
                getSchedulesByWeek()
            }
        }
    }

    private fun proceedStaffPreferenceForScheduleByWeek(preferenceSchedule: PreferenceSchedule) {
        if (preferenceSchedule.isNotEmpty() && preferenceScheduleByWeekItems.isNotEmpty()) {
            preferenceScheduleByWeekItems.clear()
        }
        preferenceSchedule.map { item ->
            QSCalendar.run {
                val dateStart = formatDate(
                    item.schedDate, QSCalendar.DateFormats.MMDDYY.label
                )
                val dateEnd = formatDate(
                    item.schedDate, QSCalendar.DateFormats.MMDDYY.label
                )

                var startTime: String?
                var endTime: String?
                var startDate: String?
                var endDate: String?

                if (dateStart != null && dateEnd != null) {
                    if (dateStart.time == dateEnd.time) {

                        startTime = getFormattedTimings(item.startTime)
                        endTime = getFormattedTimings(item.endTime)
                        addToShift(item, startTime, endTime, item.schedDate, item.schedDate)

                    } else {

                        startTime = getFormattedTimings(item.startTime)
                        endTime = "11:59 PM"
                        startDate = item.schedDate
                        endDate = item.schedDate
                        addToShift(item, startTime, endTime, startDate, endDate)

                        val shiftDays = timeDifferenceInDays(dateEnd, dateStart)

                        for (i in 0 until shiftDays) {
                            if (i == shiftDays - 1) {
                                startTime = "12:00 AM"
                                endTime = getFormattedTimings(item.endTime)
                                if (endTime != "12:00 AM") {
                                    startDate = item.schedDate
                                    endDate = item.schedDate
                                    addToShift(item, startTime, endTime, startDate, endDate)
                                }
                            } else if (startDate != null) {
                                val sdf =
                                    SimpleDateFormat(QSCalendar.DateFormats.MMDDYY.label, Locale.US)
                                val conStartDate = sdf.parse(startDate)

                                if (conStartDate != null) {
                                    val c = Calendar.getInstance()
                                    c.time = conStartDate
                                    c.add(Calendar.DATE, 1) // number of days to add
                                    startDate = sdf.format(c.time) // dt is now the new date
                                    endDate = sdf.format(c.time)
                                    startTime = "12:00 AM"
                                    endTime = "11:59 PM"
                                    addToShift(item, startTime, endTime, startDate, endDate)
                                }
                            }
                        }
                    }
                }
            }
        }
        displayPreferenceScheduleShifts()
    }

    private fun proceedStaffTORForScheduleByWeek(torSchedule: TORSchedule) {
        if (torSchedule.isNotEmpty() && torScheduleByWeekItems.isNotEmpty()) {
            torScheduleByWeekItems.clear()
        }

        torSchedule.map { item ->
            QSCalendar.run {
                val dateStart = formatDate(
                    item.schedDate, QSCalendar.DateFormats.MMDDYY.label
                )
                val dateEnd = formatDate(
                    item.schedDateEnd, QSCalendar.DateFormats.MMDDYY.label
                )

                var startTime: String?
                var endTime: String?
                var startDate: String?
                var endDate: String?

                if (dateStart != null && dateEnd != null) {
                    if (dateStart.time == dateEnd.time) {

                        startTime = getFormattedTimings(item.startTime)
                        endTime = getFormattedTimings(item.endTime)
                        addToShift(item, startTime, endTime, item.schedDate, item.schedDateEnd)

                    } else {

                        startTime = getFormattedTimings(item.startTime)
                        endTime = "11:59 PM"
                        startDate = item.schedDate
                        endDate = item.schedDate
                        addToShift(item, startTime, endTime, startDate, endDate)

                        val shiftDays = timeDifferenceInDays(dateEnd, dateStart)

                        for (i in 0 until shiftDays) {
                            if (i == shiftDays - 1) {
                                startTime = "12:00 AM"
                                endTime = getFormattedTimings(item.endTime)
                                if (endTime != "12:00 AM") {
                                    startDate = item.schedDateEnd
                                    endDate = item.schedDateEnd
                                    addToShift(item, startTime, endTime, startDate, endDate)
                                }
                            } else if (startDate != null) {
                                val sdf =
                                    SimpleDateFormat(QSCalendar.DateFormats.MMDDYY.label, Locale.US)
                                val conStartDate = sdf.parse(startDate)

                                if (conStartDate != null) {
                                    val c = Calendar.getInstance()
                                    c.time = conStartDate
                                    c.add(Calendar.DATE, 1) // number of days to add
                                    startDate = sdf.format(c.time) // dt is now the new date
                                    endDate = sdf.format(c.time)
                                    startTime = "12:00 AM"
                                    endTime = "11:59 PM"
                                    addToShift(item, startTime, endTime, startDate, endDate)
                                }
                            }
                        }
                    }
                }
            }
        }
        displayTORScheduleShifts()
    }

    private fun proceedSchedulesByWeek(schedules: Schedules) {
        if (schedules.isNotEmpty() && schedulesByWeekItems.isNotEmpty()) {
            schedulesByWeekItems.clear()
        }
        sortSchedulesByWeek(schedules)
        schedules.map { item ->

            QSCalendar.run {
                val dateStart = formatDate(
                    item.schedDate, QSCalendar.DateFormats.MMDDYY.label
                )
                val dateEnd = formatDate(
                    item.schedDateEnd, QSCalendar.DateFormats.MMDDYY.label
                )

                var startTime: String?
                var endTime: String?
                var startDate: String?
                var endDate: String?

                if (dateStart != null && dateEnd != null) {

                    val isPermitted =
                        item.clientLinkUID == 0 || (item.clientLinkUID == item.uID || isClient)

                    if (dateStart.time == dateEnd.time) {

                        startTime = getFormattedTimings(item.startTime)
                        endTime = getFormattedTimings(item.endTime)
                        if (isPermitted) {
                            addToShift(
                                schedules,
                                item,
                                startTime,
                                endTime,
                                item.schedDate,
                                item.schedDateEnd
                            )
                        }

                    } else {

                        startTime = getFormattedTimings(item.startTime)
                        endTime = "11:59 PM"
                        startDate = item.schedDate
                        endDate = item.schedDate
                        if (isPermitted) {
                            addToShift(schedules, item, startTime, endTime, startDate, endDate)
                        }

                        val shiftDays = timeDifferenceInDays(dateEnd, dateStart)

                        for (i in 0 until shiftDays) {
                            if (i == shiftDays - 1) {
                                startTime = "12:00 AM"
                                endTime = getFormattedTimings(item.endTime)
                                if (endTime != "12:00 AM") {
                                    startDate = item.schedDateEnd
                                    endDate = item.schedDateEnd
                                    if (isPermitted) {
                                        addToShift(
                                            schedules, item, startTime, endTime, startDate, endDate
                                        )
                                    }
                                }
                            } else if (startDate != null) {
                                val sdf =
                                    SimpleDateFormat(QSCalendar.DateFormats.MMDDYY.label, Locale.US)
                                val conStartDate = sdf.parse(startDate)

                                if (conStartDate != null) {
                                    val c = Calendar.getInstance()
                                    c.time = conStartDate
                                    c.add(Calendar.DATE, 1) // number of days to add
                                    startDate = sdf.format(c.time) // dt is now the new date
                                    endDate = sdf.format(c.time)
                                    startTime = "12:00 AM"
                                    endTime = "11:59 PM"
                                    if (isPermitted) {
                                        addToShift(
                                            schedules, item, startTime, endTime, startDate, endDate
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        displaySchedulesShifts()
    }

    private fun sortSchedulesByWeek(schedules: Schedules) {
        Collections.sort(schedules) { o1, o2 ->
            QSCalendar.run {
                val startDate1 = formatDate(
                    o1.schedDate + " " + getFormattedTimings(o1.startTime),
                    QSCalendar.DateFormats.MMDDYYHHMMA.label
                )
                val endDate1 = formatDate(
                    o1.schedDateEnd + " " + getFormattedTimings(o1.endTime),
                    QSCalendar.DateFormats.MMDDYYHHMMA.label
                )

                val startDate2 = formatDate(
                    o2.schedDate + " " + getFormattedTimings(o2.startTime),
                    QSCalendar.DateFormats.MMDDYYHHMMA.label
                )
                val endDate2 = formatDate(
                    o2.schedDateEnd + " " + getFormattedTimings(o2.endTime),
                    QSCalendar.DateFormats.MMDDYYHHMMA.label
                )

                var hoursDiff1 = 0L
                var hoursDiff2 = 0L
                if (startDate1 != null && endDate1 != null) {
                    hoursDiff1 = timeDifferenceInHours(endDate1, startDate1)
                }
                if (startDate2 != null && endDate2 != null) {
                    hoursDiff2 = timeDifferenceInHours(endDate2, startDate2)
                }
                if (hoursDiff1 < hoursDiff2) 1
                else if (hoursDiff1 == hoursDiff2) 0
                else -1
            }
        }
    }

    private fun addToShift(
        preferenceScheduleItem: PreferenceScheduleItem,
        startTime: String?,
        endTime: String?,
        startDate: String?,
        endDate: String?
    ) {
        val item = preferenceScheduleItem.clone() as PreferenceScheduleItem
        item.tempStartTime = startTime
        item.tempEndTime = endTime
        item.tempSchedDate = startDate
        item.tempSchedDateEnd = endDate
        preferenceScheduleByWeekItems.add(item)
    }

    private fun addToShift(
        torScheduleItem: TORScheduleItem,
        startTime: String?,
        endTime: String?,
        startDate: String?,
        endDate: String?
    ) {
        val item = torScheduleItem.clone() as TORScheduleItem
        item.tempStartTime = startTime
        item.tempEndTime = endTime
        item.tempSchedDate = startDate
        item.tempSchedDateEnd = endDate
        torScheduleByWeekItems.add(item)
    }

    private fun addToShift(
        schedules: Schedules,
        schedulesItem: SchedulesItem,
        startTime: String?,
        endTime: String?,
        startDate: String?,
        endDate: String?
    ) {
        val item = schedulesItem.clone() as SchedulesItem
        item.tempStartTime = startTime
        item.tempEndTime = endTime
        item.tempSchedDate = startDate
        item.tempSchedDateEnd = endDate
        schedulesByWeekItems.add(item)

        item.linkedSchedules.map { o1 ->
            schedules.map { o2 ->
                if (o2.uID == o1.linkedScheduleID) {
                    o2.tempStartTime = startDate
                    o2.tempEndTime = endTime
                    o2.tempSchedDate = startDate
                    o2.tempSchedDateEnd = endDate
                    schedulesByWeekItems.removeAt(schedulesByWeekItems.size - 1)
                    schedulesByWeekItems.add(o2)
                }
            }
        }
    }

    private fun fetchScheduleService() {
        clearCalendarViews()

        if (fromPreferences) {
            if (!isClient) {
                binding.fbAdd.hide()
                getStaffPreferenceForScheduleByWeek()
            }
        } else {
            if (isClient) {
                getSchedulesByWeek()
            } else {
                getStaffTORForScheduleByWeek()
            }
        }
    }

    private fun getStaffPreferenceForScheduleByWeek() {
        employee?.run {
            QSCalendar.run {
                val startDate = formatDate(weekDateArray[0], QSCalendar.DateFormats.MMDDYY.label)
                val endDate = formatDate(
                    weekDateArray[weekDateArray.size - 1], QSCalendar.DateFormats.MMDDYY.label
                )
                val monthStart = getDateAsInt(startDate, QSCalendar.DateFormats.MM.label)
                val dayStart = getDateAsInt(startDate, QSCalendar.DateFormats.d.label)
                val yearStart = getDateAsInt(startDate, QSCalendar.DateFormats.YYYY.label)
                val monthEnd = getDateAsInt(endDate, QSCalendar.DateFormats.MM.label)
                val dayEnd = getDateAsInt(endDate, QSCalendar.DateFormats.d.label)
                val yearEnd = getDateAsInt(endDate, QSCalendar.DateFormats.YYYY.label)

                val body = RequestParameters.forStaffPreferenceForScheduleByWeek(
                    staffUID = UsersUID,
                    dMonthStart = monthStart,
                    dDayStart = dayStart,
                    dYearStart = yearStart,
                    dMonthEnd = monthEnd,
                    dDayEnd = dayEnd,
                    dYearEnd = yearEnd,
                )
                viewModel.getStaffPreferenceForScheduleByWeek(body)
            }
        }
    }

    private fun getStaffTORForScheduleByWeek() {
        employee?.run {
            QSCalendar.run {
                val startDate = formatDate(weekDateArray[0], QSCalendar.DateFormats.MMDDYY.label)
                val endDate = formatDate(
                    weekDateArray[weekDateArray.size - 1], QSCalendar.DateFormats.MMDDYY.label
                )
                val monthStart = getDateAsInt(startDate, QSCalendar.DateFormats.MM.label)
                val dayStart = getDateAsInt(startDate, QSCalendar.DateFormats.d.label)
                val yearStart = getDateAsInt(startDate, QSCalendar.DateFormats.YYYY.label)
                val monthEnd = getDateAsInt(endDate, QSCalendar.DateFormats.MM.label)
                val dayEnd = getDateAsInt(endDate, QSCalendar.DateFormats.d.label)
                val yearEnd = getDateAsInt(endDate, QSCalendar.DateFormats.YYYY.label)

                val body = RequestParameters.forStaffTORForScheduleByWeek(
                    staffUID = UsersUID,
                    dMonthStart = monthStart,
                    dDayStart = dayStart,
                    dYearStart = yearStart,
                    dMonthEnd = monthEnd,
                    dDayEnd = dayEnd,
                    dYearEnd = yearEnd,
                )
                viewModel.getStaffTORForScheduleByWeek(body)
            }
        }
    }

    private fun getSchedulesByWeek() {
        QSCalendar.run {
            val startDate = formatDate(weekDateArray[0], QSCalendar.DateFormats.MMDDYY.label)
            val endDate = formatDate(
                weekDateArray[weekDateArray.size - 1], QSCalendar.DateFormats.MMDDYY.label
            )
            val monthStart = getDateAsInt(startDate, QSCalendar.DateFormats.MM.label)
            val dayStart = getDateAsInt(startDate, QSCalendar.DateFormats.d.label)
            val yearStart = getDateAsInt(startDate, QSCalendar.DateFormats.YYYY.label)
            val monthEnd = getDateAsInt(endDate, QSCalendar.DateFormats.MM.label)
            val dayEnd = getDateAsInt(endDate, QSCalendar.DateFormats.d.label)
            val yearEnd = getDateAsInt(endDate, QSCalendar.DateFormats.YYYY.label)

            val staffUId = if (isClient) 0 else employee?.UsersUID
            val clientUId = if (isClient) client?.ClientUID else 0

            val body = RequestParameters.forStaffSchedulesByWeek(
                staffUID = staffUId,
                clientUID = clientUId,
                dMonthStart = monthStart,
                dDayStart = dayStart,
                dYearStart = yearStart,
                dMonthEnd = monthEnd,
                dDayEnd = dayEnd,
                dYearEnd = yearEnd,
                isForDsn = false
            )
            viewModel.getSchedulesByWeek(body)
        }
    }

    private fun getCurrentWeekDays() {
        var selectedDate = QSCalendar.calendarDate
        if (selectedDate == null) {
            QSCalendar.calendarDate = QSCalendar.formatDate(
                weekDateArray[0],
                QSCalendar.DateFormats.MMDDYY.label,
                QSCalendar.DateFormats.DDMMYYYY.label
            )
            selectedDate = QSCalendar.calendarDate
        }

        selectedDate?.run {
            val splitSelectedDate = split("/")
            val selectedDay = splitSelectedDate[0].toInt()
            val selectedMonth = splitSelectedDate[1].toInt()
            val selectedYear = splitSelectedDate[2].toInt()

            weeklyCalendar[Calendar.DAY_OF_MONTH] = selectedDay
            weeklyCalendar[Calendar.MONTH] = selectedMonth - 1
            weeklyCalendar[Calendar.YEAR] = QSCalendar.calendarYear?.toInt() ?: selectedYear

            val weekOfMonth = weeklyCalendar[Calendar.WEEK_OF_MONTH]
            val month = weeklyCalendar[Calendar.MONTH]
            val year = weeklyCalendar[Calendar.YEAR]

            weeklyCalendar.clear()
            weeklyCalendar.firstDayOfWeek = Calendar.SUNDAY
            weeklyCalendar[Calendar.WEEK_OF_MONTH] = weekOfMonth
            weeklyCalendar[Calendar.MONTH] = month
            weeklyCalendar[Calendar.YEAR] = year

            currentWeek = weeklyCalendar[Calendar.WEEK_OF_YEAR]
            currentYear = weeklyCalendar[Calendar.YEAR]

            weekDateArray.clear()
            for (i in 0..6) {
                weekDateArray.add(
                    QSCalendar.formatDate(
                        weeklyCalendar.time, QSCalendar.DateFormats.MMDDYY.label
                    )
                )
                weeklyCalendar.add(Calendar.DAY_OF_WEEK, 1)
            }

            initCalendarDates(weekDateArray)
            setWeekHeader()
        }
    }

    private fun getWeekDays(forNext: Boolean) {
        clearCalendarSelection()
        clearCalendarViews()

        val weekOfMonth = weeklyCalendar[Calendar.WEEK_OF_MONTH]
        val month = weeklyCalendar[Calendar.MONTH]
        val year = weeklyCalendar[Calendar.YEAR]

        weeklyCalendar.clear()
        weeklyCalendar.firstDayOfWeek = Calendar.SUNDAY
        weeklyCalendar[Calendar.WEEK_OF_MONTH] = weekOfMonth
        weeklyCalendar[Calendar.MONTH] = month
        weeklyCalendar[Calendar.YEAR] = year

        if (forNext) {
            if (currentWeek == 52 && (currentYear % 6 == 0)) {
                currentWeek = 53
            } else if (currentWeek == 52) {
                currentWeek = 1
                currentYear += 1
            } else if (currentWeek == 53) {
                currentWeek = 1
                currentYear += 1
            } else {
                currentWeek += 1
            }
        } else {
            if (currentWeek == 1 && (currentYear - 1) % 6 == 0) {
                currentWeek = 53
                currentYear -= 1
            } else if (currentWeek == 1 && (currentYear - 1) % 6 != 0) {
                currentWeek = 52
                currentYear -= 1
            } else {
                currentWeek -= 1
            }
        }

        weeklyCalendar[Calendar.WEEK_OF_YEAR] = currentWeek
        weeklyCalendar[Calendar.YEAR] = currentYear

        weekDateArray.clear()
        for (i in 0..6) {
            weekDateArray.add(
                QSCalendar.formatDate(
                    weeklyCalendar.time, QSCalendar.DateFormats.MMDDYY.label
                )
            )
            weeklyCalendar.add(Calendar.DAY_OF_WEEK, 1)
        }

        initCalendarDates(weekDateArray)
        setWeekHeader()
        fetchScheduleService()
    }

    private fun initCalendarDates(array: ArrayList<String>) {
        QSCalendar.run {
            val date = formatDate(
                calendarDate,
                QSCalendar.DateFormats.DDMMYYYY.label,
                QSCalendar.DateFormats.MMDDYY.label
            )

            updateCalendarDates(date)

            binding.tvSunday.run {
                val isSelected = array[0] == date
                text = convertWeekDays(array[0])
                tag = formatMMDDYYMMM(array[0])
                setBackgroundResource(getSelectedBackgroundResource(isSelected))
                setTextColor(getSelectedTextColor(isSelected))
            }
            binding.tvMonday.run {
                val isSelected = array[1] == date
                text = convertWeekDays(array[1])
                tag = formatMMDDYYMMM(array[1])
                setBackgroundResource(getSelectedBackgroundResource(isSelected))
                setTextColor(getSelectedTextColor(isSelected))
            }
            binding.tvTuesday.run {
                val isSelected = array[2] == date
                text = convertWeekDays(array[2])
                tag = formatMMDDYYMMM(array[2])
                setBackgroundResource(getSelectedBackgroundResource(isSelected))
                setTextColor(getSelectedTextColor(isSelected))
            }
            binding.tvWednesday.run {
                val isSelected = array[3] == date
                text = convertWeekDays(array[3])
                tag = formatMMDDYYMMM(array[3])
                setBackgroundResource(getSelectedBackgroundResource(isSelected))
                setTextColor(getSelectedTextColor(isSelected))
            }
            binding.tvThursday.run {
                val isSelected = array[4] == date
                text = convertWeekDays(array[4])
                tag = formatMMDDYYMMM(array[4])
                setBackgroundResource(getSelectedBackgroundResource(isSelected))
                setTextColor(getSelectedTextColor(isSelected))
            }
            binding.tvFriday.run {
                val isSelected = array[5] == date
                text = convertWeekDays(array[5])
                tag = formatMMDDYYMMM(array[5])
                setBackgroundResource(getSelectedBackgroundResource(isSelected))
                setTextColor(getSelectedTextColor(isSelected))
            }
            binding.tvSaturday.run {
                val isSelected = array[6] == date
                text = convertWeekDays(array[6])
                tag = formatMMDDYYMMM(array[6])
                setBackgroundResource(getSelectedBackgroundResource(isSelected))
                setTextColor(getSelectedTextColor(isSelected))
            }
        }
    }

    private fun updateCalendarDates(date: String) {
        QSCalendar.run {
            calendarDate = formatDate(
                date, QSCalendar.DateFormats.MMDDYY.label, QSCalendar.DateFormats.DDMMYYYY.label
            )
            calendarDate?.run {
                val days = split("/")
                calendarMonth = days[1]
                calendarYear = days[2]

                val calendar = Calendar.getInstance()
                calendar.set(Calendar.YEAR, days[2].toInt())
                calendar.set(Calendar.MONTH, days[1].toInt() - 1)
                calendar.set(Calendar.DATE, days[0].toInt())
                calendarWeek = calendar.get(Calendar.WEEK_OF_YEAR)
            }
        }
    }

    private fun updateWeekHeader(selectedView: View) {
        binding.tvSunday.apply {
            setTextColor(getSelectedTextColor(this == selectedView))
            setBackgroundResource(getSelectedBackgroundResource(this == selectedView))
        }
        binding.tvMonday.apply {
            setTextColor(getSelectedTextColor(this == selectedView))
            setBackgroundResource(getSelectedBackgroundResource(this == selectedView))
        }
        binding.tvTuesday.apply {
            setTextColor(getSelectedTextColor(this == selectedView))
            setBackgroundResource(getSelectedBackgroundResource(this == selectedView))
        }
        binding.tvWednesday.apply {
            setTextColor(getSelectedTextColor(this == selectedView))
            setBackgroundResource(getSelectedBackgroundResource(this == selectedView))
        }
        binding.tvThursday.apply {
            setTextColor(getSelectedTextColor(this == selectedView))
            setBackgroundResource(getSelectedBackgroundResource(this == selectedView))
        }
        binding.tvFriday.apply {
            setTextColor(getSelectedTextColor(this == selectedView))
            setBackgroundResource(getSelectedBackgroundResource(this == selectedView))
        }
        binding.tvSaturday.apply {
            setTextColor(getSelectedTextColor(this == selectedView))
            setBackgroundResource(getSelectedBackgroundResource(this == selectedView))
        }
    }

    private fun setWeekHeader() {
        val first = binding.tvSunday.text.toString()
        val last = binding.tvSaturday.text.toString()

        val monthFirst = binding.tvSunday.tag as String
        val monthLast = binding.tvSaturday.tag as String

        if (monthFirst.lowercase() == monthLast.lowercase()) {
            binding.titleLayout.tvDateTitle.text =
                String.format("%s %s - %s", monthFirst, first, last)
        } else {
            binding.titleLayout.tvDateTitle.text =
                String.format("%s %s - %s %s", monthFirst, first, monthLast, last)
        }
    }

    private fun convertWeekDays(date: String): String? {
        var formattedDate: String? = null
        try {
            formattedDate = QSCalendar.formatDate(
                date, QSCalendar.DateFormats.MMDDYY.label, QSCalendar.DateFormats.d.label
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return formattedDate
    }

    private fun formatMMDDYYMMM(value: String): String {
        return QSCalendar.formatDate(
            value, QSCalendar.DateFormats.MMDDYY.label, QSCalendar.DateFormats.MMM.label
        )
    }

    private fun getSelectedBackgroundResource(isSelected: Boolean): Int {
        return if (isSelected) R.drawable.circle_app_color else 0
    }

    private fun getSelectedTextColor(isSelected: Boolean): Int {
        return ContextCompat.getColor(
            requireContext(), if (isSelected) R.color.white else R.color.black
        )
    }

    private fun clearCalendarSelection() {
        binding.tvSunday.run {
            setBackgroundResource(getSelectedBackgroundResource(false))
            setTextColor(getSelectedTextColor(false))
        }
        binding.tvMonday.run {
            setBackgroundResource(getSelectedBackgroundResource(false))
            setTextColor(getSelectedTextColor(false))
        }
        binding.tvTuesday.run {
            setBackgroundResource(getSelectedBackgroundResource(false))
            setTextColor(getSelectedTextColor(false))
        }
        binding.tvWednesday.run {
            setBackgroundResource(getSelectedBackgroundResource(false))
            setTextColor(getSelectedTextColor(false))
        }
        binding.tvThursday.run {
            setBackgroundResource(getSelectedBackgroundResource(false))
            setTextColor(getSelectedTextColor(false))
        }
        binding.tvFriday.run {
            setBackgroundResource(getSelectedBackgroundResource(false))
            setTextColor(getSelectedTextColor(false))
        }
        binding.tvSaturday.run {
            setBackgroundResource(getSelectedBackgroundResource(false))
            setTextColor(getSelectedTextColor(false))
        }
    }

    private fun clearCalendarViews() {
        binding.relSunday.removeAllViews()
        binding.relMonday.removeAllViews()
        binding.relTuesday.removeAllViews()
        binding.relWednesday.removeAllViews()
        binding.relThursday.removeAllViews()
        binding.relFriday.removeAllViews()
        binding.relSaturday.removeAllViews()
    }

    private fun displayPreferenceScheduleShifts() {
        preferenceScheduleByWeekItems.map { item ->
            item.buttonHeight = getHeightOfButton(item.tempStartTime, item.tempEndTime)
            item.topMargin = getTopMargin(QSCalendar.getFormattedTimings(item.tempStartTime))
            when (item.tempSchedDate) {
                weekDateArray[0].lowercase() -> {
                    binding.relSunday.addView(getButtonToLayout(item))
                }
                weekDateArray[1].lowercase() -> {
                    binding.relMonday.addView(getButtonToLayout(item))
                }
                weekDateArray[2].lowercase() -> {
                    binding.relTuesday.addView(getButtonToLayout(item))
                }
                weekDateArray[3].lowercase() -> {
                    binding.relWednesday.addView(getButtonToLayout(item))
                }
                weekDateArray[4].lowercase() -> {
                    binding.relThursday.addView(getButtonToLayout(item))
                }
                weekDateArray[5].lowercase() -> {
                    binding.relFriday.addView(getButtonToLayout(item))
                }
                weekDateArray[6].lowercase() -> {
                    binding.relSaturday.addView(getButtonToLayout(item))
                }
            }
        }
    }

    private fun displayTORScheduleShifts() {
        torScheduleByWeekItems.map { item ->
            item.buttonHeight = getHeightOfButton(item.tempStartTime, item.tempEndTime)
            item.topMargin = getTopMargin(QSCalendar.getFormattedTimings(item.tempStartTime))
            when (item.tempSchedDate) {
                weekDateArray[0].lowercase() -> {
                    binding.relSunday.addView(getButtonToLayout(item))
                }
                weekDateArray[1].lowercase() -> {
                    binding.relMonday.addView(getButtonToLayout(item))
                }
                weekDateArray[2].lowercase() -> {
                    binding.relTuesday.addView(getButtonToLayout(item))
                }
                weekDateArray[3].lowercase() -> {
                    binding.relWednesday.addView(getButtonToLayout(item))
                }
                weekDateArray[4].lowercase() -> {
                    binding.relThursday.addView(getButtonToLayout(item))
                }
                weekDateArray[5].lowercase() -> {
                    binding.relFriday.addView(getButtonToLayout(item))
                }
                weekDateArray[6].lowercase() -> {
                    binding.relSaturday.addView(getButtonToLayout(item))
                }
            }
        }
    }

    private fun displaySchedulesShifts() {
        schedulesByWeekItems.map { item ->
            item.buttonHeight = getHeightOfButton(item.tempStartTime, item.tempEndTime)
            item.topMargin = getTopMargin(QSCalendar.getFormattedTimings(item.tempStartTime))
            when (item.tempSchedDate) {
                weekDateArray[0].lowercase() -> {
                    binding.relSunday.addView(getButtonToLayout(item))
                }
                weekDateArray[1].lowercase() -> {
                    binding.relMonday.addView(getButtonToLayout(item))
                }
                weekDateArray[2].lowercase() -> {
                    binding.relTuesday.addView(getButtonToLayout(item))
                }
                weekDateArray[3].lowercase() -> {
                    binding.relWednesday.addView(getButtonToLayout(item))
                }
                weekDateArray[4].lowercase() -> {
                    binding.relThursday.addView(getButtonToLayout(item))
                }
                weekDateArray[5].lowercase() -> {
                    binding.relFriday.addView(getButtonToLayout(item))
                }
                weekDateArray[6].lowercase() -> {
                    binding.relSaturday.addView(getButtonToLayout(item))
                }
            }
        }
    }

    private fun getButtonToLayout(item: PreferenceScheduleItem): AppCompatButton {
        item.title = getShiftTitle(item)
        val params = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT, item.buttonHeight ?: 0
        )
        val button = AppCompatButton(requireContext())
        button.layoutParams = params
        button.setBackgroundColor(
            Color.parseColor(QSColors.getShiftBackgroundForSchedulePreference())
        )
        button.setTextColor(
            Color.parseColor(QSColors.getShiftForegroundForSchedulePreference(item))
        )
        button.text = item.title
        button.textSize = 13f
        button.isAllCaps = false
        params.setMargins(0, item.topMargin?.toInt() ?: 0, 0, 0)
        return button
    }

    private fun getButtonToLayout(item: TORScheduleItem): AppCompatButton {
        item.title = getShiftTitle(item)
        val params = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT, item.buttonHeight ?: 0
        )
        val button = AppCompatButton(requireContext())
        button.layoutParams = params
        button.setBackgroundColor(Color.parseColor("#151515"))
        button.setTextColor(Color.parseColor("#ffffff"))
        button.text = item.title
        button.textSize = 13f
        button.isAllCaps = false
        params.setMargins(0, item.topMargin?.toInt() ?: 0, 0, 0)
        return button
    }

    private fun getButtonToLayout(item: SchedulesItem): AppCompatButton {
        item.title = getShiftTitle(item)
        val params = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT, item.buttonHeight ?: 0
        )
        val button = AppCompatButton(requireContext())
        button.layoutParams = params
        button.setBackgroundColor(Color.parseColor(QSColors.getShiftBackground(item)))
        button.setTextColor(Color.parseColor(QSColors.getShiftForeground(item, isClient)))
        button.text = item.title
        button.textSize = 13f
        button.isAllCaps = false

        button.setOnClickListener {
            QSPermissions.run {
                if (isAdmin || hasScheduleModificationUpdatePermission()) {
                    requireActivity().openActivityForResult(
                        ScheduleShiftDetailsActivity::class.java, launcher
                    ) {
                        putBoolean(Constants.isClient, isClient)
                        putBoolean(Constants.newShift, false)
                        putString(Constants.schedule_map, Gson().toJson(item))
                        if (isClient) {
                            putString(Constants.client_map, Gson().toJson(client))
                        } else {
                            putString(Constants.worker_map, Gson().toJson(employee))
                        }
                    }
                }
            }
        }

        params.setMargins(0, item.topMargin?.toInt() ?: 0, 0, 0)
        return button
    }

    private fun getTopMargin(startTime: String?): Double {
        var totalMargin = 0.0
        val endTime = "12:00 AM"
        val defaultHeight = resources.getDimension(com.intuit.sdp.R.dimen._45sdp)
        val heightPerMinute = defaultHeight / 60
        try {
            QSCalendar.run {
                val startDate = formatDate(startTime, QSCalendar.DateFormats.HHMMA.label)
                val endDate = formatDate(endTime, QSCalendar.DateFormats.HHMMA.label)
                if (startDate != null && endDate != null) {
                    val totalMinutes = timeDifferenceInMinutes(startDate, endDate)
                    totalMargin = (totalMinutes * heightPerMinute).toDouble()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return totalMargin
    }

    private fun getHeightOfButton(startTime: String?, endTime: String?): Int {
        var height = 0
        val defaultHeight = resources.getDimension(com.intuit.sdp.R.dimen._45sdp)
        val perCellHeight = defaultHeight / 60
        try {
            QSCalendar.run {
                val startDate = formatDate(startTime, QSCalendar.DateFormats.HHMMA.label)
                val endDate = formatDate(endTime, QSCalendar.DateFormats.HHMMA.label)
                var totalMinutes: Long = 0
                if (endDate != null && startDate != null) {
                    totalMinutes = timeDifferenceInMinutes(endDate, startDate)
                }
                val dividerHeight = resources.getDimension(R.dimen._1dp)
                height = (totalMinutes * perCellHeight).toInt() - dividerHeight.toInt()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return height
    }

    private fun getShiftTitle(item: PreferenceScheduleItem): String {
        return item.preferenceType + " (" + trimFloatValue(item.hours) + ")"
    }

    private fun getShiftTitle(item: TORScheduleItem): String {
        return item.workPrefType + " (" + trimFloatValue(item.hours) + ")"
    }

    private fun getShiftTitle(item: SchedulesItem): String {
        return if (isClient) {
            "${item.staffFirstName} ${item.staffLastName} - ${item.taskName} (${trimFloatValue(item.hours)})"
        } else "${item.clientFirstName} ${item.clientLastName} - ${item.taskName} (${
            trimFloatValue(
                item.hours
            )
        })"
    }

    private val launcher = registerActivityResultLauncher {
        if (it.resultCode == Activity.RESULT_OK) {
            fetchScheduleService()
        }
    }
}