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
class ScheduleRepository(application: Application) : QSRepo(application) {

    private val responseStatus = MutableLiveData<ResponseStatus>()

    fun getResponseStatus() = responseStatus

    suspend fun getDashboard(map: JsonObject) {
        runApi(
            apiName = Api.getDashboard, apiCall = {
                Client.api().getDashboard(map)
            }, responseStatus = responseStatus
        )
    }
}