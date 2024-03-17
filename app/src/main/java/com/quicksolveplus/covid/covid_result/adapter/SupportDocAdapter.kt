package com.quicksolveplus.covid.covid_result.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import com.quicksolveplus.api.RequestParameters
import com.quicksolveplus.covid.covid_result.viewmodel.CovidResultViewModel
import com.quicksolveplus.modifiers.Glidifiers.loadGlide
import com.quicksolveplus.qspmobile.R
import com.quicksolveplus.qspmobile.databinding.LayoutItemSupportDocBinding
import com.quicksolveplus.utils.Preferences
import java.io.File

class SupportDocAdapter(
    private val context: Context,
    private val supportDocList: ArrayList<String>,
    private val viewModel: CovidResultViewModel,
    val supportDocActions: (position: Int) -> Unit
) : RecyclerView.Adapter<SupportDocAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: LayoutItemSupportDocBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val context: Context = itemView.context

        init {
            itemView.setOnClickListener {
                supportDocActions(adapterPosition)
            }
        }

        fun requestImageFile(body: JsonObject) {
            viewModel.getImageFile(body, Pair(adapterPosition, supportDocList[adapterPosition]))
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutItemSupportDocBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = supportDocList[position]

        val file = File(holder.context.cacheDir, item)
        if (file.exists()) {
            holder.binding.ivProgress.isVisible = false
            holder.binding.ivSupportDoc.loadGlide(file)
            ContextCompat.getDrawable(holder.context, R.drawable.ic_avatar)
        } else {
            holder.binding.ivProgress.isVisible = true
            holder.binding.ivSupportDoc.loadGlide(R.drawable.ic_avatar)
            Preferences.instance?.let {
                val body = RequestParameters.forGetImageFile(item, 10)
                holder.requestImageFile(body)
            }
        }

    }

    override fun getItemCount(): Int {
        return supportDocList.size
    }

}