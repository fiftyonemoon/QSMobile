package com.quicksolveplus.qspmobile.schedule.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DashboardItem(
    @SerializedName("col1Name") val col1Name: Any,
    @SerializedName("Description") val description: String,
    @SerializedName("HoursAvailable") val hoursAvailable: String,
    @SerializedName("HoursNotWorked") val hoursNotWorked: String,
    @SerializedName("HoursRemaining") val hoursRemaining: String,
    @SerializedName("HoursScheduled") val hoursScheduled: String,
    @SerializedName("HoursWorked") val hoursWorked: String,
    @SerializedName("IHSSFirstHalf") val iHSSFirstHalf: String,
    @SerializedName("IHSSHoursNotWorked") val iHSSHoursNotWorked: String,
    @SerializedName("IHSSHoursRemaining") val iHSSHoursRemaining: String,
    @SerializedName("IHSSHoursScheduled") val iHSSHoursScheduled: String,
    @SerializedName("IHSSHoursWorked") val iHSSHoursWorked: String,
    @SerializedName("IHSSNOA") val iHSSNOA: String,
    @SerializedName("IHSSPercent1") val iHSSPercent1: Double,
    @SerializedName("IHSSPercent2") val iHSSPercent2: Double,
    @SerializedName("IHSSSecondHalf") val iHSSSecondHalf: String,
    @SerializedName("NoaPosCellBackcolor") val noaPosCellBackcolor: Any,
    @SerializedName("NoaPosCellForecolor") val noaPosCellForecolor: Any,
    @SerializedName("POSDescription") val pOSDescription: String,
    @SerializedName("POSFirstHalf") val pOSFirstHalf: Double,
    @SerializedName("POSHoursAvailable") val pOSHoursAvailable: Double,
    @SerializedName("POSHoursNotWorked") val pOSHoursNotWorked: Double,
    @SerializedName("POSHoursRemaining") val pOSHoursRemaining: Double,
    @SerializedName("POSHoursScheduled") val pOSHoursScheduled: Double,
    @SerializedName("POSHoursWorked") val pOSHoursWorked: Double,
    @SerializedName("POSPercent1") val pOSPercent1: Double,
    @SerializedName("POSPercent2") val pOSPercent2: Double,
    @SerializedName("POSSecondHalf") val pOSSecondHalf: Double,
    @SerializedName("TotalPercent1") val totalPercent1: String,
    @SerializedName("TotalPercent2") val totalPercent2: String
) : Serializable