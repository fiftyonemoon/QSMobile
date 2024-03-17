package com.quicksolveplus.covid.covid_result.test_result.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.quicksolveplus.covid.covid_result.test_result.models.TestingResultItem
import com.quicksolveplus.qspmobile.databinding.LayoutItemTestResultBinding
import com.quicksolveplus.utils.QSCalendar

class TestResultAdapter(
    private val context: Context,
    private val testResultList: ArrayList<TestingResultItem>,
    val testResultAction: (position: Int) -> Unit
) : RecyclerView.Adapter<TestResultAdapter.ViewHolder>() {


    inner class ViewHolder(val binding: LayoutItemTestResultBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                testResultAction(adapterPosition)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutItemTestResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val startDate: String = QSCalendar.formatDate(
            testResultList[position].WeekStartDate,
            QSCalendar.DateFormats.yyyyMMddTHHmmss.label,
            QSCalendar.DateFormats.MMMDD.label
        )
        val endDate: String = QSCalendar.formatDate(
            testResultList[position].WeekEndDate,
            QSCalendar.DateFormats.yyyyMMddTHHmmss.label,
            QSCalendar.DateFormats.MMMDDYYYY.label
        )
        holder.binding.tvDate.text = "$startDate - $endDate"
        holder.binding.tvStatus.text = testResultList[position].TestingStatus
        holder.binding.tvStatus.setTextColor(Color.parseColor(testResultList[position].TestingStatusForecolor))
    }

    override fun getItemCount(): Int {
        return testResultList.size
    }


}