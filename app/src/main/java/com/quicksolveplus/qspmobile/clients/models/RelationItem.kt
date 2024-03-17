package com.quicksolveplus.qspmobile.clients.models

import com.google.gson.annotations.SerializedName

data class RelationItem(
    @SerializedName("Custom") val Custom: Boolean,
    @SerializedName("Disabled") val Disabled: Boolean,
    @SerializedName("Group") val Group: Any,
    @SerializedName("Selected") val Selected: Boolean,
    @SerializedName("Text") val Text: String,
    @SerializedName("Value") val Value: String
) : java.io.Serializable