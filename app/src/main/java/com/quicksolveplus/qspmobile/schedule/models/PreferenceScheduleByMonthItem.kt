package com.quicksolveplus.qspmobile.schedule.models

import com.google.gson.annotations.SerializedName

data class PreferenceScheduleByMonthItem(
    @SerializedName("PreferenceCount")
    val preferenceCount: Int,
    @SerializedName("PreferenceDate")
    val preferenceDate: String
) : java.io.Serializable