package com.quicksolveplus.qspmobile.clients.contacts.repository

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.quicksolveplus.api.Api
import com.quicksolveplus.api.Client
import com.quicksolveplus.api.QSRepo
import com.quicksolveplus.api.ResponseStatus

class ContactsRepository(application: Application) : QSRepo(application) {
    private val responseStatus = MutableLiveData<ResponseStatus>()

    fun getResponseStatus() = responseStatus

    suspend fun getClientContacts(map: JsonObject) {
        runApi(
            apiName = Api.getClientContacts,
            apiCall = {
                Client.api().getClientContacts(map)
            },
            responseStatus = responseStatus

        )
    }

    suspend fun getOptionListWithUID(map: JsonObject) {
        runApi(
            apiName = Api.getOptionListWithUID, apiCall = {
                Client.api().getOptionListWithUID(map)
            }, responseStatus = responseStatus
        )
    }

    suspend fun updateClientContact(map: JsonObject) {
        runApi(
            apiName = Api.updateClientContact, apiCall = {
                Client.api().updateClientContact(map)
            }, responseStatus = responseStatus
        )
    }

    suspend fun addNewClientContact(map: JsonObject) {
        runApi(
            apiName = Api.addNewClientContact, apiCall = {
                Client.api().addNewClientContact(map)
            }, responseStatus = responseStatus
        )
    }
}