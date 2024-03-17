package com.quicksolveplus.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.quicksolveplus.qspmobile.R
import com.quicksolveplus.qspmobile.databinding.DialogAlertBinding

/**
 * 22/03/23.
 * Pop the Alert from anywhere.
 *
 * @author hardkgosai.
 */
class QSAlert(
    context: Context,
    private val title: String? = null,
    private val message: String? = null,
    private val positiveButtonText: String? = null,
    private val negativeButtonText: String? = null,
    private val cancelable: Boolean = true,
    private val onButtonClicked: (isPositive: Boolean) -> Unit = {}
) : Dialog(context) {

    private lateinit var binding: DialogAlertBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.dialog_insets))

        binding = DialogAlertBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setCancelable(cancelable)

        initUI()
        setText()
        setVisibility()
    }

    private fun initUI() {
        binding.tvYes.setOnClickListener {
            dismiss()
            onButtonClicked(true)
        }
        binding.tvNo.setOnClickListener {
            dismiss()
            onButtonClicked(false)
        }
    }

    private fun setText() {
        binding.tvTitle.text = title
        binding.tvMessage.text = message
        binding.tvYes.text = positiveButtonText
        binding.tvNo.text = negativeButtonText
    }

    private fun setVisibility() {
        binding.tvTitle.isVisible = title != null
        binding.tvMessage.isVisible = message != null
        binding.tvYes.isVisible = positiveButtonText != null
        binding.tvNo.isVisible = negativeButtonText != null
        binding.verticalDivider.isVisible = binding.tvNo.isVisible
        binding.horizontalDivider1.isVisible = binding.tvTitle.isVisible
    }
}