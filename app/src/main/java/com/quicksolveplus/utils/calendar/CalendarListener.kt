package com.quicksolveplus.utils.calendar

import java.util.*

/**
 * 29/03/23.
 *
 * @author hardkgosai.
 */
interface CalendarListener {
    fun onDateSelected(date: Date?)

    fun onMonthChanged(time: Date?)
}