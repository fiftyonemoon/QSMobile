package com.quicksolveplus.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import androidx.core.content.ContextCompat
import com.quicksolveplus.qspmobile.R
import com.quicksolveplus.qspmobile.databinding.DialogSuccessBinding

class QSSuccess(context: Context,
    private val message: String,
    private val onOkClick: (isOk: Boolean) -> Unit
) : Dialog(context) {

    private lateinit var binding: DialogSuccessBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.dialog_insets))

        binding = DialogSuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setCancelable(true)
        initUI()
    }

    private fun initUI() {
        binding.tvMessage.text = message
        binding.tvOk.setOnClickListener {
            dismiss()
            onOkClick(true)
        }
    }
}