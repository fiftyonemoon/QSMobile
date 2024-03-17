package com.quicksolveplus.qspmobile.schedule.models

import com.google.gson.annotations.SerializedName

data class AttendanceTracking(
    @SerializedName("AddAsPst")
    val addAsPst: Boolean,
    @SerializedName("AssignPstRate")
    val assignPstRate: Boolean,
    @SerializedName("AttendanceClientId")
    val attendanceClientId: Int,
    @SerializedName("AttendanceClientLinkId")
    val attendanceClientLinkId: Any,
    @SerializedName("AttendanceEmployeeId")
    val attendanceEmployeeId: Int,
    @SerializedName("AttendanceEndTime")
    val attendanceEndTime: String,
    @SerializedName("AttendanceNotes")
    val attendanceNotes: String,
    @SerializedName("AttendanceReason")
    val attendanceReason: String,
    @SerializedName("AttendanceScheduleDate")
    val attendanceScheduleDate: String,
    @SerializedName("AttendanceScheduleDateEnd")
    val attendanceScheduleDateEnd: String,
    @SerializedName("AttendanceScheduleHours")
    val attendanceScheduleHours: Double,
    @SerializedName("AttendanceScheduleId")
    val attendanceScheduleId: Int,
    @SerializedName("AttendanceServiceType")
    val attendanceServiceType: String,
    @SerializedName("AttendanceStartTime")
    val attendanceStartTime: String,
    @SerializedName("Id")
    val id: Int,
    @SerializedName("PstRemaining")
    val pstRemaining: Double,
    @SerializedName("UserId")
    val userId: Int
) : java.io.Serializable