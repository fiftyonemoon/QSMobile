package com.quicksolveplus.qspmobile.schedule.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.quicksolveplus.qspmobile.R
import com.quicksolveplus.qspmobile.databinding.LayoutScheduleItemsBinding
import com.quicksolveplus.qspmobile.schedule.models.SchedulesItem
import com.quicksolveplus.utils.QSCalendar
import com.quicksolveplus.utils.QSColors
import com.quicksolveplus.utils.trimFloatValue
import java.text.SimpleDateFormat
import java.util.*

/**
 * 04/04/23.
 *
 * @author hardkgosai.
 */
class SchedulesAdapter(
    private val items: ArrayList<SchedulesItem>,
    private val isClient: Boolean,
    private val listener: OnItemClickedListener
) : RecyclerView.Adapter<SchedulesAdapter.ViewHolder>() {

    interface OnItemClickedListener {
        fun onItemClicked(position: Int)
        fun onLinkedClientClicked(position: Int)
    }

    inner class ViewHolder(val binding: LayoutScheduleItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val context: Context = itemView.context

        init {
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    listener.onItemClicked(adapterPosition)
                }
            }
            binding.ivLinkedClient.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    listener.onLinkedClientClicked(adapterPosition)
                }
            }
        }
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
            QSCalendar.getFormattedDate(startDate) + " - " + QSCalendar.getFormattedTimings(
                item.startTime
            )
        val endTime: String =
            QSCalendar.getFormattedDate(endDate) + " - " + QSCalendar.getFormattedTimings(
                item.endTime
            )

        holder.binding.tvStartTime.text = startTime
        holder.binding.tvEndTime.text = endTime

        holder.binding.llShift.setBackgroundColor(
            Color.parseColor(QSColors.getShiftBackground(item))
        )
        holder.binding.tvStartTime.setTextColor(
            Color.parseColor(QSColors.getShiftForeground(item, isClient))
        )
        holder.binding.tvEndTime.setTextColor(
            Color.parseColor(QSColors.getShiftForeground(item, isClient))
        )

        val dateFormat = SimpleDateFormat("MM/dd/yy", Locale.US)
        var selectedDate: Date? = Date()
        var scheduleDate: Date? = Date()

        if (item.selectedDate != null) {
            selectedDate = dateFormat.parse(item.selectedDate)
        }
        if (item.schedDate != null) {
            scheduleDate = dateFormat.parse(item.schedDate)
        }

        if (selectedDate != null && scheduleDate != null) {
            holder.binding.tvTitle.text = if (!isClient) {
                if (scheduleDate < selectedDate) {
                    String.format(
                        "* %s %s - %s (%s)",
                        item.clientFirstName,
                        item.clientLastName,
                        item.taskName,
                        trimFloatValue(item.hours)
                    )
                } else {
                    String.format(
                        "%s %s - %s (%s)",
                        item.clientFirstName,
                        item.clientLastName,
                        item.taskName,
                        trimFloatValue(item.hours)
                    )
                }
            } else {
                if (scheduleDate < selectedDate) {
                    String.format(
                        "* %s %s - %s (%s)",
                        item.staffFirstName,
                        item.staffLastName,
                        item.taskName,
                        trimFloatValue(item.hours)
                    )
                } else {
                    String.format(
                        "%s %s - %s (%s)",
                        item.staffFirstName,
                        item.staffLastName,
                        item.taskName,
                        trimFloatValue(item.hours)
                    )
                }
            }
        }

        //if has meal break
        if (item.hasMealBreak == 1) {
            holder.binding.tvHasMealBreak.isVisible = true
            holder.binding.tvHasMealBreak.text = String.format(
                holder.context.getString(R.string.str_rest_period) + " (%s-%s)",
                QSCalendar.getFormattedTimings(item.mealOut),
                QSCalendar.getFormattedTimings(item.mealIn)
            )
        } else {
            holder.binding.tvHasMealBreak.isVisible = false
        }

        //if has shift objectives, shift sub code
        holder.binding.ivObjectives.isVisible =
            item.hasObjectives && item.hasSubCodes && item.serviceNotes?.isNotEmpty() == true

        holder.binding.ivLinkedClient.isVisible = item.linkedSchedules.isNotEmpty()
    }

    override fun getItemCount(): Int {
        return items.size
    }

}