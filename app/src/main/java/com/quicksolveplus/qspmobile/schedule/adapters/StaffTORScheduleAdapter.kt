package com.quicksolveplus.qspmobile.schedule.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.quicksolveplus.qspmobile.databinding.LayoutScheduleItemsBinding
import com.quicksolveplus.qspmobile.schedule.models.PreferenceScheduleItem
import com.quicksolveplus.qspmobile.schedule.models.TORScheduleItem
import com.quicksolveplus.utils.QSCalendar
import com.quicksolveplus.utils.QSColors
import com.quicksolveplus.utils.trimFloatValue
import java.util.*

/**
 * 04/04/23.
 *
 * @author hardkgosai.
 */
class StaffTORScheduleAdapter(private val items: ArrayList<TORScheduleItem>) :
    RecyclerView.Adapter<StaffTORScheduleAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: LayoutScheduleItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            LayoutScheduleItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val startDate = QSCalendar.formatDate(
            item.schedDate, QSCalendar.DateFormats.MMDDYY.label
        )
        val endDate = QSCalendar.formatDate(
            item.schedDateEnd, QSCalendar.DateFormats.MMDDYY.label
        )
        val startTime: String =
            QSCalendar.getFormattedDate(startDate) + " " + QSCalendar.getFormattedTimings(
                item.startTime
            )
        val endTime: String =
            QSCalendar.getFormattedDate(endDate) + " " + QSCalendar.getFormattedTimings(
                item.endTime
            )

        holder.binding.tvStartTime.text = startTime
        holder.binding.tvEndTime.text = endTime
        holder.binding.tvTitle.text = String.format(
            "%s (%s)", item.workPrefType, trimFloatValue(item.hours)
        )

        holder.binding.llShift.setBackgroundColor(Color.parseColor(QSColors.getShiftBackgroundForTOR()))
        holder.binding.tvStartTime.setTextColor(
            Color.parseColor(QSColors.getShiftForegroundForTOR())
        )
        holder.binding.tvEndTime.setTextColor(
            Color.parseColor(QSColors.getShiftForegroundForTOR())
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

}