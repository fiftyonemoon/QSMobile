package com.quicksolveplus.utils.calendar

import java.util.*

/**
 * 29/03/23.
 *
 * @author hardkgosai.
 */
object CalendarUtils {

    fun isSameMonth(c1: Calendar?, c2: Calendar?): Boolean {
        return if (c1 == null || c2 == null) false
        else c1[Calendar.ERA] == c2[Calendar.ERA]
                && c1[Calendar.YEAR] == c2[Calendar.YEAR]
                && c1[Calendar.MONTH] == c2[Calendar.MONTH]
    }

    fun isToday(calendar: Calendar?): Boolean {
        return isSameDay(calendar, Calendar.getInstance())
    }

    fun isSameDay(cal1: Calendar?, cal2: Calendar?): Boolean {
        require(!(cal1 == null || cal2 == null)) { "The dates must not be null" }
        return cal1[Calendar.ERA] == cal2[Calendar.ERA]
                && cal1[Calendar.YEAR] == cal2[Calendar.YEAR]
                && cal1[Calendar.DAY_OF_YEAR] == cal2[Calendar.DAY_OF_YEAR]
    }
}