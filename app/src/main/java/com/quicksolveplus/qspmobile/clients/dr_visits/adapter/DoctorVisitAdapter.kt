package com.quicksolveplus.qspmobile.clients.dr_visits.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.quicksolveplus.qspmobile.clients.dr_visits.model.VisitDoctorItem
import com.quicksolveplus.qspmobile.databinding.LayoutItemDoctorBinding

class DoctorVisitAdapter(
    private var visitDoctorList: ArrayList<VisitDoctorItem>,
    val onItemClick: (position: Int) -> Unit
) : RecyclerView.Adapter<DoctorVisitAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: LayoutItemDoctorBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val context: Context = itemView.context

        init {
            itemView.setOnClickListener { onItemClick(adapterPosition) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutItemDoctorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return visitDoctorList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.tvDoctor.text = visitDoctorList[position].VisitDetail
    }
}