package com.quicksolveplus.dashboard.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import androidx.core.content.ContextCompat
import com.quicksolveplus.qspmobile.R
import com.quicksolveplus.qspmobile.databinding.DialogCovidVaccineDeclarationBinding
import com.quicksolveplus.utils.Preferences
import com.quicksolveplus.utils.QSTranslator
import com.quicksolveplus.utils.dismissQSProgress
import com.quicksolveplus.utils.showQSProgress

/**
 * 28/03/23.
 *
 * @author hardkgosai.
 */
class CovidVaccineDeclarationForm(
    context: Context, private val onClicked: (isContinue: Boolean) -> Unit
) : Dialog(context) {

    private lateinit var binding: DialogCovidVaccineDeclarationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawable(
            ContextCompat.getDrawable(
                context, R.drawable.dialog_blue_insets
            )
        )

        binding = DialogCovidVaccineDeclarationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        translateMessage()
        initUI()
    }

    private fun translateMessage() {
        ownerActivity?.let {
            showQSProgress(it)
        }
        Preferences.instance?.user?.run {
            QSTranslator.translate(covidComplianceFormDetails.introductionPage) { result, isSuccess ->
                if (isSuccess) {
                    binding.tvMessage.text = result
                }
                dismissQSProgress()
            }
        }
    }

    private fun initUI() {
        binding.tvContinue.setOnClickListener {
            dismiss()
            onClicked(true)
        }
        binding.tvRemindLater.setOnClickListener {
            dismiss()
            onClicked(false)
        }
    }
}