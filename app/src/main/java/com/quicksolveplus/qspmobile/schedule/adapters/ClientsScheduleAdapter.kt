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
import com.quicksolveplus.qspmobile.clients.models.ClientsItem
import com.quicksolveplus.qspmobile.databinding.LayoutItemClientsBinding
import com.quicksolveplus.qspmobile.schedule.viewmodel.ClientScheduleViewModel
import com.quicksolveplus.utils.Preferences
import java.io.File
import java.util.*

class ClientsScheduleAdapter(
    private var clientsList: ArrayList<ClientsItem>,
    private val viewModel: ClientScheduleViewModel,
    val onItemClick: (position: Int) -> Unit
) : RecyclerView.Adapter<ClientsScheduleAdapter.ViewHolder>() {

    private val searchList: ArrayList<ClientsItem> = arrayListOf()

    init {
        searchList.addAll(clientsList)
    }

    inner class ViewHolder(val binding: LayoutItemClientsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val context: Context = itemView.context

        init {
            itemView.setOnClickListener { onItemClick(adapterPosition) }
        }

        fun requestProfilePicture(body: JsonObject) {
            viewModel.getClientProfilePic(body, Pair(adapterPosition, clientsList[adapterPosition]))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            LayoutItemClientsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return clientsList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val client = clientsList[position]
        holder.binding.tvClient.text = client.FirstName.plus(" ").plus(client.LastName)
        holder.binding.ivProgress.isVisible = true

        val filename = client.ClientProfilePic
        val file = File(holder.context.cacheDir, filename)

        if (file.exists()) {

            holder.binding.ivProgress.isVisible = false
            holder.binding.ivClient.loadGlide(
                file,
                ContextCompat.getDrawable(holder.context, R.drawable.ic_avatar)
            )

        } else {

            holder.binding.ivProgress.isVisible = true
            holder.binding.ivClient.loadGlide(R.drawable.ic_avatar)
            Preferences.instance?.let {
                val level = it.user?.userLevel1 ?: -1
                val body = RequestParameters.forProfilePicture(filename, level)
                holder.requestProfilePicture(body)
            }
        }
    }

    fun filter(searchText: String) {
        val charText = searchText.lowercase(Locale.getDefault())
        clientsList.clear()
        if (charText.isEmpty()) {
            clientsList.addAll(searchList)
        } else {
            for (client in searchList) {
                val clientName: String = client.FirstName + " " + client.LastName
                if (charText.startsWith(" ") || clientName.lowercase(Locale.getDefault())
                        .contains(charText)
                ) {
                    clientsList.add(client)
                }
            }
        }
        doRefresh(clientsList)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun doRefresh(data: ArrayList<ClientsItem>) {
        clientsList = data
        notifyDataSetChanged()
    }
}