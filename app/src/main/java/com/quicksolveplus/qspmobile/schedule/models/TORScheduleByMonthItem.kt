package com.quicksolveplus.qspmobile.schedule.models

import com.google.gson.annotations.SerializedName

data class TORScheduleByMonthItem(
    @SerializedName("TORCount")
    val tORCount: Int,
    @SerializedName("TORDate")
    val tORDate: String
) : java.io.Serializable