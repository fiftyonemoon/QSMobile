package com.quicksolveplus.dashboard.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.quicksolveplus.dashboard.repository.DashboardRepository
import com.quicksolveplus.qspmobile.clients.models.ClientsItem
import kotlinx.coroutines.launch

/**
 * 27/03/23.
 *
 * @author hardkgosai.
 */
class DashboardViewModel(
    application: Application,
) : AndroidViewModel(application) {

    private var repository: DashboardRepository

    init {
        repository = DashboardRepository(application)
    }

    private val responseStatus = repository.getResponseStatus()

    fun getResponseStatus() = responseStatus

    fun getClientProfilePic(body: JsonObject) {
        viewModelScope.launch {
            repository.getClientProfilePic(body)
        }
    }

    fun getWorker(body: JsonObject) {
        viewModelScope.launch {
            repository.getWorker(body)
        }
    }

    fun getEmployeeAnnouncements(body: JsonObject) {
        viewModelScope.launch {
            repository.getEmployeeAnnouncements(body)
        }
    }

    fun getCovidComplianceFormDetails(body: JsonObject) {
        viewModelScope.launch {
            repository.getCovidComplianceFormDetails(body)
        }
    }
}