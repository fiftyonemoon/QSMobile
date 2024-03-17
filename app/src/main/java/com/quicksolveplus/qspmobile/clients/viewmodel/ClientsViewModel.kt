package com.quicksolveplus.qspmobile.clients.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.quicksolveplus.qspmobile.clients.models.ClientsItem
import com.quicksolveplus.qspmobile.clients.repository.ClientsRepository
import kotlinx.coroutines.launch

class ClientsViewModel(application: Application) : AndroidViewModel(application) {

    private var repository: ClientsRepository

    init {
        repository = ClientsRepository(application)
    }

    private val responseStatus = repository.getResponseStatus()

    fun responseStatus() = responseStatus

    fun getClientsData(body: JsonObject) {
        viewModelScope.launch {
            repository.getClientsData(body)
        }
    }

    fun getClientCaseloadOnly(body: JsonObject) {
        viewModelScope.launch {
            repository.getClientCaseloadOnly(body)
        }
    }

    fun getClientProfilePic(body: JsonObject, pair: Pair<Int, ClientsItem>) {
        viewModelScope.launch {
            repository.getClientProfilePic(body, pair)
        }
    }

    fun getClientsTrainedByEmployee(body: JsonObject) {
        viewModelScope.launch {
            repository.getClientsTrainedByEmployee(body)
        }
    }

    fun getClientsForEmployeeOffices(body: JsonObject) {
        viewModelScope.launch {
            repository.getClientsForEmployeeOffices(body)
        }
    }

    fun updateClient(body: JsonObject) {
        viewModelScope.launch {
            repository.updateClient(body)
        }
    }

    fun getEmergencyContact(body: JsonObject) {
        viewModelScope.launch {
            repository.getEmergencyContact(body)
        }
    }

    fun addNewEmergencyContact(body: JsonObject) {
        viewModelScope.launch {
            repository.addNewEmergencyContact(body)
        }
    }

    fun updateEmergencyContact(body: JsonObject) {
        viewModelScope.launch {
            repository.updateEmergencyContact(body)
        }
    }

    fun getOptionListWithUID(body: JsonObject) {
        viewModelScope.launch {
            repository.getOptionListWithUID(body)
        }
    }

}