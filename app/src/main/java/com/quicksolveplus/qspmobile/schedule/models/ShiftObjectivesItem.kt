package com.quicksolveplus.qspmobile.schedule.models

import com.google.gson.annotations.SerializedName

data class ShiftObjectivesItem(
    @SerializedName("ClientUID") val clientUID: Int,
    @SerializedName("dDay") val dDay: Int,
    @SerializedName("dMonth") val dMonth: Int,
    @SerializedName("dYear") val dYear: Int,
    @SerializedName("Level1") val level1: Int,
    @SerializedName("Level2") val level2: Int,
    @SerializedName("Objective") val objective: String,
    @SerializedName("ObjectiveDetail") val objectiveDetail: String,
    @SerializedName("ObjectiveTime") val objectiveTime: String,
    @SerializedName("SchedDate") val schedDate: Any,
    @SerializedName("ScheduleUID") val scheduleUID: Int,
    @SerializedName("ServiceNotes") val serviceNotes: String,
    @SerializedName("StaffUID") val staffUID: Int,
    @SerializedName("UID") val uID: Int
) : java.io.Serializable