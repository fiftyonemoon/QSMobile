package com.quicksolveplus.api

/**
 * 17/03/23.
 *
 * @author hardkgosai.
 */
sealed class ResponseStatus {
    data class Running(val apiName: String = "") : ResponseStatus()
    data class Success(
        val apiName: String = "",
        val data: Any? = null,
        val other: Any? = null
    ) : ResponseStatus()

    data class Failed(
        val apiName: String = "",
        val msg: String = "Server is temporary down! Please try after sometime",
        val id: Int? = 0
    ) : ResponseStatus()
}