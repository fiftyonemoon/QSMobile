package com.quicksolveplus.qspmobile.schedule.repository

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.quicksolveplus.api.Api
import com.quicksolveplus.api.ResponseStatus
import com.quicksolveplus.api.Client
import com.quicksolveplus.api.QSRepo

/**
 * 12/04/23.
 *
 * @author hardkgosai.
 */
class WeeklyShiftRepository(application: Application) : QSRepo(application) {

    private val responseStatus = MutableLiveData<ResponseStatus>()

    fun getResponseStatus() = responseStatus

    suspend fun getStaffPreferenceForScheduleByWeek(map: JsonObject) {
        runApi(
            apiName = Api.getStaffPreferenceForScheduleByWeek, apiCall = {
                Client.api().getStaffPreferenceForScheduleByWeek(map)
            }, responseStatus = responseStatus
        )
    }

    suspend fun getStaffTORForScheduleByWeek(map: JsonObject) {
        runApi(
            apiName = Api.getStaffTORForScheduleByWeek, apiCall = {
                Client.api().getStaffTORForScheduleByWeek(map)
            }, responseStatus = responseStatus
        )
    }

    suspend fun getSchedulesByWeek(map: JsonObject) {
        runApi(
            apiName = Api.getSchedulesByWeek, apiCall = {
                Client.api().getSchedulesByWeek(map)
            }, responseStatus = responseStatus
        )
    }
}