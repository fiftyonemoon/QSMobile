package com.quicksolveplus.qspmobile.schedule.repository

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.quicksolveplus.api.Api
import com.quicksolveplus.api.ResponseStatus
import com.quicksolveplus.api.Client
import com.quicksolveplus.api.QSRepo
import com.quicksolveplus.qspmobile.employee.model.EmployeesItem

/**
 * 11/04/23.
 *
 * @author hardkgosai.
 */
class TrainedEmployeesRepository(application: Application) : QSRepo(application) {

    private val responseStatus = MutableLiveData<ResponseStatus>()

    fun getResponseStatus() = responseStatus

    suspend fun getClientTrainedEmployees(map: JsonObject) {
        runApi(
            apiName = Api.getClientTrainedEmployees, apiCall = {
                Client.api().getClientTrainedEmployees(map)
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