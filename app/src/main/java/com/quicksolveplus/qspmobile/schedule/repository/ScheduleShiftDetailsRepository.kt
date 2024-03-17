package com.quicksolveplus.qspmobile.schedule.repository

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.quicksolveplus.api.Api
import com.quicksolveplus.api.ResponseStatus
import com.quicksolveplus.api.Client
import com.quicksolveplus.api.QSRepo

/**
 * 17/04/23.
 *
 * @author hardkgosai.
 */
class ScheduleShiftDetailsRepository(application: Application) : QSRepo(application) {

    private val responseStatus = MutableLiveData<ResponseStatus>()

    fun getResponseStatus() = responseStatus

    suspend fun getCustomClientServiceTypes(map: JsonObject) {
        runApi(
            apiName = Api.getCustomClientServiceTypes, apiCall = {
                Client.api().getCustomClientServiceTypes(map)
            }, responseStatus = responseStatus
        )
    }

    suspend fun getCustomEmployeeServiceTypes(map: JsonObject) {
        runApi(
            apiName = Api.getCustomEmployeeServiceTypes, apiCall = {
                Client.api().getCustomEmployeeServiceTypes(map)
            }, responseStatus = responseStatus
        )
    }

    suspend fun getOptionListWithUID(map: JsonObject) {
        runApi(
            apiName = Api.getOptionListWithUID, apiCall = {
                Client.api().getOptionListWithUID(map)
            }, responseStatus = responseStatus
        )
    }

    suspend fun getAttendanceTracking(map: JsonObject) {
        runApi(
            apiName = Api.getAttendanceTracking, apiCall = {
                Client.api().getAttendanceTracking(map)
            }, responseStatus = responseStatus
        )
    }

    suspend fun getClientRemainingPOS(map: JsonObject) {
        runApi(
            apiName = Api.getClientRemainingPOS, apiCall = {
                Client.api().getClientRemainingPOS(map)
            }, responseStatus = responseStatus
        )
    }

    suspend fun getClientsForShiftPopup(map: JsonObject) {
        runApi(
            apiName = Api.getClientsForShiftPopup, apiCall = {
                Client.api().getClientsForShiftPopup(map)
            }, responseStatus = responseStatus
        )
    }

    suspend fun getWorkersForShiftPopup(map: JsonObject) {
        runApi(
            apiName = Api.getWorkersForShiftPopup, apiCall = {
                Client.api().getWorkersForShiftPopup(map)
            }, responseStatus = responseStatus
        )
    }

    suspend fun getShiftSubCodes(map: JsonObject) {
        runApi(
            apiName = Api.getShiftSubCodes, apiCall = {
                Client.api().getShiftSubCodes(map)
            }, responseStatus = responseStatus
        )
    }

    suspend fun getShiftObjectives(map: JsonObject) {
        runApi(
            apiName = Api.getShiftObjectives, apiCall = {
                Client.api().getShiftObjectives(map)
            }, responseStatus = responseStatus
        )
    }

    suspend fun getISPObjectivesForSchedule(map: JsonObject) {
        runApi(
            apiName = Api.getISPObjectivesForSchedule, apiCall = {
                Client.api().getISPObjectivesForSchedule(map)
            }, responseStatus = responseStatus
        )
    }
}