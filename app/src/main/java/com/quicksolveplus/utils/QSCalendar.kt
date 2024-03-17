package com.quicksolveplus.utils

import android.content.res.Resources
import android.text.TextUtils
import android.text.format.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * 29/03/23.
 *
 * @author hardkgosai.
 */
object QSCalendar {

    var calendarDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    var calendar: Calendar? = null
    var calendarDate: String? = null
    var calendarMonth: String? = null
    var calendarYear: String? = null
    var calendarWeek = 0

    fun refresh(resources: Resources) {
        val calendar = Calendar.getInstance(resources.configuration.locale)
        calendar.firstDayOfWeek = Calendar.SUNDAY
        calendarWeek = calendar[Calendar.WEEK_OF_YEAR]
        calendarDate = calendarDateFormat.format(calendar.time)
        calendarDate?.split("/")?.let {
            calendarMonth = it[1]
            calendarYear = it[2]
        }
    }

    fun formatDate(date: String?, format: String?): Date? {
        if (date.isNullOrEmpty() || format.isNullOrEmpty()) {
            return null
        }
        return try {
            val simpleDateFormat = SimpleDateFormat(format, Locale.US)
            simpleDateFormat.parse(date)
        } catch (_: Exception) {
            null
        }
    }

    fun formatDate(date: Date?, targetDateFormat: String?): String {
        if (targetDateFormat.isNullOrEmpty() || date == null) {
            return ""
        }
        val postFormatter = SimpleDateFormat(targetDateFormat, Locale.US)
        return postFormatter.format(date)
    }

    fun formatDate(
        strdate: String?, sourceDateFormat: String?, targetDateFormat: String?
    ): String {

        if (strdate == null) return ""
        if (TextUtils.isEmpty(strdate) || TextUtils.isEmpty(sourceDateFormat) || TextUtils.isEmpty(
                targetDateFormat
            )
        ) {
            return ""
        }

        val form = SimpleDateFormat(sourceDateFormat, Locale.US)
        val date: Date?
        var newDateStr = ""

        try {
            date = form.parse(strdate)
            val postFormater = SimpleDateFormat(targetDateFormat, Locale.US)
            if (date != null) newDateStr = postFormater.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return newDateStr
    }

    fun getDateAsInt(date: Date?, format: String?): Int {
        return (DateFormat.format(format, date) as String).toInt()
    }

    fun getFormattedDate(date: Date?): String {
        val value: String = formatDate(date, DateFormats.d.label)
        return when (val day = value.toInt()) {
            11 -> {
                value + "th"
            }
            12 -> {
                value + "th"
            }
            13 -> {
                value + "th"
            }
            else -> {
                when (day % 10) {
                    1 -> value + "st"
                    2 -> value + "nd"
                    3 -> value + "rd"
                    else -> value + "th"
                }
            }
        }
    }

    fun getFormattedTimings(time: String?): String? {
        return if (time != null && time.isNotEmpty()) {
            "${time}M"
        } else time
    }

    fun timeDifferenceInDays(sTime: Date, eTime: Date): Long {
        val difference = sTime.time - eTime.time
        return TimeUnit.MILLISECONDS.toDays(difference)
    }

    fun timeDifferenceInMinutes(sTime: Date, eTime: Date): Long {
        val difference = sTime.time - eTime.time
        return TimeUnit.MILLISECONDS.toMinutes(difference)
    }

    fun timeDifferenceInSeconds(sTime: Date, eTime: Date): Long {
        val difference = sTime.time - eTime.time
        return TimeUnit.MILLISECONDS.toSeconds(difference)
    }

    fun timeDifferenceInHours(sTime: Date, eTime: Date): Long {
        val difference = sTime.time - eTime.time
        return TimeUnit.MILLISECONDS.toHours(difference)
    }

    fun formatCurrentDate(currentDate: Date?, targetDateFormat: String?): Date? {
        if (targetDateFormat.isNullOrEmpty() || currentDate == null) {
            return null
        }

        try {
            val simpleDateFormat = SimpleDateFormat(targetDateFormat, Locale.US)
            val newDateStr = simpleDateFormat.format(currentDate)
            return simpleDateFormat.parse(newDateStr)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return null
    }

    enum class DateFormats(val iD: Int, val label: String) {
        MMDDYY(0, "MM/dd/yy"), MMDDYYYY(1, "MM/dd/yyyy"), MMDDYY_Dashed(
            2, "MM-dd-yy"
        ),
        MMDDYYYY_Dashed(3, "MM-dd-yyyy"), YYYYMMDD_Dashed(4, "yyyy-MM-dd"), HHMM(
            5, "HH:mm"
        ),
        HHMMSS(6, "HH:mm:ss"),  // HH : 24 hrs format
        HHMMSSA(7, "hh:mm:ss a"),  // hh : 12 hrs format
        HHMMA(8, "hh:mm a"), YYYYMMDDTHHMMSS(
            9, YYYYMMDD_Dashed.label + "'T'" + HHMMSS.label
        ),// yyyy-MM-dd'T'HH:mm:ss 2018-06-29'T'01:26:27
        MMMMDDYYYY(10, "MMMM d, yyyy"), MMMMDDYYYYHHMMSS(
            11, MMMMDDYYYY.label + " " + HHMMSS.label
        ),
        MMMMDDYYYYHHMMSS_Dashed(12, MMMMDDYYYY.label + " - " + HHMMA.label), MMDDYYYYHHMMA(
            13, MMDDYYYY.label + " " + HHMMA.label
        ),  //  MM/dd/yyyy hh:mm a
        YY(14, "yy"),  // 16
        YYYY(15, "yyyy"), DDMMYYYY(16, "dd/MM/yyyy"), MMM(17, "MMM"),  // Dec
        dd(18, "dd"), MM(19, "MM"), MMYYYY(20, "MM/yyyy"), MMDDHHMMA(
            21, MMM.label + " " + dd.label + " @ " + HHMMA.label
        ),  //  MM/dd/yyyy hh:mm a;
        MMDDYYYYHHMMSS(22, MMDDYYYY.label + " " + HHMMSS.label), MMDDYYYY_Slashed(
            23, "MM/dd/yyyy"
        ),
        MMDDYYYYHHMMSSA(24, MMDDYYYY_Slashed.label + " " + HHMMSSA.label), MMDDYYHHMMA(
            25, MMDDYY.label + " " + HHMMA.label
        ),
        DDMMMMYYYY(26, "d MMMM, yyyy"), d(27, "d"), YYYYMMDDHHMMSS(
            28, YYYYMMDD_Dashed.label + "' '" + HHMMSS.label
        ),// yyyy-MM-dd HH:mm:ss
        MMDDYY_SlashedHHMMA(29, MMDDYY.label + "' '" + HHMMA.label), EEEMMMDDHHMMA(
            25, "EEE, MMM dd" + " " + HHMMA.label
        ),
        MMDDYYYYHHMMSSA_Slashed(30, "MM/dd/yyyy hh:mm:ss a"), MMDDYYYYHHMMA_Slashed(
            31, "MM/dd/yyyy hh:mm a"
        ),
        MMMMDDYYYYEEEE(10, "MMMM d, yyyy - EEEE"), EEEEMMMMDDYYYY(
            32, "EEEE MMMM d, yyyy"
        ),
        yyyyMMddTHHmmssSSS(33, "yyyy-MM-dd'T'HH:mm:ss.SSS"), MMMDD(34, "MMM dd"), MMMDDYYYY(
            35, "MMM dd, yyyy"
        ),
        yyyyMMddTHHmmss(36, "yyyy-MM-dd'T'HH:mm:ss");
    }
}