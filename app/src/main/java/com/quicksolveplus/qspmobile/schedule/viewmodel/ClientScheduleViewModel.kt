package com.quicksolveplus.qspmobile.schedule.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.quicksolveplus.qspmobile.clients.models.ClientsItem
import com.quicksolveplus.qspmobile.schedule.repository.ClientScheduleRepository
import kotlinx.coroutines.launch

/**
 * 31/03/23.
 *
 * @author hardkgosai.
 */
class ClientScheduleViewModel(
    application: Application,
) : AndroidViewModel(application) {

    private var repository: ClientScheduleRepository

    init {
        repository = ClientScheduleRepository(application)
    }

    private val responseStatus = repository.getResponseStatus()

    fun getResponseStatus() = responseStatus

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

    fun getClientProfilePic(body: JsonObject, pair: Pair<Int, ClientsItem>) {
        viewModelScope.launch {
            repository.getClientProfilePic(body, pair)
        }
    }
}