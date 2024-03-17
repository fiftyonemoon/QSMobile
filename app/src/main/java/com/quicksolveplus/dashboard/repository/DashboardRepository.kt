package com.quicksolveplus.dashboard.repository

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.quicksolveplus.api.Api
import com.quicksolveplus.api.ResponseStatus
import com.quicksolveplus.api.Client
import com.quicksolveplus.api.QSRepo

/**
 * 27/03/23.
 *
 * @author hardkgosai.
 */
class DashboardRepository(application: Application) : QSRepo(application) {

    private val responseStatus = MutableLiveData<ResponseStatus>()

    fun getResponseStatus() = responseStatus

    suspend fun getClientProfilePic(map: JsonObject) {
        runApi(
            apiName = Api.getClientProfilePic, apiCall = {
                Client.api().getClientProfilePic(map)
            }, responseStatus = responseStatus
        )
    }

    suspend fun getWorker(map: JsonObject) {
        runApi(
            apiName = Api.getWorker, apiCall = {
                Client.api().getWorker(map)
            }, responseStatus = responseStatus
        )
    }

    suspend fun getEmployeeAnnouncements(map: JsonObject) {
        runApi(
            apiName = Api.getEmployeeAnnouncements, apiCall = {
                Client.api().getEmployeeAnnouncements(map)
            }, responseStatus = responseStatus
        )
    }

    suspend fun getCovidComplianceFormDetails(map: JsonObject) {
        runApi(
            apiName = Api.getCovidComplianceFormDetails, apiCall = {
                Client.api().getCovidComplianceFormDetails(map)
            }, responseStatus = responseStatus
        )
    }
}