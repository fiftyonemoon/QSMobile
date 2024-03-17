package com.quicksolveplus.qspmobile.clients.repository

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.quicksolveplus.api.*
import com.quicksolveplus.qspmobile.clients.models.ClientsItem
import org.json.JSONObject

class ClientsRepository(application: Application) : QSRepo(application) {

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

    suspend fun updateClient(map: JsonObject) {
        runApi(
            apiName = Api.updateClient, apiCall = {
                Client.api().updateClient(map)
            }, responseStatus = responseStatus
        )
    }

    suspend fun getEmergencyContact(map: JsonObject) {
        runApi(
            apiName = Api.getEmergencyContact, apiCall = {
                Client.api().getEmergencyContact(map)
            }, responseStatus = responseStatus
        )
    }

    suspend fun addNewEmergencyContact(map: JsonObject) {
        runApi(
            apiName = Api.addNewEmergencyContact, apiCall = {
                Client.api().addNewEmergencyContact(map)
            }, responseStatus = responseStatus
        )
    }

    suspend fun updateEmergencyContact(map: JsonObject) {
        runApi(
            apiName = Api.updateEmergencyContact, apiCall = {
                Client.api().updateEmergencyContact(map)
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
}