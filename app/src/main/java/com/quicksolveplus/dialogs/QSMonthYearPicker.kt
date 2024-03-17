package com.quicksolveplus.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import androidx.core.content.ContextCompat
import com.quicksolveplus.qspmobile.R
import com.quicksolveplus.qspmobile.databinding.DialogMonthYearPickerBinding
import java.util.*

/**
 * 11/04/23.
 * Month Year Dialog.
 *
 * @author hardkgosai.
 */
class QSMonthYearPicker(
    context: Context, private val callback: (monthName: String, month: Int, year: Int) -> Unit
) : Dialog(context) {

    private lateinit var binding: DialogMonthYearPickerBinding

    private val minYear = 1970
    private val maxYear = 2099

    private var selectedMonth = -1
    private var selectedYear = -1

    private var monthsName: Array<String> = arrayOf()

    init {
        Calendar.getInstance().apply {
            selectedMonth = this[Calendar.MONTH]
            selectedYear = this[Calendar.YEAR]
        }
        monthsName = arrayOf(
            context.getString(R.string.str_month_january),
            context.getString(R.string.str_month_february),
            context.getString(R.string.str_month_march),
            context.getString(R.string.str_month_april),
            context.getString(R.string.str_month_may),
            context.getString(R.string.str_month_june),
            context.getString(R.string.str_month_july),
            context.getString(R.string.str_month_august),
            context.getString(R.string.str_month_september),
            context.getString(R.string.str_month_october),
            context.getString(R.string.str_month_november),
            context.getString(R.string.str_month_december)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.dialog_insets))

        binding = DialogMonthYearPickerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
    }

    private fun initUI() {
        binding.tvOk.setOnClickListener {
            val month = binding.monthPicker.value
            val year = binding.yearPicker.value
            val monthName = getSelectedMonthName(month)
            callback(monthName, month, year)
            dismiss()
        }
        binding.monthPicker.run {
            displayedValues = monthsName
            minValue = 0
            maxValue = monthsName.size - 1
            value = selectedMonth
            descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS
        }
        binding.yearPicker.run {
            minValue = minYear
            maxValue = maxYear
            value = selectedYear
            descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS
        }
    }

    fun getSelectedMonthName(month: Int): String {
        return monthsName[month]
    }

    fun getSelectedMonthName(): String {
        return monthsName[selectedMonth]
    }

    fun getSelectedMonth(): Int {
        return selectedMonth
    }

    fun getSelectedYear(): Int {
        return selectedYear
    }
}