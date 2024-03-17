package com.quicksolveplus.qspmobile.schedule.repository

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.quicksolveplus.api.Api
import com.quicksolveplus.api.ResponseStatus
import com.quicksolveplus.api.Client
import com.quicksolveplus.api.QSRepo

/**
 * 31/03/23.
 *
 * @author hardkgosai.
 */
class LinkedClientRepository(application: Application) : QSRepo(application) {

    private val responseStatus = MutableLiveData<ResponseStatus>()

    fun getResponseStatus() = responseStatus

    suspend fun getClientProfilePic(map: JsonObject, filename: String) {
        runApi(
            apiName = Api.getClientProfilePic, apiCall = {
                Client.api().getClientProfilePic(map)
            }, responseStatus = responseStatus, other = filename
        )
    }
}