package com.quicksolveplus.utils

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import androidx.appcompat.R
import com.quicksolveplus.languist.Language
import com.quicksolveplus.languist.Linguist
import com.quicksolveplus.languist.Options
import com.quicksolveplus.languist.UnsupportedLanguageException

/**
 * 28/03/23.
 *
 * @author hardkgosai.
 */
object QSLanguage {

    fun initialize(context: Context, languageCode: String? = "en"): Linguist {
        try {
            val code = if (languageCode == "zh") "zh-CN" else languageCode
            val linguistOptions =
                Options.Builder(context, Language.fromCode(code))
                    .setAutoTranslatedLanguages(*Language.values()).addStrings(R.string::class.java)
                    .excludeStrings(android.R.string::class.java).build()
            return Linguist.init(linguistOptions)
        } catch (e: UnsupportedLanguageException) {
            e.printStackTrace()
        }
        return Linguist.get(context)
    }
}