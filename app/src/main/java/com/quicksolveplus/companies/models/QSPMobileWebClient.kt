package com.quicksolveplus.companies.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * 16/03/23.
 *
 * @author hardkgosai.
 */
data class QSPMobileWebClient(
    @SerializedName("CompanyAbbreviation") val companyAbbreviation: String,
    @SerializedName("CompanyId") val companyId: Int,
    @SerializedName("CompanyName") val companyName: String,
    @SerializedName("CompanyURL") val companyURL: String
) : Serializable