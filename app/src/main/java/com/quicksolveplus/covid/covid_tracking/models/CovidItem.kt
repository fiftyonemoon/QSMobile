package com.quicksolveplus.covid.covid_tracking.models

data class CovidItem(
    val ComplianceId: Int,
    val ComplianceType: String,
    val CreatedDate: String,
    val Dose1Date: String,
    val Dose2Date: String,
    val EmployeeId: Int,
    val EmployeeSignature: String,
    val EmployeeSignatureDate: String,
    val ExemptionType: String,
    val IsWorkingRemotely: Boolean,
    val MedicalFormItems: Any,
    val ReligiousFormItems: Any,
    val SupportingImageNames: ArrayList<String>
)