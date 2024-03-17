package com.quicksolveplus.authentication.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UserPermission(
    @SerializedName("AppModuleId") val appModuleId: Int,
    @SerializedName("PermissionAction") val permissionAction: String,
    @SerializedName("PermissionModule") val permissionModule: String,
    @SerializedName("PermissionName") val permissionName: String
) : Serializable