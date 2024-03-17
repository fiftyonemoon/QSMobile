package com.quicksolveplus.qspmobile.schedule.repository

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.quicksolveplus.api.Api
import com.quicksolveplus.api.ResponseStatus
import com.quicksolveplus.api.Client
import com.quicksolveplus.api.QSRepo
import com.quicksolveplus.qspmobile.clients.models.ClientsItem

/**
 * 31/03/23.
 *
 * @author hardkgosai.
 */
class ClientScheduleRepository(application: Application) : QSRepo(application) {

    private val responseStatus = MutableLiveData<ResponseStatus>()

    fun getResponseStatus() = responseStatus

    suspend fun getClientsData(map: JsonObject) {
        runApi(
            apiName = Api.getClients, apiCall = {
                Client.api().getClients(map)
            }, responseStatus = responseStatus
        )
    }

    suspend fun getClientCaseloadOnly(map: JsonObject) {
        runApi(
            apiName = Api.getClientCaseloadOnly, apiCall = {
                Client.api().getClientCaseloadOnly(map)
            }, responseStatus = responseStatus
        )
    }

    suspend fun getClientsTrainedByEmployee(map: JsonObject) {
        runApi(
            apiName = Api.getClientsTrainedByEmployee, apiCall = {
                Client.api().getClientsTrainedByEmployee(map)
            }, responseStatus = responseStatus
        )
    }

    suspend fun getClientsForEmployeeOffices(map: JsonObject) {
        runApi(
            apiName = Api.getClientsForEmployeeOffices, apiCall = {
                Client.api().getClientsForEmployeeOffices(map)
            }, responseStatus = responseStatus
        )
    }

    suspend fun getClientProfilePic(
        map: JsonObject,
        pair: Pair<Int, ClientsItem?> = Pair(-1, null)
    ) {
        runApi(
            apiName = Api.getClientProfilePic, apiCall = {
                Client.api().getClientProfilePic(map)
            }, responseStatus = responseStatus, other = pair
        )
    }
}