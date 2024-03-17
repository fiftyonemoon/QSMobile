package com.quicksolveplus.utils.calendar

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.quicksolveplus.qspmobile.R
import com.quicksolveplus.utils.Constants
import com.quicksolveplus.utils.QSCalendar
import com.quicksolveplus.utils.QSPermissions
import java.text.DateFormatSymbols
import java.util.*

/**
 * 29/03/23.
 *
 * @author hardkgosai.
 */
class CustomCalendarView : LinearLayout {

    private var mContext: Context
    private val DAY_OF_WEEK = "dayOfWeek"
    private val DAY_OF_MONTH_TEXT = "dayOfMonthText"
    private val SCHEDULED_DAY_OF_MONTH_TEXT = "scheduledDayOfMonthText"
    private val SERVICE_RECORD_DAY_OF_MONTH_TEXT = "serviceRecordDayOfMonthText"
    private val DAY_OF_MONTH_CONTAINER = "dayOfMonthContainer"

    constructor(context: Context) : super(context) {
        this.mContext = context
        initCalendar()
    }

    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs) {
        this.mContext = context
        attrs?.let {
            getAttributes(attrs)
        }
        initCalendar()
    }

    constructor(
        context: Context, attrs: AttributeSet?, defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        this.mContext = context
        attrs?.let {
            getAttributes(attrs)
        }
        initCalendar()
    }

    private lateinit var view: View
    private lateinit var previousMonthButton: AppCompatImageView
    private lateinit var nextMonthButton: AppCompatImageView
    private lateinit var calendarListener: CalendarListener
    private lateinit var container: LinearLayout

    private var disabledDayBackgroundColor = 0
    private var disabledDayTextColor = 0
    private var calendarBackgroundColor = 0
    private var weekLayoutBackgroundColor = 0
    private var calendarTitleBackgroundColor = 0
    private var selectedDayTextColor = 0
    private var calendarTitleTextColor = 0
    private var dayOfWeekTextColor = 0
    private var dayOfMonthTextColor = 0
    private var currentDayOfMonth = 0
    private var currentMonthIndex = 0
    private var selectedDayBackground = 0
    private var currentMonth = 0
    private var currentYear = 0

    private var isOverflowDateVisible = true
    private var lastSelectedDay: Date? = null
    private var customTypeface: Typeface? = null
    private var firstDayOfWeek = Calendar.SUNDAY
    private var decorators: List<DayDecorator>? = null
    private val locale by lazy {
        context.resources.configuration.locale
    }

    private fun getAttributes(attrs: AttributeSet) {
        val typedArray: TypedArray =
            mContext.obtainStyledAttributes(attrs, R.styleable.CustomCalendarView, 0, 0)

        calendarBackgroundColor = typedArray.getColor(
            R.styleable.CustomCalendarView_calendarBackgroundColor,
            resources.getColor(R.color.white)
        )
        calendarTitleBackgroundColor = typedArray.getColor(
            R.styleable.CustomCalendarView_titleLayoutBackgroundColor,
            resources.getColor(R.color.white)
        )
        calendarTitleTextColor = typedArray.getColor(
            R.styleable.CustomCalendarView_calendarTitleTextColor, resources.getColor(R.color.black)
        )
        weekLayoutBackgroundColor = typedArray.getColor(
            R.styleable.CustomCalendarView_weekLayoutBackgroundColor,
            resources.getColor(R.color.white)
        )
        dayOfWeekTextColor = typedArray.getColor(
            R.styleable.CustomCalendarView_dayOfWeekTextColor, resources.getColor(R.color.black)
        )
        dayOfMonthTextColor = typedArray.getColor(
            R.styleable.CustomCalendarView_dayOfMonthTextColor, resources.getColor(R.color.black)
        )
        disabledDayBackgroundColor = typedArray.getColor(
            R.styleable.CustomCalendarView_disabledDayBackgroundColor,
            resources.getColor(R.color.day_disabled_background_color)
        )
        disabledDayTextColor = typedArray.getColor(
            R.styleable.CustomCalendarView_disabledDayTextColor,
            resources.getColor(R.color.day_disabled_text_color)
        )
        selectedDayBackground = typedArray.getResourceId(
            R.styleable.CustomCalendarView_selectedDayBackground, R.drawable.circle_app_color
        )
        selectedDayTextColor = typedArray.getColor(
            R.styleable.CustomCalendarView_selectedDayTextColor, resources.getColor(R.color.white)
        )
        currentDayOfMonth = typedArray.getColor(
            R.styleable.CustomCalendarView_currentDayOfMonthColor,
            resources.getColor(R.color.current_day_of_month)
        )

        typedArray.recycle()
    }

    private fun initCalendar() {
        //always use mContext instead of this class context for inflating layout
        val inflater = LayoutInflater.from(mContext)
        view = inflater.inflate(R.layout.layout_calendar, this, true)

        previousMonthButton = view.findViewById(R.id.iv_previous)
        nextMonthButton = view.findViewById(R.id.iv_next)
        container = view.findViewById(R.id.daysContainer)

        //init left right swipe
        SwipeEvents(container, swipeCallback)

        initCalendarLayout()
    }

    private fun initCalendarLayout() {
        QSCalendar.run {
            calendar = Calendar.getInstance(locale)
            setFirstDayOfWeek(Calendar.SUNDAY)

            calendar?.let { calendar ->

                calendarDate?.let { date ->

                    if (date.isNotEmpty()) {

                        date.split("/").run {
                            calendarMonth = this[1]
                            calendarYear = this[2]
                        }

                        calendarMonth?.let { month ->
                            calendarYear?.let { year ->

                                if (month.isNotEmpty() && year.isNotEmpty()) {

                                    calendar.set(Calendar.MONTH, month.toInt() - 1)
                                    calendar.set(Calendar.YEAR, year.toInt())

                                    refresh(calendar)

                                    currentMonth = calendar.get(Calendar.MONTH) + 1
                                    currentYear = calendar.get(Calendar.YEAR)

                                    val currentMonthYear = "$currentMonth/$currentYear"
                                    val currentDate = formatDate(
                                        currentMonthYear, QSCalendar.DateFormats.MMYYYY.label
                                    )

                                    currentDate?.let {
                                        initPreviousButton(currentDate)
                                        initNextButton(currentDate)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun initPreviousButton(date: Date) {
        val previousCalendar = Calendar.getInstance()
        previousCalendar.time = date
        previousCalendar.add(Calendar.MONTH, 1)

        val previousDate = QSCalendar.formatCurrentDate(
            previousCalendar.time, QSCalendar.DateFormats.MMYYYY.label
        )

        previousMonthButton.setOnClickListener {
            QSCalendar.run {
                calendarMonth?.let { month ->
                    calendarYear?.let { year ->

                        if (month.isNotEmpty() && year.isNotEmpty()) {
                            val selectedMonthYear = "$month/$year"
                            val selectedDate = formatDate(
                                selectedMonthYear, QSCalendar.DateFormats.MMYYYY.label
                            )

                            selectedDate?.let {

                                val selectedCalendar = Calendar.getInstance()
                                selectedCalendar.time = selectedDate
                                selectedCalendar.add(Calendar.MONTH, -1)
                                val selectedMonthDate = formatCurrentDate(
                                    selectedCalendar.time, QSCalendar.DateFormats.MMYYYY.label
                                )

                                if (QSPermissions.isAdmin || !QSPermissions.hasPriorCurrentNextMonthViewOnlyReadPermission()) {

                                    currentMonthIndex--
                                    this@run.calendar = selectedCalendar
                                    this@CustomCalendarView.refresh(selectedCalendar)
                                    calendarListener.onMonthChanged(selectedCalendar.time)

                                } else if (selectedMonthDate != null) {
                                    if (selectedMonthDate == date || selectedMonthDate == previousDate) {
                                        currentMonthIndex--
                                        this@run.calendar = selectedCalendar
                                        this@CustomCalendarView.refresh(selectedCalendar)
                                        calendarListener.onMonthChanged(selectedCalendar.time)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun initNextButton(date: Date) {
        val nextCalendar = Calendar.getInstance()
        nextCalendar.time = date
        nextCalendar.add(Calendar.MONTH, 1)

        val nextDate =
            QSCalendar.formatCurrentDate(nextCalendar.time, QSCalendar.DateFormats.MMYYYY.label)

        nextMonthButton.setOnClickListener {
            QSCalendar.run {
                calendarMonth?.let { month ->
                    calendarYear?.let { year ->

                        if (month.isNotEmpty() && year.isNotEmpty()) {
                            val selectedMonthYear = "$month/$year"
                            val selectedDate = formatDate(
                                selectedMonthYear, QSCalendar.DateFormats.MMYYYY.label
                            )

                            selectedDate?.let {

                                val selectedCalendar = Calendar.getInstance()
                                selectedCalendar.time = selectedDate
                                selectedCalendar.add(Calendar.MONTH, 1)
                                val selectedMonthDate = formatCurrentDate(
                                    selectedCalendar.time, QSCalendar.DateFormats.MMYYYY.label
                                )

                                if (QSPermissions.isAdmin || !QSPermissions.hasPriorCurrentNextMonthViewOnlyReadPermission()) {

                                    currentMonthIndex++
                                    this@run.calendar = selectedCalendar
                                    this@CustomCalendarView.refresh(selectedCalendar)
                                    calendarListener.onMonthChanged(selectedCalendar.time)

                                } else if (selectedMonthDate != null) {
                                    if (selectedMonthDate == date || selectedMonthDate == nextDate) {
                                        currentMonthIndex++
                                        this@run.calendar = selectedCalendar
                                        this@CustomCalendarView.refresh(selectedCalendar)
                                        calendarListener.onMonthChanged(selectedCalendar.time)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private val swipeCallback: SwipeEvents.SwipeCallback = object : SwipeEvents.SwipeCallback {
        override fun onSwipeRight() {
            previousMonthButton.performClick()
        }

        override fun onSwipeLeft() {
            nextMonthButton.performClick()
        }

        override fun onClick(view: View) {
            if (view.id != container.id) {
                onDayOfMonthClickListener.onClick(view)
            }
        }
    }

    fun refresh(refreshCalendar: Calendar?) {
        if (refreshCalendar == null) return
        QSCalendar.run {
            calendarMonth = (refreshCalendar.get(Calendar.MONTH) + 1).toString()
            calendarYear = refreshCalendar.get(Calendar.YEAR).toString()
            calendar = refreshCalendar

            calendar?.firstDayOfWeek = getFirstDayOfWeek()

            initTitleUI()
            initWeekUI()
            initDaysUI()
        }
    }

    private fun initTitleUI() {
        val titleLayout = view.findViewById<View>(R.id.titleLayout)
        val dateTitle = view.findViewById<AppCompatTextView>(R.id.tv_dateTitle)
        titleLayout.setBackgroundColor(calendarTitleBackgroundColor)

        QSCalendar.run {
            calendar?.let {
                var dateText = DateFormatSymbols(locale).shortMonths[it.get(Calendar.MONTH)]
                dateText =
                    dateText.substring(0, 1).uppercase(Locale.getDefault()) + dateText.subSequence(
                        1, dateText.length
                    )

                dateTitle.text = String.format(
                    Locale.ENGLISH, "%s %d", dateText, it.get(Calendar.YEAR)
                )
                dateTitle.setTextColor(calendarTitleTextColor)
            }
        }
    }

    private fun initWeekUI() {
        var dayOfWeek: AppCompatTextView
        var dayOfTheWeekString: String

        val titleLayout = view.findViewById<View>(R.id.weekLayout)
        titleLayout.setBackgroundColor(weekLayoutBackgroundColor)

        val weekDaysArray = DateFormatSymbols(locale).shortWeekdays
        QSCalendar.calendar?.let { calendar ->
            for (i in 1 until weekDaysArray.size) {
                dayOfTheWeekString = weekDaysArray[i].uppercase()
                dayOfWeek = view.findViewWithTag(
                    DAY_OF_WEEK + getWeekIndex(i, calendar)
                )
                dayOfWeek.text = dayOfTheWeekString
                dayOfWeek.setTextColor(dayOfWeekTextColor)
            }
        }
    }

    private fun initDaysUI() {
        val calendar = Calendar.getInstance(locale)
        QSCalendar.calendar?.let { calendar.time = it.time }
        calendar[Calendar.DAY_OF_MONTH] = 1
        calendar.firstDayOfWeek = getFirstDayOfWeek()

        val firstDayOfMonth = calendar[Calendar.DAY_OF_WEEK]
        var dayOfMonthIndex: Int = getWeekIndex(firstDayOfMonth, calendar)
        val actualMaximum = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        val startCalendar = calendar.clone() as Calendar
        startCalendar.add(Calendar.DATE, -(dayOfMonthIndex - 1))
        val monthEndIndex = 42 - (actualMaximum + dayOfMonthIndex - 1)

        var dayView: DayView?
        var scheduledDayView: ScheduledDayView
        var serviceRecordDayView: ServiceRecordDayView
        var dayOfMonthContainer: ViewGroup

        for (i in 1 until 43) {

            dayOfMonthContainer = view.findViewWithTag(DAY_OF_MONTH_CONTAINER + i)
            dayView = view.findViewWithTag(DAY_OF_MONTH_TEXT + i)
            scheduledDayView = view.findViewWithTag(SCHEDULED_DAY_OF_MONTH_TEXT + i)
            serviceRecordDayView = view.findViewWithTag(SERVICE_RECORD_DAY_OF_MONTH_TEXT + i)

            if (dayView == null || scheduledDayView == null || serviceRecordDayView == null) continue

            //new-added@hardkgosai
            dayOfMonthContainer.setOnTouchListener(null)

            // Apply the default styles
            dayView.bind(startCalendar.time, null)
            scheduledDayView.bind(startCalendar.time, null)
            serviceRecordDayView.bind(startCalendar.time, null)

            dayView.isVisible = true
            scheduledDayView.isInvisible = true
            serviceRecordDayView.isInvisible = true

            if (CalendarUtils.isSameMonth(calendar, startCalendar)) {

                SwipeEvents(dayOfMonthContainer, swipeCallback)
                dayView.setBackgroundColor(calendarBackgroundColor)
                dayView.setTextColor(dayOfMonthTextColor)

                QSCalendar.run {
                    val currentDate = calendarDateFormat.format(startCalendar.time)
                    calendarDate?.let {
                        if (it.isNotEmpty()) {
                            if (it == currentDate) {
                                markDayAsSelectedDay(startCalendar.time)
                            }
                        }
                    }
                }
            } else {
                dayView.setBackgroundColor(disabledDayBackgroundColor)
                dayView.setTextColor(disabledDayTextColor)

                if (!isOverflowDateVisible()) {
                    dayView.visibility = GONE
                } else if (i >= 36 && (monthEndIndex.toFloat() / 7.0f) >= 1) {
                    dayView.visibility = GONE
                }
            }

            dayView.decorate()
            scheduledDayView.decorate()
            serviceRecordDayView.decorate()
            startCalendar.add(Calendar.DATE, 1)
            dayOfMonthIndex++
        }


        // If the last week row has no visible days, hide it or show it in case
        val weekRow = view.findViewWithTag<ViewGroup>("weekRow6")
        dayView = view.findViewWithTag("dayOfMonthText36")
        if (dayView.visibility != VISIBLE) {
            weekRow.visibility = GONE
        } else {
            weekRow.visibility = VISIBLE
        }

        val weekRow5 = view.findViewWithTag<ViewGroup>("weekRow5")
        dayView = view.findViewWithTag("dayOfMonthText29")
        if (dayView.visibility != VISIBLE) {
            weekRow5.visibility = GONE
        } else {
            weekRow5.visibility = VISIBLE
        }
    }

    fun markDayAsSelectedDay(currentDate: Date?) {
        if (currentDate == null) return

        val currentCalendar = getTodayCalendar()
        currentCalendar.firstDayOfWeek = getFirstDayOfWeek()
        currentCalendar.time = currentDate

        // Clear previous marks
        clearDayOfTheMonthStyle(lastSelectedDay)

        // Store current values as last values
        storeLastValues(currentDate)

        // Mark current day as selected
        val view: DayView = getDayOfMonthText(currentCalendar)
        // view.setBackgroundColor(selectedDayBackground);
        view.setBackgroundResource(selectedDayBackground)
        view.setTextColor(selectedDayTextColor)
    }

    fun markDayAsScheduled(currentDate: Date?) {
        if (currentDate == null) return

        val currentCalendar: Calendar = getTodayCalendar()
        currentCalendar.firstDayOfWeek = getFirstDayOfWeek()
        currentCalendar.time = currentDate

        // Mark current day as selected
        val view = getDayOfMonthView(currentCalendar)
        view.isVisible = true
    }

    private fun clearDayOfTheMonthStyle(currentDate: Date?) {
        if (currentDate != null) {
            val calendar = getTodayCalendar()
            calendar.firstDayOfWeek = getFirstDayOfWeek()
            calendar.time = currentDate

            val dayView = getDayOfMonthText(calendar)
            dayView.setBackgroundColor(calendarBackgroundColor)
            dayView.setTextColor(dayOfMonthTextColor)
            dayView.decorate()
        }
    }

    private fun storeLastValues(currentDate: Date) {
        lastSelectedDay = currentDate
    }

    private fun getWeekIndex(weekIndex: Int, currentCalendar: Calendar): Int {
        val firstDayWeekPosition = currentCalendar.firstDayOfWeek
        return if (firstDayWeekPosition == 1) {
            weekIndex
        } else {
            if (weekIndex == 1) {
                7
            } else {
                weekIndex - 1
            }
        }
    }

    private fun getDayOfMonthText(currentCalendar: Calendar): DayView {
        return getView(DAY_OF_MONTH_TEXT, currentCalendar) as DayView
    }

    private fun getDayOfMonthView(currentCalendar: Calendar): ScheduledDayView {
        return getView(
            SCHEDULED_DAY_OF_MONTH_TEXT, currentCalendar
        ) as ScheduledDayView
    }

    private fun getServiceRecordDayOfMonthView(currentCalendar: Calendar): ServiceRecordDayView {
        return getView(
            SERVICE_RECORD_DAY_OF_MONTH_TEXT, currentCalendar
        ) as ServiceRecordDayView
    }

    private fun getDayIndexByDate(currentCalendar: Calendar): Int {
        val monthOffset: Int = getMonthOffset(currentCalendar)
        val currentDay = currentCalendar[Calendar.DAY_OF_MONTH]
        return currentDay + monthOffset
    }

    private fun getMonthOffset(currentCalendar: Calendar): Int {
        val calendar = Calendar.getInstance()
        calendar.firstDayOfWeek = getFirstDayOfWeek()
        calendar.time = currentCalendar.time
        calendar[Calendar.DAY_OF_MONTH] = 1

        val firstDayWeekPosition = calendar.firstDayOfWeek
        val dayPosition = calendar[Calendar.DAY_OF_WEEK]

        return if (firstDayWeekPosition == 1) {
            dayPosition - 1
        } else {
            if (dayPosition == 1) {
                6
            } else {
                dayPosition - 2
            }
        }
    }

    private fun getView(key: String, currentCalendar: Calendar): View? {
        val index = getDayIndexByDate(currentCalendar)
        return view.findViewWithTag(key + index)
    }

    private fun getTodayCalendar(): Calendar {
        val currentCalendar = Calendar.getInstance(locale)
        currentCalendar.firstDayOfWeek = getFirstDayOfWeek()
        return currentCalendar
    }

    fun dsnMarkDayAsServiceDays(currentDate: Date, dsnStatus: String?) {
        val currentCalendar: Calendar = getTodayCalendar()
        currentCalendar.firstDayOfWeek = getFirstDayOfWeek()
        currentCalendar.time = currentDate
        val view = getServiceRecordDayOfMonthView(currentCalendar)
        when (dsnStatus) {
            Constants.dsnShiftStatusComplete -> view.setBackgroundResource(R.drawable.green_thick_ring)
            Constants.dsnShiftStatusIncomplete -> view.setBackgroundResource(R.drawable.light_blue_thick_ring)
            Constants.dsnShiftStatusPending -> view.setBackgroundResource(R.drawable.red_thick_ring)
        }
        view.isVisible = true
    }

    fun getFirstDayOfWeek(): Int {
        return firstDayOfWeek
    }

    fun setFirstDayOfWeek(firstDayOfWeek: Int) {
        this.firstDayOfWeek = firstDayOfWeek
    }

    fun isOverflowDateVisible(): Boolean {
        return isOverflowDateVisible
    }

    fun setShowOverflowDate(isOverFlowEnabled: Boolean) {
        isOverflowDateVisible = isOverFlowEnabled
    }

    fun setCalendarListener(calendarListener: CalendarListener) {
        this.calendarListener = calendarListener
    }

    private val onDayOfMonthClickListener = OnClickListener { view ->
        val dayOfMonthContainer = view as ViewGroup
        var tagId = dayOfMonthContainer.tag as String
        tagId = tagId.substring(DAY_OF_MONTH_CONTAINER.length)
        val dayOfMonthText = view.findViewWithTag<AppCompatTextView>(DAY_OF_MONTH_TEXT + tagId)

        QSCalendar.run {
            calendar?.let { it ->
                val calendar = Calendar.getInstance()
                calendar.firstDayOfWeek = getFirstDayOfWeek()
                calendar.time = it.time
                calendar[Calendar.DAY_OF_MONTH] = dayOfMonthText.text.toString().toInt()
                calendarWeek = calendar[Calendar.WEEK_OF_YEAR]
                markDayAsSelectedDay(calendar.time)
                calendarListener.onDateSelected(calendar.time)
            }
        }
    }
}