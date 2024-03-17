package com.quicksolveplus.settings.language.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.quicksolveplus.qspmobile.databinding.LayoutItemLanguageBinding
import com.quicksolveplus.settings.language.models.LanguageModel

class LanguageAdapter(
    private val languageModelList: ArrayList<LanguageModel>,
    private val languageActions: (languageModel: LanguageModel) -> Unit
) : RecyclerView.Adapter<LanguageAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: LayoutItemLanguageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                handleSelection(adapterPosition)
                languageActions(languageModelList[adapterPosition])
            }
        }

        private fun handleSelection(position: Int) {
            for (i in 0 until languageModelList.size) {
                if (languageModelList[i].isSelected) {
                    languageModelList[i].isSelected = false
                    notifyItemChanged(i)
                }
            }
            languageModelList[position].isSelected = true
            notifyItemChanged(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutItemLanguageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val language = languageModelList[position]
        holder.binding.tvLanguageName.text = language.languageTitle
        holder.binding.ivSelected.isVisible = language.isSelected
    }

    override fun getItemCount(): Int {
        return languageModelList.size
    }
}