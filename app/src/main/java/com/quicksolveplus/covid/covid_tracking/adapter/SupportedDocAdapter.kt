package com.quicksolveplus.covid.covid_tracking.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import com.quicksolveplus.api.RequestParameters
import com.quicksolveplus.covid.covid_tracking.booster.add_booster.viewmodel.AddBoosterViewModel
import com.quicksolveplus.modifiers.Glidifiers.loadGlide
import com.quicksolveplus.qspmobile.R
import com.quicksolveplus.qspmobile.databinding.LayoutItemVaccineDocBinding
import com.quicksolveplus.utils.Preferences
import java.io.File

class SupportedDocAdapter(
    private val context: Context,
    private val supportDocList: ArrayList<String>,
    private val viewModel: AddBoosterViewModel,
    val supportDocActions: (position: Int) -> Unit,
) : RecyclerView.Adapter<SupportedDocAdapter.ViewHolder>() {


    inner class ViewHolder(val binding: LayoutItemVaccineDocBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val context: Context = itemView.context

        init {
            binding.ivDeleteSupportedDoc.setOnClickListener{
                supportDocActions(adapterPosition)
            }
        }
        fun requestImageFile(body: JsonObject) {
            viewModel.getImageFile(body, Pair(adapterPosition, supportDocList[adapterPosition]))
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutItemVaccineDocBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        val  item = supportDocList[position]
        val file = File(holder.context.cacheDir, item)

        if (file.exists()) {
            holder.binding.ivProgress.isVisible = false
            holder.binding.ivSupportedDoc.loadGlide(file, ContextCompat.getDrawable(holder.context, R.drawable.ic_avatar))
        } else {
            holder.binding.ivProgress.isVisible = true
            holder.binding.ivSupportedDoc.loadGlide(R.drawable.ic_avatar)
            Preferences.instance?.let {
                val body = RequestParameters.forGetImageFile(item, 10)
                holder.requestImageFile(body)
            }
        }


//        holder.binding.ivSupportedDoc.loadGlide(supportDocList[position].SupportingImageNames)
    }

    override fun getItemCount(): Int {
        return supportDocList.size
    }

}