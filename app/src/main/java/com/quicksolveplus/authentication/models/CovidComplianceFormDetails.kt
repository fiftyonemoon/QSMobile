package com.quicksolveplus.authentication.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CovidComplianceFormDetails(
    @SerializedName("CovidFormDetailId") val covidFormDetailId: Int,
    @SerializedName("HasCustomMedicalForm") val hasCustomMedicalForm: Boolean,
    @SerializedName("HasCustomReligiousForm") val hasCustomReligiousForm: Boolean,
    @SerializedName("hasSubmittedBooster") val hasSubmittedBooster: Boolean,
    @SerializedName("hasSubmittedExemption") val hasSubmittedExemption: Boolean,
    @SerializedName("hasSubmittedVaccination") val hasSubmittedVaccination: Boolean,
    @SerializedName("IntroductionPage") val introductionPage: String,
    @SerializedName("isWorkingRemotely") val isWorkingRemotely: Boolean,
    @SerializedName("MedicalFormItems") val medicalFormItems: List<MedicalFormItem>,
    @SerializedName("ReligiousFormItems") val religiousFormItems: List<ReligiousFormItem>,
    @SerializedName("RequireMedicalDocumentation") val requireMedicalDocumentation: Boolean,
    @SerializedName("RequireReligiousDocumentation") val requireReligiousDocumentation: Boolean,
    @SerializedName("TestingStartDate") val testingStartDate: String
) : Serializable