package com.quicksolveplus.authentication.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class MedicalFormItem(
    @SerializedName("AnswerOption") val answerOption: String,
    @SerializedName("CovidFormItemId") val covidFormItemId: Int,
    @SerializedName("ItemQuestion") val itemQuestion: String
) : Serializable