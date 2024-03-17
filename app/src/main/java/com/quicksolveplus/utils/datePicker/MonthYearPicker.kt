package com.quicksolveplus.utils.datePicker

import android.app.Activity
import com.quicksolveplus.qspmobile.R
import java.util.*

/**
 * 10/04/23.
 *
 * @author hardkgosai.
 */
class MonthYearPicker(private val activity: Activity) {

    private val minYear = 1970
    private val maxYear = 2099

    private var currentYear = -1
    private var currentMonth = -1

    private val monthCustomNumberPicker: NumberPicker? = null
    private val yearCustomNumberPicker: NumberPicker? = null

    private val monthsName: Array<String> by lazy {
        arrayOf(
            activity.getString(R.string.str_month_january),
            activity.getString(R.string.str_month_february),
            activity.getString(R.string.str_month_march),
            activity.getString(R.string.str_month_april),
            activity.getString(R.string.str_month_may),
            activity.getString(R.string.str_month_june),
            activity.getString(R.string.str_month_july),
            activity.getString(R.string.str_month_august),
            activity.getString(R.string.str_month_september),
            activity.getString(R.string.str_month_october),
            activity.getString(R.string.str_month_november),
            activity.getString(R.string.str_month_december)
        )
    }

    init {
        val instance = Calendar.getInstance()
        currentMonth = instance[Calendar.MONTH]
        currentYear = instance[Calendar.YEAR]
    }
}