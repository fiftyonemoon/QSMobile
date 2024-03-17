package com.quicksolveplus.qspmobile.dashboard.pos.models

import com.google.gson.annotations.SerializedName

data class ClientPOSItem(
    @SerializedName("HrsNotWorked")
    val hrsNotWorked: Double?,
    @SerializedName("HrsScheduled")
    val hrsScheduled: Double?,
    @SerializedName("HrsWorked")
    val hrsWorked: Double?,
    @SerializedName("POS")
    val pOS: Double?,
    @SerializedName("sHrsNotWorked")
    val sHrsNotWorked: Double?,
    @SerializedName("sHrsScheduled")
    val sHrsScheduled: Double?,
    @SerializedName("sHrsWorked")
    val sHrsWorked: Double?,
    @SerializedName("SchedTotal")
    val schedTotal: Double?,
    @SerializedName("TotalPercent")
    val totalPercent: Double?
) : java.io.Serializable