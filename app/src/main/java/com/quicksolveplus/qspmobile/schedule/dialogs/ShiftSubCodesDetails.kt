package com.quicksolveplus.qspmobile.schedule.dialogs

import android.app.Activity
import android.app.Dialog

import android.os.Bundle
import android.view.Window
import android.widget.TextView

import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.quicksolveplus.dialogs.QSAlert
import com.quicksolveplus.qspmobile.R
import com.quicksolveplus.qspmobile.databinding.DialogShiftSubCodesDetailsBinding
import com.quicksolveplus.qspmobile.schedule.models.ShiftSubCodesItem
import com.quicksolveplus.utils.DecimalDigitsInputFilter
import com.quicksolveplus.utils.Validation

/**
 * 06/04/23.
 *
 * @author hardkgosai.
 */
class ShiftSubCodesDetails(
    private val activity: Activity,
    private val title: String,
    private val isEditable: Boolean,
    private val shiftSubCodesItem: ShiftSubCodesItem,
    private val onDeleteClick: (dialog: ShiftSubCodesDetails) -> Unit,
    private val onSubCodesClick: (subCodeTextView: TextView) -> Unit,
    private val onAddClick: (subCode: String, time: String, notes: String) -> Unit,
    private val onCancelClick: () -> Unit
) : Dialog(activity) {

    private lateinit var binding: DialogShiftSubCodesDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.dialog_insets))

        binding = DialogShiftSubCodesDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
        setCancelable(false)
    }

    private fun initUI() {
        binding.ivDelete.isVisible = isEditable
        binding.tvTitle.text = title
        binding.etTime.filters = arrayOf(DecimalDigitsInputFilter(2))

        if (isEditable) {
            binding.tvSubCodes.text = shiftSubCodesItem.subCode
            binding.etTime.setText(shiftSubCodesItem.subCodeTime)
            binding.etServiceNotes.setText(shiftSubCodesItem.subCodeNotes)
            binding.tvAdd.text = context.getString(R.string.str_update)
        }

        binding.tvAdd.setOnClickListener {
            if (isValidShiftSubCodesFields()) {
                onAddClick(
                    binding.tvSubCodes.text.toString(),
                    binding.etTime.text.toString(),
                    binding.etServiceNotes.text.toString()
                )
                dismiss()
            }
        }
        binding.tvCancel.setOnClickListener {
            onCancelClick()
            dismiss()
        }
        binding.ivDelete.setOnClickListener { onDeleteClick(this) }
        binding.clSubCodes.setOnClickListener { onSubCodesClick(binding.tvSubCodes) }
    }

    private fun isValidShiftSubCodesFields(): Boolean {
        Validation(activity).run {
            if (!isValueValid(binding.tvSubCodes.text.toString())) {
                showQSAlert(activity.getString(R.string.str_select_objectives_err))
                return false
            } else if (!isValueValid(binding.etTime.text.toString())) {
                showQSAlert(activity.getString(R.string.str_enter_time_err))
                return false
            } else if (!isValueValid(binding.etServiceNotes.text.toString())) {
                showQSAlert(activity.getString(R.string.str_enter_service_notes))
                return false
            } else if (binding.etTime.text.toString().startsWith('.')) {
                showQSAlert(activity.getString(R.string.str_time_objective_error))
                return false
            }
        }
        return true
    }

    private fun showQSAlert(message: String) {
        QSAlert(
            context = activity,
            title = activity.getString(R.string.str_error),
            message = message,
            positiveButtonText = activity.getString(R.string.str_ok)
        ).show()
    }
}