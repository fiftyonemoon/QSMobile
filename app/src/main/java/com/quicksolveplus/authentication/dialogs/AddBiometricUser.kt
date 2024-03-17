package com.quicksolveplus.authentication.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import androidx.core.content.ContextCompat
import com.quicksolveplus.qspmobile.R
import com.quicksolveplus.qspmobile.databinding.DialogAddBiometricUserBinding
import com.quicksolveplus.utils.Validation

/**
 * 21/03/23.
 *
 * @author hardkgosai.
 */
class AddBiometricUser(
    context: Context, private val onOkClicked: (username: String, password: String) -> Unit
) : Dialog(context) {

    private lateinit var binding: DialogAddBiometricUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.dialog_insets))

        binding = DialogAddBiometricUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
    }

    private fun initUI() {
        binding.tvOk.setOnClickListener { validateCredentials() }
        binding.tvCancel.setOnClickListener { dismiss() }
    }

    private fun validateCredentials() {
        val username = binding.etUsername.text.toString()
        val password = binding.etPassword.text.toString()

        Validation(context, username = username, password = password).run {
            if (isUsernameValid() && isPasswordValid(password)) {
                dismiss()
                onOkClicked(username, password)
            }
        }
    }
}