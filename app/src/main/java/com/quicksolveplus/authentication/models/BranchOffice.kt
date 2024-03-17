package com.quicksolveplus.authentication.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * 16/03/23.
 *
 * @author hardkgosai.
 */
data class BranchOffice(
    @SerializedName("ClientUID") val clientUID: Int,
    @SerializedName("ProviderOfficeUID") val providerOfficeUID: Int,
    @SerializedName("UID") val uID: Int,
    @SerializedName("UsersEmployeeUID") val usersEmployeeUID: Int
) : Serializable