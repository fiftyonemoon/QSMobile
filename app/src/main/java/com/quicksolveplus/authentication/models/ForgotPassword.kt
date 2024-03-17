package com.quicksolveplus.authentication.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ForgotPassword(
    @SerializedName("Message")
    val message: String,
    @SerializedName("StatusCode")
    val statusCode: Int
) : Serializable