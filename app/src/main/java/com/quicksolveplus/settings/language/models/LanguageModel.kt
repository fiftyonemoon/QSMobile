package com.quicksolveplus.settings.language.models

import com.quicksolveplus.utils.Preferences

class LanguageModel(
    var languageCode: String,
    var languageTitle: String,
    var isSelected: Boolean = Preferences.instance?.appLanguage == languageCode
)