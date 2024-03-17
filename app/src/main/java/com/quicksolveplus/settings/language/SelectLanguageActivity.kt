package com.quicksolveplus.settings.language

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.nl.translate.TranslateLanguage
import com.quicksolveplus.QSMobile
import com.quicksolveplus.modifiers.Actifiers.onActivityBackPressed
import com.quicksolveplus.qspmobile.R
import com.quicksolveplus.qspmobile.databinding.ActivitySelectLanguageBinding
import com.quicksolveplus.settings.language.adapter.LanguageAdapter
import com.quicksolveplus.settings.language.models.LanguageModel
import com.quicksolveplus.utils.*

class SelectLanguageActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySelectLanguageBinding
    private var languageAdapter: LanguageAdapter? = null
    private var languageModelList: ArrayList<LanguageModel> = arrayListOf()
    private val activeLanguage = Preferences.instance?.appLanguage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySelectLanguageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
        initBackPressed()
        prepareLanguages()
        setLanguageAdapter()
    }

    private fun initUI() {
        binding.toolBar.ivBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.toolBar.tvTitle.text = getString(R.string.str_application_language)
    }

    private fun initBackPressed() {
        onActivityBackPressed {
            val appliedLanguage = Preferences.instance?.appLanguage
            if (activeLanguage != appliedLanguage) {
                setResult(RESULT_OK)
            }
            finish()
        }
    }

    private fun prepareLanguages() {
        languageModelList.add(LanguageModel(TranslateLanguage.CHINESE, "Chinese"))
        languageModelList.add(LanguageModel(TranslateLanguage.ENGLISH, "English"))
        languageModelList.add(LanguageModel(TranslateLanguage.TAGALOG, "Filipino"))
        languageModelList.add(LanguageModel(TranslateLanguage.PERSIAN, "Farsi (Persian)"))
        languageModelList.add(LanguageModel(TranslateLanguage.SPANISH, "Spanish"))
        languageModelList.add(LanguageModel(TranslateLanguage.VIETNAMESE, "Vietnamese"))
    }

    private fun setLanguageAdapter() {
        languageAdapter = LanguageAdapter(languageModelList) { language ->
            downloadLanguage(language)
        }
        binding.rvLanguages.adapter = languageAdapter
    }

    private fun downloadLanguage(languageModel: LanguageModel) {
        showQSProgress(this)
        QSTranslator.downloadLanguage(languageModel) { isSuccess ->
            if (isSuccess) {
                Preferences.instance?.appLanguage = languageModel.languageCode
                (application as QSMobile).setupQSLanguage()
            }
            dismissQSProgress()
        }
    }
}