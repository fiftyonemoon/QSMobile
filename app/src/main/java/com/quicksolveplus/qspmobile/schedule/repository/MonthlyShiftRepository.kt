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
class MonthlyShiftRepository(application: Application) : QSRepo(application) {

    private val responseStatus = MutableLiveData<ResponseStatus>()

    fun getResponseStatus() = responseStatus

    suspend fun getStaffPreferenceForScheduleByMonth(map: JsonObject) {
        runApi(
            apiName = Api.getStaffPreferenceForScheduleByMonth, apiCall = {
                Client.api().getStaffPreferenceForScheduleByMonth(map)
            }, responseStatus = responseStatus
        )
    }

    suspend fun getStaffTORForScheduleByMonth(map: JsonObject) {
        runApi(
            apiName = Api.getStaffTORForScheduleByMonth, apiCall = {
                Client.api().getStaffTORForScheduleByMonth(map)
            }, responseStatus = responseStatus
        )
    }

    suspend fun getSchedulesByMonth(map: JsonObject) {
        runApi(
            apiName = Api.getSchedulesByMonth, apiCall = {
                Client.api().getSchedulesByMonth(map)
            }, responseStatus = responseStatus
        )
    }

    suspend fun getSchedules(map: JsonObject) {
        runApi(
            apiName = Api.getSchedules, apiCall = {
                Client.api().getSchedules(map)
            }, responseStatus = responseStatus
        )
    }

    suspend fun getStaffPreferenceForSchedule(map: JsonObject) {
        runApi(
            apiName = Api.getStaffPreferenceForSchedule, apiCall = {
                Client.api().getStaffPreferenceForSchedule(map)
            }, responseStatus = responseStatus
        )
    }

    suspend fun getStaffTORForSchedule(map: JsonObject) {
        runApi(
            apiName = Api.getStaffTORForSchedule, apiCall = {
                Client.api().getStaffTORForSchedule(map)
            }, responseStatus = responseStatus
        )
    }

    suspend fun getClientProfilePic(
        map: JsonObject,
        filename: String,
        responseStatus: MutableLiveData<ResponseStatus>
    ) {
        runApi(
            apiName = Api.getClientProfilePic, apiCall = {
                Client.api().getClientProfilePic(map)
            }, responseStatus = responseStatus, other = filename
        )
    }
}