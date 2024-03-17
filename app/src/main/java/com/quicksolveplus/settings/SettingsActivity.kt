package com.quicksolveplus.settings

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.quicksolveplus.dialogs.QSAlert
import com.quicksolveplus.modifiers.Actifiers.onActivityBackPressed
import com.quicksolveplus.modifiers.Actifiers.registerActivityResultLauncher
import com.quicksolveplus.qsbase.Base
import com.quicksolveplus.qspmobile.R
import com.quicksolveplus.qspmobile.databinding.ActivitySettingsBinding
import com.quicksolveplus.settings.color_blind.ColorBlindnessActivity
import com.quicksolveplus.settings.language.SelectLanguageActivity

class SettingsActivity : Base() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
        initBackPressed()
    }

    private fun initBackPressed() {
        onActivityBackPressed {
            if (intent.hasExtra("languageChanged")) {
                setResult(RESULT_OK)
            }
            finish()
        }
    }

    private fun initUI() {
        binding.apply {
            toolBar.tvTitle.text = getString(R.string.str_setting)
            toolBar.ivBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
            clLanguage.setOnClickListener(onClickListener)
            clAdjustColorBlind.setOnClickListener(onClickListener)
            clTouchFaceId.setOnClickListener(onClickListener)
        }
    }

    private var onClickListener = View.OnClickListener {
        when (it.id) {
            R.id.ivBack -> {
                onBackPressedDispatcher.onBackPressed()
            }
            R.id.clLanguage -> {
                navigateToLanguages()
            }
            R.id.clTouchFaceId -> {
                showRemoveBiometricDialog()
//                openSimpleDialog()
            }
            R.id.clAdjustColorBlind -> {
                navigateToColorBlindness()
            }
        }
    }

    private fun navigateToLanguages() {
        val intent = Intent(this@SettingsActivity, SelectLanguageActivity::class.java)
        languageResultLauncher.launch(intent)
    }

    private fun navigateToColorBlindness() {
        val intent = Intent(this@SettingsActivity, ColorBlindnessActivity::class.java)
        startActivity(intent)
    }

    private fun showBiometricNotConfiguredDialog() {
        QSAlert(
            this,
            message = getString(R.string.str_touch_id_not_configured),
            positiveButtonText = getString(R.string.str_ok)
        ) { isPositive ->

        }.show()
    }

    private fun showRemoveBiometricDialog() {
        QSAlert(
            this,
            message = getString(R.string.str_msg_remove_user_from_touch_id),
            positiveButtonText = getString(R.string.action_yes),
            negativeButtonText = getString(R.string.action_no)
        ) { isPositive ->

        }.show()
    }

    private val languageResultLauncher = registerActivityResultLauncher {
        if (it.resultCode == RESULT_OK) {
            intent.putExtra("languageChanged", true)
            recreate()
        }
    }
}