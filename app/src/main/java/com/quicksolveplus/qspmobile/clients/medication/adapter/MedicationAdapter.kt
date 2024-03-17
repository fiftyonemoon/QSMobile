package com.quicksolveplus.qspmobile.clients.medication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.quicksolveplus.qspmobile.R
import com.quicksolveplus.qspmobile.clients.medication.model.MedicationItem
import com.quicksolveplus.qspmobile.databinding.LayoutItemMedicationBinding
import com.quicksolveplus.utils.QSCalendar

class MedicationAdapter(
    private var medicationList: ArrayList<MedicationItem>,
    private var isDiscontinuedMedDisplay: Boolean = false,
    val onItemClick: (position: Int) -> Unit,
) : RecyclerView.Adapter<MedicationAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: LayoutItemMedicationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val context: Context = itemView.context

        init {
            itemView.setOnClickListener { onItemClick(adapterPosition) }
        }

        fun setDataToView(medicationItem: MedicationItem) {

            if (medicationItem.DateDiscontinued?.isNotEmpty() == true) {
                if (isDiscontinuedMedDisplay) {
                    binding.flOverlay.isVisible = true
                }
            } else {
                binding.flOverlay.isGone = true
            }
            if (medicationItem.Precautions?.isNotEmpty() == true) {
                binding.tvMedication.text = medicationItem.Precautions
            }
            if (medicationItem.GenericName?.isNotEmpty() == true) {
                binding.tvGenericName.text = medicationItem.GenericName
            }
            var strengthValue = ""
            if (medicationItem.Strength?.isNotEmpty() == true) {
                strengthValue = medicationItem.Strength
            }
            var strengthUnit = ""
            if ((!medicationItem.StrengthUnit.equals(
                    context.getString(R.string.str_select),
                    true
                ) && !medicationItem.StrengthUnit.equals(
                    context.getString(R.string.lbl_select),
                    true
                )) && (medicationItem.StrengthUnit?.isNotEmpty() == true)
            ) {
                strengthUnit = medicationItem.StrengthUnit
            }
            binding.tvStrength.text = String.format("%s %s", strengthValue, strengthUnit)

            var dosageValue = ""
            if (medicationItem.Dosage?.isNotEmpty() == true) {
                dosageValue = medicationItem.Dosage
            }

            var dosageUnit = ""
            if (!medicationItem.DosageUnit.equals(
                    context.getString(R.string.str_select), true
                ) && !medicationItem.DosageUnit.equals(
                    context.getString(R.string.lbl_select), true
                ) && medicationItem.DosageUnit?.isNotEmpty() == true
            ) {
                dosageUnit = medicationItem.DosageUnit
            }

            binding.tvMedication.text = String.format("%s %s", dosageValue, dosageUnit)

            if (medicationItem.Frequency?.isNotEmpty() == true) {
                binding.tvFrequency.text = medicationItem.Frequency
            }

            if (medicationItem.Treatment?.isNotEmpty() == true) {
                binding.tvTreatment.text = medicationItem.Treatment
            }

            if (!medicationItem.PrescribedBy.equals(
                    context.getString(R.string.str_select), true
                ) && !medicationItem.PrescribedBy.equals(
                    context.getString(R.string.lbl_select), true
                ) && medicationItem.PrescribedBy?.isNotEmpty() == true
            ) {
                binding.tvPrescribed.text = medicationItem.PrescribedBy
            }

            if (medicationItem.DateDiscontinued?.isNotEmpty() == true
            ) {
                binding.tvDC.text = QSCalendar.formatDate(
                    medicationItem.DateDiscontinued,
                    QSCalendar.DateFormats.YYYYMMDDTHHMMSS.label,
                    QSCalendar.DateFormats.MMDDYYYY.label
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutItemMedicationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return medicationList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setDataToView(medicationList[position])
    }

    fun showHideDiscontinuedMed(isDiscontinuedMedDisplay: Boolean) {
        this.isDiscontinuedMedDisplay = isDiscontinuedMedDisplay
        notifyDataSetChanged()
    }
}