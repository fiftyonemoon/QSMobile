package com.quicksolveplus.covid.covid_result.test_result.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.quicksolveplus.covid.covid_result.test_result.models.Options
import com.quicksolveplus.qspmobile.databinding.LayoutItemOptionsBinding
import java.util.*


class OptionListAdapter(
    private val context: Context,
    private var optionList: ArrayList<Options>,
    val optionActions: (position: Int) -> Unit
) : RecyclerView.Adapter<OptionListAdapter.ViewHolder>() {


    inner class ViewHolder(val binding: LayoutItemOptionsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                optionActions(adapterPosition)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutItemOptionsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val optionList = optionList[holder.adapterPosition]
        holder.binding.tvName.text = optionList[position].Text
    }



    override fun getItemCount(): Int {
        return optionList.size
    }

}