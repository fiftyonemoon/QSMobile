package com.quicksolveplus.qspmobile.schedule.models

import com.google.gson.annotations.SerializedName

data class ISPObjectivesItem(
    @SerializedName("Category") val category: String,
    @SerializedName("ISPDetail") val iSPDetail: String,
    @SerializedName("ISPType") val iSPType: String,
    @SerializedName("Obj1HrsWeek") val obj1HrsWeek: String,
    @SerializedName("Objective") val objective: String?,
    @SerializedName("Objective10") val objective10: String?,
    @SerializedName("Objective11") val objective11: String?,
    @SerializedName("Objective12") val objective12: String?,
    @SerializedName("Objective13") val objective13: String?,
    @SerializedName("Objective2") val objective2: String?,
    @SerializedName("Objective3") val objective3: String?,
    @SerializedName("Objective4") val objective4: String?,
    @SerializedName("Objective5") val objective5: String?,
    @SerializedName("Objective6") val objective6: String?,
    @SerializedName("Objective7") val objective7: String?,
    @SerializedName("Objective8") val objective8: String?,
    @SerializedName("Objective9") val objective9: String?,
    @SerializedName("ServCompDate") val servCompDate: String,
    @SerializedName("ServCompDateEnd") val servCompDateEnd: String
) : java.io.Serializable