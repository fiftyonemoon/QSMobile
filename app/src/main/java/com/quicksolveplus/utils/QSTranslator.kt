package com.quicksolveplus.utils

import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import com.quicksolveplus.settings.language.models.LanguageModel

/**
 * 28/03/23.
 *
 * @author hardkgosai.
 */
object QSTranslator {

    fun translate(value: String, onComplete: (result: String, isSuccess: Boolean) -> Unit) {
        val languageCode = Preferences.instance?.appLanguage ?: "en"
        val options = TranslatorOptions.Builder().setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(languageCode).build()
        val translator = Translation.getClient(options)
        translator.translate(value)
            .addOnCompleteListener { result -> onComplete(result.result, result.isSuccessful) }
    }

    fun downloadLanguage(languageModel: LanguageModel, onComplete: (isSuccess: Boolean) -> Unit) {
        val options = TranslatorOptions.Builder().setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(languageModel.languageCode).build()
        val translator = Translation.getClient(options)
        val conditions = DownloadConditions.Builder().build()
        translator.downloadModelIfNeeded(conditions)
            .addOnCompleteListener { result -> onComplete(result.isSuccessful) }
    }
}