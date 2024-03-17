package com.quicksolveplus.qspmobile.schedule.models

import com.google.gson.annotations.SerializedName

data class TORScheduleItem(
    @SerializedName("ApprovedBy") val approvedBy: Any,
    @SerializedName("ApprovedDateTime") val approvedDateTime: Any,
    @SerializedName("AssignPSTRates") val assignPSTRates: Boolean,
    @SerializedName("Comments") val comments: Any,
    @SerializedName("Create_DateTime") val createDateTime: Any,
    @SerializedName("Create_UserUID") val createUserUID: Any,
    @SerializedName("dDay") val dDay: Any,
    @SerializedName("dMonth") val dMonth: Any,
    @SerializedName("dYear") val dYear: Any,
    @SerializedName("DeniedBy") val deniedBy: Any,
    @SerializedName("DeniedDateTime") val deniedDateTime: Any,
    @SerializedName("EndTime") val endTime: String,
    @SerializedName("EndTimeActual") val endTimeActual: String,
    @SerializedName("Hours") val hours: Double,
    @SerializedName("Modify_DateTime") val modifyDateTime: Any,
    @SerializedName("Modify_UserUID") val modifyUserUID: Any,
    @SerializedName("PaidTimeOff") val paidTimeOff: Boolean,
    @SerializedName("PremiumHours") val premiumHours: Any,
    @SerializedName("PremiumRateOfPay") val premiumRateOfPay: Any,
    @SerializedName("RecordType") val recordType: Any,
    @SerializedName("SchedDate") val schedDate: String,
    @SerializedName("SchedDateEnd") val schedDateEnd: String,
    @SerializedName("ShiftTopValue") val shiftTopValue: Double,
    @SerializedName("StandardHours") val standardHours: Any,
    @SerializedName("StandardRateOfPay") val standardRateOfPay: Any,
    @SerializedName("StartTime") val startTime: String,
    @SerializedName("StartTimeActual") val startTimeActual: String,
    @SerializedName("Status") val status: String,
    @SerializedName("TORDetail") val tORDetail: String,
    @SerializedName("UID") val uID: Int,
    @SerializedName("UsersUID") val usersUID: Int,
    @SerializedName("WorkPref1") val workPref1: Any,
    @SerializedName("WorkPrefType") val workPrefType: String,
    var topMargin: Double?,
    var buttonHeight: Int?,
    var buttonBackground: String?,
    var buttonForeground: String?,
    var title: String?,
    var tempStartTime: String?,
    var tempEndTime: String?,
    var tempSchedDate: String?,
    var tempSchedDateEnd: String?,
) : java.io.Serializable, Cloneable {

    @Throws(CloneNotSupportedException::class)
    public override fun clone(): Any {
        return super.clone()
    }
}