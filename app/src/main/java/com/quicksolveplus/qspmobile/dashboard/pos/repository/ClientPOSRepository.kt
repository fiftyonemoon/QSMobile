package com.quicksolveplus.qspmobile.dashboard.pos.repository

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.quicksolveplus.api.*

class ClientPOSRepository(application: Application) : QSRepo(application) {

    private val responseStatus = MutableLiveData<ResponseStatus>()

    fun getResponseStatus() = responseStatus

    suspend fun getClientPOS(map: JsonObject) {
        runApi(
            apiName = Api.getClientPOS, apiCall = {
                Client.api().getClientPOS(map)
            }, responseStatus = responseStatus
        )
    }
}