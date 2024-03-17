package com.quicksolveplus.companies.repository

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.quicksolveplus.api.*

/**
 * 20/03/23.
 *
 * @author hardkgosai.
 */
class CompanyRepository(application: Application) : QSRepo(application) {

    private val responseStatus = MutableLiveData<ResponseStatus>()

    fun getResponseStatus() = responseStatus

    suspend fun getQSMobileWebClients() {
        runApi(
            apiName = Api.getQSMobileWebClients,
            apiCall = {
                Client.api().getQSMobileWebClients()
            }, responseStatus = responseStatus
        )
    }
}