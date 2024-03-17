package com.quicksolveplus.qspmobile.clients.models

import com.google.gson.annotations.SerializedName

data class ApprovedLocation(
    @SerializedName("Address")
    val Address: String,
    @SerializedName("City")
    val City: String,
    @SerializedName("Latitude")
    val Latitude: String,
    @SerializedName("LocationName")
    val LocationName: String,
    @SerializedName("Longitude")
    val Longitude: String,
    @SerializedName("State")
    val State: String,
    @SerializedName("Zip")
    val Zip: String
)