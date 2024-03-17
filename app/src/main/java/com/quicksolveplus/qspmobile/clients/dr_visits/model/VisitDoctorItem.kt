package com.quicksolveplus.qspmobile.clients.dr_visits.model

data class VisitDoctorItem(
    val ClientUID: Int,
    val Doctor: String,
    val NewMedication: String,
    val NextVisitDate: String,
    val NextVisitEndTime: String,
    val NextVisitStartTime: String,
    val Outcome: String,
    val Reason: String,
    val UID: Int,
    val VisitDate: String,
    val VisitDetail: String,
    val VisitEndTime: String,
    val VisitStartTime: String
)