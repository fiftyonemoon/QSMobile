package com.quicksolveplus.qspmobile.schedule.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.quicksolveplus.modifiers.Glidifiers.loadGlide
import com.google.gson.JsonObject
import com.quicksolveplus.api.RequestParameters
import com.quicksolveplus.qspmobile.R
import com.quicksolveplus.qspmobile.databinding.LayoutItemEmployeeBinding
import com.quicksolveplus.qspmobile.employee.model.EmployeesItem
import com.quicksolveplus.qspmobile.schedule.viewmodel.EmployeeScheduleViewModel
import com.quicksolveplus.utils.Preferences
import java.io.File
import java.util.*

class EmployeeScheduleAdapter(
    private var employeeList: ArrayList<EmployeesItem>,
    private val viewModel: EmployeeScheduleViewModel,
    val onItemClick: (position: Int) -> Unit
) : RecyclerView.Adapter<EmployeeScheduleAdapter.ViewHolder>() {

    private val searchList: ArrayList<EmployeesItem> = arrayListOf()

    init {
        searchList.addAll(employeeList)
    }

    inner class ViewHolder(val binding: LayoutItemEmployeeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val context: Context = itemView.context

        init {
            itemView.setOnClickListener { onItemClick(adapterPosition) }
        }

        fun requestProfilePicture(body: JsonObject) {
            viewModel.getWorkerProfilePic(
                body,
                Pair(adapterPosition, employeeList[adapterPosition])
            )
        }

        fun setData(employee: EmployeesItem) {
            binding.apply {
                tvEmployee.text = employee.FirstName.plus(" ").plus(employee.LastName)
                if (employee.VaccineStatus.isNotEmpty()) {
                    if (employee.VaccineStatus.equals("Vaccination", true)) {
                        tvStatus.text = context.getString(R.string.str_vaccinated)
                        tvStatus.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.app_color
                            )
                        )
                        tvStatus.isVisible = true
                    } else if (employee.VaccineStatus.equals("Exemption", true)) {
                        tvStatus.text = context.getString(R.string.str_exempt)
                        tvStatus.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.color_hint
                            )
                        )
                        tvStatus.isVisible = true
                    }
                } else {
                    tvStatus.isGone = true
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            LayoutItemEmployeeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return employeeList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val employee = employeeList[position]
        holder.setData(employeeList[position])

        val filename = employee.WorkerProfilePic
        val file = File(holder.context.cacheDir, filename)

        if (file.exists()) {

            holder.binding.ivProgress.isVisible = false
            holder.binding.ivEmployee.loadGlide(
                file,
                ContextCompat.getDrawable(holder.context, R.drawable.ic_avatar)
            )

        } else {

            holder.binding.ivProgress.isVisible = true
            holder.binding.ivEmployee.loadGlide(R.drawable.ic_avatar)
            Preferences.instance?.let {
                val level = it.user?.userLevel1 ?: -1
                val body = RequestParameters.forProfilePicture(filename, level)
                holder.requestProfilePicture(body)
            }
        }
    }

    fun filter(searchText: String) {
        val charText = searchText.lowercase(Locale.getDefault())
        employeeList.clear()
        if (charText.isEmpty()) {
            employeeList.addAll(searchList)
        } else {
            for (employee in searchList) {
                val employeeName: String = employee.FirstName + " " + employee.LastName
                if (charText.startsWith(" ") || employeeName.lowercase(Locale.getDefault())
                        .contains(charText)
                ) {
                    employeeList.add(employee)
                }
            }
        }
        doRefresh(employeeList)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun doRefresh(data: ArrayList<EmployeesItem>) {
        employeeList = data
        notifyDataSetChanged()
    }
}