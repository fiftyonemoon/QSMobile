package com.quicksolveplus.companies.dialogs

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.quicksolveplus.companies.CompanyActivity
import com.quicksolveplus.companies.models.QSPMobileWebClient
import com.quicksolveplus.qspmobile.R
import com.quicksolveplus.qspmobile.databinding.DialogSavedCompaniesBinding
import com.quicksolveplus.utils.Preferences

/**
 * 20/03/23.
 *
 * @author hardkgosai.
 */
class SavedCompaniesDialog(
    context: Context, private val onCompanySelect: (qspMobileWebClient: QSPMobileWebClient) -> Unit
) : Dialog(context) {

    private lateinit var binding: DialogSavedCompaniesBinding
    private val shortListedCompanies by lazy {
        Preferences.instance?.shortListedCompanies ?: arrayListOf()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.dialog_insets))

        binding = DialogSavedCompaniesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
        setListView()
    }

    private fun initUI() {
        binding.tvTitle.text = context.getString(R.string.str_shortlisted_companies)
        binding.tvEdit.setOnClickListener {
            dismiss()
            navigateToCompany()
        }
    }

    private fun setListView() {
        val adapter = object : ArrayAdapter<QSPMobileWebClient>(
            /* context = */ context,
            /* resource = */ R.layout.layout_item_list,
            /* textViewResourceId = */ R.id.tv,
            /* objects = */ shortListedCompanies
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val textview = view.findViewById<TextView>(R.id.tv)
                val company = shortListedCompanies[position]
                val selectedCompany = Preferences.instance?.defaultCompany

                val isSelected = selectedCompany?.companyAbbreviation == company.companyAbbreviation

                val selectedTextColor =
                    if (isSelected) ContextCompat.getColor(context, R.color.white)
                    else ContextCompat.getColor(context, R.color.black)

                val selectedBackgroundColor =
                    if (isSelected) ContextCompat.getColor(context, R.color.app_color)
                    else ContextCompat.getColor(context, R.color.white)

                textview.text = company.companyAbbreviation
                textview.setTextColor(selectedTextColor)
                textview.setBackgroundColor(selectedBackgroundColor)
                return view
            }
        }

        binding.lvCompanies.adapter = adapter
        binding.lvCompanies.setOnItemClickListener { _, _, position, _ ->
            val company = shortListedCompanies[position]
            onCompanySelect(company)
            dismiss()
        }
    }

    private fun navigateToCompany() {
        val intent = Intent(context, CompanyActivity::class.java)
        context.startActivity(intent)
    }
}