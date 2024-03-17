package com.quicksolveplus.covid.covid_tracking.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.quicksolveplus.covid.covid_tracking.models.CovidItem
import com.quicksolveplus.covid.covid_tracking.viewmodel.CovidViewModel
import com.quicksolveplus.dialogs.QSImageView
import com.quicksolveplus.qspmobile.databinding.LayoutItemBoosterBinding
import com.quicksolveplus.utils.Constants
import com.quicksolveplus.utils.QSCalendar


class BoosterAdapter(
    private val context: Context,
    private val boosterList: ArrayList<CovidItem>,
    private val viewModel: CovidViewModel,
    val boosterActions: (boosterModel: BoosterAdapter) -> Unit
) : RecyclerView.Adapter<BoosterAdapter.ViewHolder>() {

    private var supportDocumentAdapter: SupportDocumentAdapter? = null

    inner class ViewHolder(val binding: LayoutItemBoosterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutItemBoosterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.tvType.text = boosterList[position].ComplianceType

        Log.e("TAG", "onBindViewHolder: "+boosterList[position].SupportingImageNames )

        holder.binding.tvDoseDate.text =
            QSCalendar.formatDate(
                boosterList[position].Dose1Date,
                QSCalendar.DateFormats.yyyyMMddTHHmmss.label,
                QSCalendar.DateFormats.MMDDYYYY.label
            )

        supportDocumentAdapter = SupportDocumentAdapter(context, boosterList[position].SupportingImageNames,viewModel) { pos ->
            val intent = Intent(context, QSImageView::class.java)
            intent.putExtra(Constants.covidVaccine,boosterList[position].SupportingImageNames[pos])
            context.startActivity(intent)
        }
        holder.binding.rvSupportDocument.adapter = supportDocumentAdapter
    }

    override fun getItemCount(): Int {
        return boosterList.size
    }


}