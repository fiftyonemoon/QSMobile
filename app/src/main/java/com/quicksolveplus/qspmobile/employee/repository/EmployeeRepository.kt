package com.quicksolveplus.qspmobile.employee.repository

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.quicksolveplus.api.*
import com.quicksolveplus.qspmobile.employee.model.EmployeesItem

class EmployeeRepository(application: Application) : QSRepo(application) {
    private val responseStatus = MutableLiveData<ResponseStatus>()

    fun getResponseStatus() = responseStatus

    suspend fun getWorkers(map: JsonObject) {
        runApi(
            apiName = Api.getWorkers,
            apiCall = {
                Client.api().getWorkers(map)
            },
            responseStatus = responseStatus
        )
    }

    suspend fun getWorkersByMyCaseload(map: JsonObject) {
        runApi(
            apiName = Api.getWorkersByMyCaseload,
            apiCall = {
                Client.api().getWorkersByMyCaseload(map)
            },
            responseStatus = responseStatus
        )
    }

    suspend fun getWorker(map: JsonObject) {
        runApi(
            apiName = Api.getWorker,
            apiCall = {
                Client.api().getWorker(map)
            },
            responseStatus = responseStatus
        )
    }

    suspend fun getWorkersWithSameSupervisor(map: JsonObject) {
        runApi(
            apiName = Api.getWorkersWithSameSupervisor,
            apiCall = {
                Client.api().getWorkersWithSameSupervisor(map)
            },
            responseStatus = responseStatus
        )
    }

    suspend fun getWorkersForEmployeeOffices(map: JsonObject) {
        runApi(
            apiName = Api.getWorkersForEmployeeOffices,
            apiCall = {
                Client.api().getWorkersForEmployeeOffices(map)
            },
            responseStatus = responseStatus
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