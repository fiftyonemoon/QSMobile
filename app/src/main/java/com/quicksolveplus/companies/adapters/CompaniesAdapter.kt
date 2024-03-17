package com.quicksolveplus.companies.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.quicksolveplus.companies.models.QSPMobileWebClient
import com.quicksolveplus.qspmobile.databinding.AdapterCompaniesBinding

/**
 * 20/03/23.
 *
 * @author hardkgosai.
 */
class CompaniesAdapter(
    private val companies: ArrayList<QSPMobileWebClient>,
    private val shortListedCompanies: ArrayList<QSPMobileWebClient>
) : RecyclerView.Adapter<CompaniesAdapter.ViewHolder>() {

    private val searchList = ArrayList(companies)

    inner class ViewHolder(val binding: AdapterCompaniesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    val company = companies[adapterPosition]
                    if (isCompanyShortListed(company)) {
                        shortListedCompanies.remove(company)
                    } else shortListedCompanies.add(company)
                    notifyItemChanged(adapterPosition)
                }
            }
        }

        fun isCompanyShortListed(qspMobileWebClient: QSPMobileWebClient): Boolean {
            return shortListedCompanies.contains(qspMobileWebClient)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AdapterCompaniesBinding.inflate(LayoutInflater.from(parent.context))
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val company = companies[position]
        holder.binding.tvCompany.text = company.companyAbbreviation
        holder.binding.ivCheck.isSelected = holder.isCompanyShortListed(company)
    }

    override fun getItemCount(): Int {
        return companies.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun filter(query: String?) {
        companies.clear()
        if (query.isNullOrEmpty()) {
            companies.addAll(searchList)
        } else {
            searchList.map {
                if (it.companyAbbreviation.trim().lowercase().contains(query.trim().lowercase())) {
                    companies.add(it)
                }
            }
        }
        notifyDataSetChanged()
    }
}