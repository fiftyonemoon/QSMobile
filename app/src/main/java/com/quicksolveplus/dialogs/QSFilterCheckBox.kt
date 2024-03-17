package com.quicksolveplus.dialogs

import android.app.Dialog
import android.content.Context

import android.os.Bundle
import android.view.Window

import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.quicksolveplus.qspmobile.R
import com.quicksolveplus.qspmobile.databinding.DialogQsFilterBinding

/**
 * 17/03/23.
 * Filter Dialog.
 *
 * @author hardkgosai.
 */
class QSFilterCheckBox(
    context: Context,
    private val isChecked: Boolean,
    private val onOkPressed: (isChecked: Boolean) -> Unit
) : Dialog(context) {

    private lateinit var binding: DialogQsFilterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.dialog_insets))

        binding = DialogQsFilterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
    }

    private fun initUI() {
        binding.cb.isVisible = true
        binding.cb.isChecked = isChecked
        binding.tvOk.setOnClickListener {
            onOkPressed(binding.cb.isChecked)
            dismiss()
        }
    }
}