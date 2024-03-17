package com.quicksolveplus.qspmobile.clients.models

import com.google.gson.annotations.SerializedName

data class ClientServiceType(
    @SerializedName("POSExpDate")
    val POSExpDate: String,
    @SerializedName("POSHours")
    val POSHours: Double,
    @SerializedName("ServiceType")
    val ServiceType: String
)