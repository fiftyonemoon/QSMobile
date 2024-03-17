package com.quicksolveplus.qspmobile.schedule.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.quicksolveplus.modifiers.Glidifiers.loadGlide
import com.google.gson.JsonObject
import com.quicksolveplus.api.RequestParameters
import com.quicksolveplus.qspmobile.R
import com.quicksolveplus.qspmobile.databinding.LayoutItemEmployeeBinding
import com.quicksolveplus.qspmobile.employee.model.Employees
import com.quicksolveplus.qspmobile.schedule.viewmodel.TrainedEmployeesViewModel
import com.quicksolveplus.utils.Preferences
import java.io.File
import java.util.*

class TrainedEmployeesAdapter(
    private var employees: Employees,
    private val viewModel: TrainedEmployeesViewModel,
    val onItemClick: (position: Int) -> Unit
) : RecyclerView.Adapter<TrainedEmployeesAdapter.ViewHolder>() {

    private lateinit var searchList: Employees

    init {
        searchList.addAll(employees)
    }

    inner class ViewHolder(val binding: LayoutItemEmployeeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val context: Context = itemView.context

        init {
            itemView.setOnClickListener { onItemClick(adapterPosition) }
        }

        fun requestProfilePicture(body: JsonObject) {
            viewModel.getWorkerProfilePic(body, Pair(adapterPosition, employees[adapterPosition]))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            LayoutItemEmployeeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return employees.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val employee = employees[position]
        holder.binding.tvEmployee.text = employee.FirstName.plus(" ").plus(employee.LastName)
        holder.binding.tvStatus.isVisible = false
        holder.binding.ivProgress.isVisible = true

        val filename = employee.WorkerProfilePic
        val file = File(holder.context.cacheDir, filename)

        if (file.exists()) {

            holder.binding.ivProgress.isVisible = false
            holder.binding.ivEmployee.loadGlide(
                file, ContextCompat.getDrawable(holder.context, R.drawable.ic_avatar)
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
        employees.clear()
        if (charText.isEmpty()) {
            employees.addAll(searchList)
        } else {
            for (client in searchList) {
                val clientName: String = client.FirstName + " " + client.LastName
                if (charText.startsWith(" ") || clientName.lowercase(Locale.getDefault())
                        .contains(charText)
                ) {
                    employees.add(client)
                }
            }
        }
        doRefresh(employees)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun doRefresh(data: Employees) {
        employees = data
        notifyDataSetChanged()
    }
}