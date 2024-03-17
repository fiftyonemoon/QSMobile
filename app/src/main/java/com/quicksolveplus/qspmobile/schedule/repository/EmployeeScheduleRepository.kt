package com.quicksolveplus.qspmobile.schedule.repository

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.quicksolveplus.api.Api
import com.quicksolveplus.api.ResponseStatus
import com.quicksolveplus.api.Client
import com.quicksolveplus.api.QSRepo
import com.quicksolveplus.qspmobile.clients.models.ClientsItem
import com.quicksolveplus.qspmobile.employee.model.EmployeesItem

/**
 * 31/03/23.
 *
 * @author hardkgosai.
 */
class EmployeeScheduleRepository(application: Application) : QSRepo(application) {

    private val responseStatus = MutableLiveData<ResponseStatus>()

    fun getResponseStatus() = responseStatus

    suspend fun getWorkers(map: JsonObject) {
        runApi(
            apiName = Api.getWorkers, apiCall = {
                Client.api().getWorkers(map)
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

    suspend fun getWorkersWithSameSupervisor(map: JsonObject) {
        runApi(
            apiName = Api.getWorkersWithSameSupervisor, apiCall = {
                Client.api().getWorkersWithSameSupervisor(map)
            }, responseStatus = responseStatus
        )
    }

    suspend fun getWorkersForEmployeeOffices(map: JsonObject) {
        runApi(
            apiName = Api.getWorkersForEmployeeOffices, apiCall = {
                Client.api().getWorkersForEmployeeOffices(map)
            }, responseStatus = responseStatus
        )
    }

    suspend fun getWorkerProfilePic(
        map: JsonObject,
        pair: Pair<Int, EmployeesItem?> = Pair(-1, null)
    ) {
        runApi(
            apiName = Api.getWorkerProfilePic, apiCall = {
                Client.api().getWorkerProfilePic(map)
            }, responseStatus = responseStatus, other = pair
        )
    }
}