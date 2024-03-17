package com.quicksolveplus.companies.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * 16/03/23.
 *
 * @author hardkgosai.
 */
data class Companies(
    @SerializedName("QSPMobileWebClientList") val qSPMobileWebClientList: ArrayList<QSPMobileWebClient>
) : Serializable