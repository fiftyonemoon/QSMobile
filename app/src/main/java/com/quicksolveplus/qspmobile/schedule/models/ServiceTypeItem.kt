package com.quicksolveplus.qspmobile.schedule.models

import com.google.gson.annotations.SerializedName

data class ServiceTypeItem(
    @SerializedName("Custom") val custom: Boolean,
    @SerializedName("Group") val group: Any,
    @SerializedName("IsActivated") val isActivated: Any,
    @SerializedName("IsBillable") val isBillable: Any,
    @SerializedName("IsEvvServiceType") val isEvvServiceType: Boolean,
    @SerializedName("IsPayable") val isPayable: Any,
    @SerializedName("IsSubCoded") val isSubCoded: Boolean,
    @SerializedName("Method") val method: Boolean,
    @SerializedName("Mileage") val mileage: Boolean,
    @SerializedName("MultiClient") val multiClient: Boolean,
    @SerializedName("MultiStaff") val multiStaff: Boolean,
    @SerializedName("OTExempt") val oTExempt: Boolean,
    @SerializedName("ObjectiveNotes") val objectiveNotes: Boolean,
    @SerializedName("RestPeriods") val restPeriods: Boolean,
    @SerializedName("ScheduleNotes") val scheduleNotes: Boolean,
    @SerializedName("Selected") val selected: Boolean,
    @SerializedName("ServiceBackTypeColor") val serviceBackTypeColor: String = "",
    @SerializedName("ServiceForeTypeColor") val serviceForeTypeColor: String = "",
    @SerializedName("ServiceNotes") val serviceNotes: Boolean,
    @SerializedName("SubCode") val subCode: Any,
    @SerializedName("Text") val text: String = "",
    @SerializedName("UID") val uID: Int,
    @SerializedName("Value") val value: String = ""
) : java.io.Serializable