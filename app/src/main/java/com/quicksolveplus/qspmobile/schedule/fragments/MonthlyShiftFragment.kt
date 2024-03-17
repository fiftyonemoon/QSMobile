package com.quicksolveplus.qspmobile.schedule.fragments

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
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
import com.quicksolveplus.qspmobile.databinding.FragmentMonthlyShiftBinding
import com.quicksolveplus.qspmobile.employee.model.EmployeesItem
import com.quicksolveplus.qspmobile.schedule.ScheduleShiftDetailsActivity
import com.quicksolveplus.qspmobile.schedule.adapters.SchedulesAdapter
import com.quicksolveplus.qspmobile.schedule.adapters.StaffPreferenceScheduleAdapter
import com.quicksolveplus.qspmobile.schedule.adapters.StaffTORScheduleAdapter
import com.quicksolveplus.qspmobile.schedule.dialogs.LinkedClients
import com.quicksolveplus.qspmobile.schedule.models.*
import com.quicksolveplus.qspmobile.schedule.viewmodel.MonthlyShiftViewModel
import com.quicksolveplus.utils.*
import com.quicksolveplus.utils.QSCalendar.calendarDate
import com.quicksolveplus.utils.QSCalendar.calendarMonth
import com.quicksolveplus.utils.QSCalendar.calendarYear
import com.quicksolveplus.utils.calendar.CalendarListener
import java.util.*

/**
 * 30/03/23.
 *
 * @author hardkgosai.
 */
class MonthlyShiftFragment(
    private val isClient: Boolean = false,
    private val fromPreferences: Boolean = false,
    private val client: ClientsItem? = null,
    private val employee: EmployeesItem? = null
) : Fragment() {

    private lateinit var binding: FragmentMonthlyShiftBinding
    private val viewModel: MonthlyShiftViewModel by viewModels()

    private val dotList = arrayListOf<String>()
    private var forFirstTime = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMonthlyShiftBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        setObservers()
        fetchSchedulesByMonths()
    }

    private fun initUI() {
        QSPermissions.run {
            if (isAdmin || hasScheduleModificationCreatePermission()) {
                binding.fab.show()
            } else binding.fab.hide()
        }

        binding.fab.setOnClickListener {
            //do on fab click
        }
        binding.calendarView.setFirstDayOfWeek(Calendar.SUNDAY)
        binding.calendarView.setShowOverflowDate(false)
        binding.calendarView.markDayAsSelectedDay(
            QSCalendar.formatDate(
                calendarDate, QSCalendar.DateFormats.DDMMYYYY.label
            )
        )
        binding.calendarView.setCalendarListener(object : CalendarListener {
            override fun onDateSelected(date: Date?) {
                date?.let {
                    calendarDate = QSCalendar.calendarDateFormat.format(date)
                    fetchSchedulesService()
                }
            }

            override fun onMonthChanged(time: Date?) {
                fetchSchedulesByMonths()
            }
        })
    }

    private fun setObservers() {
        viewModel.getResponseStatus().observe(viewLifecycleOwner) {
            when (it) {
                is ResponseStatus.Running -> {
                    showQSProgress(requireActivity())
                }
                is ResponseStatus.Success -> {
                    proceedResponse(it)
                    dismissQSProgress()
                }
                is ResponseStatus.Failed -> {
                    if (it.msg == getString(R.string.msg_server_error)) {
                        toast(requireActivity(), it.msg)
                    }
                    proceedFailure(it)
                    dismissQSProgress()
                }
                else -> {
                    toast(requireActivity(), "Something goes wrong")
                    dismissQSProgress()
                }
            }
        }
    }

    private fun proceedResponse(success: ResponseStatus.Success) {
        when (success.apiName) {
            Api.getStaffPreferenceForScheduleByMonth -> {
                if (success.data is PreferenceScheduleByMonth) {
                    proceedStaffPreferenceForScheduleByMonth(success.data)
                }
            }
            Api.getSchedulesByMonth -> {
                if (success.data is ScheduleByMonth) {
                    proceedSchedulesByMonth(success.data)
                }
            }
            Api.getStaffTORForScheduleByMonth -> {
                if (success.data is TORScheduleByMonth) {
                    proceedStaffTORForScheduleByMonth(success.data)
                }
            }
            Api.getSchedules -> {
                if (success.data is Schedules) {
                    proceedSchedules(success.data)
                }
            }
            Api.getStaffTORForSchedule -> {
                if (success.data is TORSchedule) {
                    proceedStaffTORForSchedule(success.data)
                }
            }
            Api.getStaffPreferenceForSchedule -> {
                if (success.data is PreferenceSchedule) {
                    proceedStaffPreferenceForSchedule(success.data)
                }
            }
        }
    }

    private fun proceedFailure(failed: ResponseStatus.Failed) {
        when (failed.apiName) {
            Api.getSchedulesByMonth -> {
                fillDotsInCalendar()
                if (forFirstTime) {
                    forFirstTime = false
                    fetchSchedulesService()
                }
            }
            Api.getStaffTORForScheduleByMonth -> {
                getSchedulesByMonth()
            }
            Api.getSchedules -> {
                binding.tvError.isVisible = true
            }
            Api.getStaffTORForSchedule -> {
                getSchedules()
            }
            Api.getStaffPreferenceForSchedule -> {
                binding.tvError.apply {
                    isVisible = true
                    text = getString(R.string.str_no_schedule_pref_found)
                }
            }
        }
    }

    private fun proceedStaffPreferenceForScheduleByMonth(preferenceScheduleByMonth: PreferenceScheduleByMonth) {
        preferenceScheduleByMonth.map {
            dotList.add(it.preferenceDate)
        }

        fillDotsInCalendar()

        if (forFirstTime) {
            forFirstTime = false
            fetchSchedulesService()
        }
    }

    private fun proceedSchedulesByMonth(scheduleByMonth: ScheduleByMonth) {
        scheduleByMonth.map {
            dotList.add(it.scheduleDate)
        }

        fillDotsInCalendar()

        if (forFirstTime) {
            forFirstTime = false
            fetchSchedulesService()
        }
    }

    private fun proceedStaffTORForScheduleByMonth(torScheduleByMonth: TORScheduleByMonth) {
        torScheduleByMonth.map {
            dotList.add(it.tORDate)
        }
        getSchedulesByMonth()
    }

    private fun proceedStaffPreferenceForSchedule(preferenceSchedule: PreferenceSchedule) {
        setStaffPreferenceForScheduleAdapter(preferenceSchedule = preferenceSchedule)
    }

    private fun proceedStaffTORForSchedule(torSchedule: TORSchedule) {
        setStaffTORForScheduleAdapter(torSchedule = torSchedule)
        getSchedules()
    }

    private fun proceedSchedules(schedules: Schedules) {
        filterSchedules(schedules = schedules)
    }

    private fun fetchSchedulesByMonths() {
        dotList.clear()
        binding.calendarView.refresh(QSCalendar.calendar)
        if (fromPreferences) {
            if (!isClient) {
                binding.fab.hide()
                getStaffPreferenceForScheduleByMonth()
            }
        } else {
            if (isClient) {
                getSchedulesByMonth()
            } else {
                getStaffTORForScheduleByMonth()
            }
        }
    }

    private fun getStaffPreferenceForScheduleByMonth() {
        employee?.run {
            val body = RequestParameters.forStaffPreferenceForScheduleByMonth(
                staffUID = UsersUID, dMonth = calendarMonth, dYear = calendarYear
            )
            viewModel.getStaffPreferenceForScheduleByMonth(body = body)
        }
    }

    private fun getStaffTORForScheduleByMonth() {
        employee?.run {
            val body = RequestParameters.forStaffTORForScheduleByMonth(
                staffUID = UsersUID, dMonth = calendarMonth, dYear = calendarYear
            )
            viewModel.getStaffTORForScheduleByMonth(body = body)
        }
    }

    private fun getSchedulesByMonth() {
        val staffId = if (isClient) 0 else employee?.UsersUID
        val clientId = if (isClient) client?.ClientUID else 0

        val body = RequestParameters.forSchedulesByMonth(
            staffUID = staffId,
            ClientUID = clientId,
            dMonth = calendarMonth,
            dYear = calendarYear,
            isForDsn = false
        )
        viewModel.getSchedulesByMonth(body = body)
    }

    private fun fetchSchedulesService() {
        if (fromPreferences) {
            if (!isClient) {
                getStaffPreferenceForSchedule()
            }
        } else {
            if (isClient) {
                getSchedules()
            } else {
                getStaffTORForSchedule()
            }
        }
    }

    private fun getStaffPreferenceForSchedule() {
        employee?.run {
            val date = QSCalendar.formatDate(calendarDate, QSCalendar.DateFormats.DDMMYYYY.label)
            date?.run {
                val day = QSCalendar.getDateAsInt(date, QSCalendar.DateFormats.d.label)
                val month = QSCalendar.getDateAsInt(date, QSCalendar.DateFormats.MM.label)
                val year = QSCalendar.getDateAsInt(date, QSCalendar.DateFormats.YYYY.label)
                val body = RequestParameters.forStaffPreferenceForSchedule(
                    staffID = UsersUID, schedDate = "$month/$day/$year"
                )
                viewModel.getStaffPreferenceForSchedule(body = body)
            }
        }
    }

    private fun getSchedules() {
        val staffId = if (isClient) 0 else employee?.UsersUID
        val clientId = if (isClient) client?.ClientUID else 0
        val date = QSCalendar.formatDate(calendarDate, QSCalendar.DateFormats.DDMMYYYY.label)
        date?.run {
            val day = QSCalendar.getDateAsInt(date, QSCalendar.DateFormats.d.label)
            val month = QSCalendar.getDateAsInt(date, QSCalendar.DateFormats.MM.label)
            val year = QSCalendar.getDateAsInt(date, QSCalendar.DateFormats.YYYY.label)
            val body = RequestParameters.forSchedules(
                staffUID = staffId,
                ClientUID = clientId,
                dDay = day,
                dMonth = month,
                dYear = year,
                isForDsn = false
            )
            viewModel.getSchedules(body = body)
        }
    }

    private fun getStaffTORForSchedule() {
        employee?.run {
            val date = QSCalendar.formatDate(calendarDate, QSCalendar.DateFormats.DDMMYYYY.label)
            date?.run {
                val day = QSCalendar.getDateAsInt(date, QSCalendar.DateFormats.d.label)
                val month = QSCalendar.getDateAsInt(date, QSCalendar.DateFormats.MM.label)
                val year = QSCalendar.getDateAsInt(date, QSCalendar.DateFormats.YYYY.label)
                val body = RequestParameters.forStaffTORForSchedule(
                    staffID = UsersUID, schedDate = "$month/$day/$year"
                )
                viewModel.getStaffTORForSchedule(body = body)
            }
        }
    }

    private fun fillDotsInCalendar() {
        dotList.map {
            val date = QSCalendar.formatDate(it, QSCalendar.DateFormats.YYYYMMDDTHHMMSS.label)
            binding.calendarView.markDayAsScheduled(date)
        }
    }

    private fun setStaffTORForScheduleAdapter(torSchedule: TORSchedule) {
        if (torSchedule.isNotEmpty()) {
            StaffTORScheduleAdapter(items = torSchedule).apply {
                binding.rv.adapter = this@apply
            }
        } else binding.rv.adapter = null
    }

    private fun setStaffPreferenceForScheduleAdapter(preferenceSchedule: PreferenceSchedule) {
        binding.tvError.apply {
            isVisible = preferenceSchedule.isEmpty()
            text = getString(R.string.str_no_schedule_pref_found)
        }
        if (preferenceSchedule.isNotEmpty()) {
            StaffPreferenceScheduleAdapter(items = preferenceSchedule).apply {
                binding.rv.adapter = this@apply
            }
        } else binding.rv.adapter = null
    }

    private fun setSchedulesAdapter(schedules: Schedules) {
        binding.tvError.isVisible = schedules.isEmpty() && binding.rv.adapter == null
        if (schedules.isNotEmpty()) {
            SchedulesAdapter(items = schedules,
                isClient = isClient,
                object : SchedulesAdapter.OnItemClickedListener {
                    override fun onItemClicked(position: Int) {
                        requireActivity().openActivityForResult(
                            ScheduleShiftDetailsActivity::class.java, launcher
                        ) {
                            putBoolean(Constants.isClient, isClient)
                            putBoolean(Constants.newShift, false)
                            putString(Constants.schedule_map, Gson().toJson(schedules[position]))
                            if (isClient) {
                                putString(Constants.client_map, Gson().toJson(client))
                            } else {
                                putString(Constants.worker_map, Gson().toJson(employee))
                            }
                        }
                    }

                    override fun onLinkedClientClicked(position: Int) {
                        val schedule = schedules[position]
                        LinkedClients(
                            activity = requireActivity() as AppCompatActivity,
                            items = schedule.linkedSchedules
                        ).show()
                    }
                }).apply {
                binding.rvShifts.adapter = this@apply
            }
        } else binding.rvShifts.adapter = null

    }

    private val launcher = registerActivityResultLauncher {
        if (it.resultCode == Activity.RESULT_OK) {
            forFirstTime = true
            fetchSchedulesByMonths()
        }
    }

    private fun filterSchedules(schedules: Schedules) {
        val shiftItems = Schedules()
        val rawItems = Schedules()

        schedules.map { item ->
            val schedDate = item.schedDate
            val endTime = item.endTime
            val schedEndDate = QSCalendar.formatDate(
                item.schedDateEnd,
                QSCalendar.DateFormats.MMDDYY.label,
                QSCalendar.DateFormats.MMDDYYYY.label
            )

            calendarDate?.split("/")?.let { array ->
                val selectedDate = "${array[1]}/${array[0]}/${array[2]}" //format mm/dd/yyyy
                val schedCurrentDate = "${item.dMonth}/${item.dDay}/${item.dYear}"
                val currentDate = QSCalendar.formatDate(
                    schedCurrentDate,
                    QSCalendar.DateFormats.MMDDYYYY.label,
                    QSCalendar.DateFormats.MMDDYY.label
                )

                if (schedDate == currentDate && !(selectedDate == schedEndDate && endTime == "12:00 A")) {
                    if (item.clientLinkUID == 0 || item.clientUID == item.uID || isClient) {
                        shiftItems.add(item)
                    }
                    rawItems.add(item)
                }
            }
        }

        shiftItems.map { item ->
            if (item.taskName == "IHSS" && item.linkedSchedules.isNotEmpty()) {
                item.linkedSchedules.mapIndexed { position, item2 ->
                    if (item2.linkedTaskName == "IHSS") {
                        rawItems.map { raw ->
                            if (raw.uID == item2.linkedScheduleID) {
                                shiftItems[position] = raw
                                return
                            }
                        }
                    }
                }
            }
        }

        setSchedulesAdapter(shiftItems)
    }
}
