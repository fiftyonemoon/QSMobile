package com.quicksolveplus.authentication.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import androidx.core.content.ContextCompat
import com.quicksolveplus.qspmobile.R
import com.quicksolveplus.qspmobile.databinding.DialogForgotPasswordBinding
import com.quicksolveplus.utils.Validation

/**
 * 21/03/23.
 *
 * @author hardkgosai.
 */
class RequestForgotPassword(
    context: Context, private val onOkClicked: (email: String) -> Unit
) : Dialog(context) {

    private lateinit var binding: DialogForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.dialog_insets))

        binding = DialogForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
    }

    private fun initUI() {
        binding.tvOk.setOnClickListener { validateCredentials() }
        binding.tvCancel.setOnClickListener { dismiss() }
    }

    private fun validateCredentials() {
        val email = binding.etEmail.text.toString()

        Validation(context, email = email).run {
            if (isEmailValid()) {
                dismiss()
                onOkClicked(email)
            }
        }
    }
}