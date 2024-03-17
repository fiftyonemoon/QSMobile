package com.quicksolveplus.qspmobile.schedule.models

import com.google.gson.annotations.SerializedName

data class ShiftSubCodesItem(
    @SerializedName("ClientUID") val clientUID: Int,
    @SerializedName("dDay") val dDay: Int,
    @SerializedName("dMonth") val dMonth: Int,
    @SerializedName("dYear") val dYear: Int,
    @SerializedName("Level1") val level1: Int,
    @SerializedName("Level2") val level2: Int,
    @SerializedName("SchedDate") val schedDate: String,
    @SerializedName("ScheduleUID") val scheduleUID: Int,
    @SerializedName("StaffUID") val staffUID: Int,
    @SerializedName("SubCode") var subCode: String,
    @SerializedName("SubCodeDetail") var subCodeDetail: String,
    @SerializedName("SubCodeNotes") var subCodeNotes: String,
    @SerializedName("SubCodeTime") var subCodeTime: String,
    @SerializedName("UID") val uID: Int
) : java.io.Serializable