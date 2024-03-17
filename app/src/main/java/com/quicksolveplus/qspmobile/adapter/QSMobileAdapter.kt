package com.quicksolveplus.qspmobile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.quicksolveplus.qspmobile.databinding.LayoutItemMenuBinding
import com.quicksolveplus.qspmobile.model.MenuItem

class QSMobileAdapter(
    private val menuList: ArrayList<MenuItem>,
    private val onMenuItemClick: OnMenuItemClick
) :
    RecyclerView.Adapter<QSMobileAdapter.ViewHolder>() {

    inner class ViewHolder(var binding: LayoutItemMenuBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setData(menuItem: MenuItem, position: Int) {
            binding.apply {
                if (position % 2 == 0) {
                    cvEnd.isVisible = true
                    cvStart.isGone = true
                    tvEnd.text = menuItem.title
                    ivEnd.setImageResource(menuItem.imgIcon)

                } else {
                    cvEnd.isGone = true
                    cvStart.isVisible = true
                    tvStart.text = menuItem.title
                    ivStart.setImageResource(menuItem.imgIcon)
                }

                root.setOnClickListener {
                    onMenuItemClick.onMenuItemClick(menuItem)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutItemMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(menuList[position], position)
    }

    interface OnMenuItemClick {
        fun onMenuItemClick(menuItem: MenuItem)
    }
}
