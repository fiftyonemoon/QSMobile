package com.quicksolveplus.qspmobile.schedule.models

import com.google.gson.annotations.SerializedName

/**
 * 06/04/23.
 *
 * @author hardkgosai.
 */
data class LinkedSchedulesItem(
    @SerializedName("LinkedScheduleID") var linkedScheduleID: Int,
    @SerializedName("LinkedClientID") val linkedClientID: String? = null,
    @SerializedName("LinkedClientFirstName") val linkedClientFirstName: String? = null,
    @SerializedName("LinkedClientLastName") val linkedClientLastName: String? = null,
    @SerializedName("LinkedTaskName") val linkedTaskName: String? = null,
    @SerializedName("LinkedClientNickName") val linkedClientNickName: String? = null,
    @SerializedName("LinkedClientProfilePic") val linkedClientProfilePic: String? = null,
    val isNew: Boolean = false
) : java.io.Serializable