package com.quicksolveplus.api

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class QSResponse<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("status_code") val statusCode: Int,
    @SerializedName("data") val data: T,
) : Serializable