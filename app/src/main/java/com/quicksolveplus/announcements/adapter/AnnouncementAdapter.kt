package com.quicksolveplus.announcements.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daimajia.swipe.SwipeLayout
import com.quicksolveplus.dashboard.models.AnnouncementsItem
import com.quicksolveplus.qspmobile.R
import com.quicksolveplus.qspmobile.databinding.LayoutItemAnnouncementsBinding
import java.util.*

class AnnouncementAdapter(
    private val context: Context,
    private var announcementList: ArrayList<AnnouncementsItem>,
    val announcementActions: (position: Int) -> Unit
) : RecyclerView.Adapter<AnnouncementAdapter.ViewHolder>() {

    private val searchList: ArrayList<AnnouncementsItem> = arrayListOf()

    init {
        searchList.addAll(announcementList)
    }


    inner class ViewHolder(val binding: LayoutItemAnnouncementsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.clMain.setOnClickListener {
                announcementActions(adapterPosition)
            }
        }
    }


    fun filter(searchText: String) {
        val charText = searchText.lowercase(Locale.getDefault())
        announcementList.clear()
        if (charText.isEmpty()) {
            announcementList.addAll(searchList)
        } else {
            for (announcement in searchList) {
                val announcementSubject: String = announcement.announcementSubject
                val announcementSender: String = announcement.senderName
                if (charText.startsWith(" ") || announcementSubject.lowercase(Locale.getDefault()).contains(charText) || charText.startsWith(" ") || announcementSender.lowercase(Locale.getDefault()).contains(charText)) {
                    announcementList.add(announcement)
                }
            }
        }
        doRefresh(announcementList)
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun doRefresh(data: ArrayList<AnnouncementsItem>) {
        announcementList = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutItemAnnouncementsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val announcementList = announcementList[holder.adapterPosition]

        holder.binding.tvTitle.text = announcementList.announcementSubject
        holder.binding.tvDate.text = announcementList.sentDateTime
        holder.binding.tvFrom.text = announcementList.senderName
        holder.binding.slAnnouncement.isRightSwipeEnabled = announcementList.isRead



        holder.binding.slAnnouncement.addSwipeListener(object : SwipeLayout.SwipeListener {
            override fun onClose(layout: SwipeLayout) {
                Log.e("slAnnouncement", "onClose: ")
//                announcementList.isSelected = false
            }

            override fun onUpdate(layout: SwipeLayout, leftOffset: Int, topOffset: Int) {
                Log.e("slAnnouncement", "onUpdate: ")
//                handleSelection(holder.adapterPosition)
            }

            override fun onStartOpen(layout: SwipeLayout) {
                Log.e("slAnnouncement", "onStartOpen: ")
            }

            override fun onOpen(layout: SwipeLayout) {
                Log.e("slAnnouncement", "onOpen: ")
            }

            override fun onStartClose(layout: SwipeLayout) {
                Log.e("slAnnouncement", "onStartClose: ")
            }

            override fun onHandRelease(layout: SwipeLayout, xvel: Float, yvel: Float) {
//                handleSelection(holder.adapterPosition)
            }
        })


        holder.binding.apply {
            if (announcementList.isRead) {
                tvStatus.backgroundTintList = context.getColorStateList(R.color.color_read)
                tvStatus.text = context.getString(R.string.str_read)
            } else if (announcementList.isArchived) {
                tvStatus.backgroundTintList = context.getColorStateList(R.color.color_archive)
                tvDate.text = context.getString(R.string.str_archived)
            }
//            else if (!announcementList.IsRead) {
//                tvStatus.backgroundTintList = context.getColorStateList(R.color.color_unread)
//                tvStatus.text = context.getString(R.string.str_unread)
//            }

        }

    }

    private fun handleSelection(position: Int) {
        for (i in 0 until announcementList.size) {
            if (announcementList[i].isSelected) {
                announcementList[i].isSelected = false
                notifyItemChanged(i)
            }
        }
        announcementList[position].isSelected = true
        notifyItemChanged(position)
    }


    override fun getItemCount(): Int {
        return announcementList.size
    }


}



