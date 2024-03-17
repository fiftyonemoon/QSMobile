package com.quicksolveplus.authentication.repository

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.quicksolveplus.api.Api
import com.quicksolveplus.api.ResponseStatus
import com.quicksolveplus.api.Client
import com.quicksolveplus.api.QSRepo

/**
 * 17/03/23.
 *
 * @author hardkgosai.
 */
class LoginRepository(application: Application) : QSRepo(application) {

    private val responseStatus = MutableLiveData<ResponseStatus>()

    fun getResponseStatus() = responseStatus

    suspend fun validateUserLogon(map: JsonObject) {
        runApi(
            apiName = Api.validateUserLogon, apiCall = {
                Client.api().validateUserLogon(map)
            }, responseStatus = responseStatus
        )
    }

    suspend fun forgotPassword(map: JsonObject) {
        runApi(
            apiName = Api.forgotPassword, apiCall = {
                Client.api().forgotPassword(map)
            }, responseStatus = responseStatus
        )
    }

    suspend fun getQSMobileWebClients() {
        runApi(
            apiName = Api.getQSMobileWebClients, apiCall = {
                Client.api().getQSMobileWebClients()
            }, responseStatus = responseStatus
        )
    }
}