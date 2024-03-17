package com.quicksolveplus.qspmobile.schedule.models

import com.google.gson.annotations.SerializedName

data class ScheduleByMonthItem(
    @SerializedName("DsnStatus") val dsnStatus: Any,
    @SerializedName("IsDsnCompleteForDay") val isDsnCompleteForDay: Boolean,
    @SerializedName("IsServiceRecordCompleteForDay") val isServiceRecordCompleteForDay: Boolean,
    @SerializedName("ScheduleCount") val scheduleCount: Int,
    @SerializedName("ScheduleDate") val scheduleDate: String,
    @SerializedName("ServiceRecordStatus") val serviceRecordStatus: Int
) : java.io.Serializable