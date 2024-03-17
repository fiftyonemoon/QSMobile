package com.quicksolveplus.qspmobile.schedule.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.quicksolveplus.qspmobile.employee.model.EmployeesItem
import com.quicksolveplus.qspmobile.schedule.repository.EmployeeScheduleRepository
import kotlinx.coroutines.launch

/**
 * 31/03/23.
 *
 * @author hardkgosai.
 */
class EmployeeScheduleViewModel(
    application: Application,
) : AndroidViewModel(application) {

    private var repository: EmployeeScheduleRepository

    init {
        repository = EmployeeScheduleRepository(application)
    }

    private val responseStatus = repository.getResponseStatus()

    fun getResponseStatus() = responseStatus

    fun getWorkers(body: JsonObject) {
        viewModelScope.launch {
            repository.getWorkers(body)
        }
    }

    fun getWorker(body: JsonObject) {
        viewModelScope.launch {
            repository.getWorker(body)
        }
    }

    fun getWorkersWithSameSupervisor(body: JsonObject) {
        viewModelScope.launch {
            repository.getWorkersWithSameSupervisor(body)
        }
    }

    fun getWorkersForEmployeeOffices(body: JsonObject) {
        viewModelScope.launch {
            repository.getWorkersForEmployeeOffices(body)
        }
    }

    fun getWorkerProfilePic(body: JsonObject, pair: Pair<Int, EmployeesItem>) {
        viewModelScope.launch {
            repository.getWorkerProfilePic(body, pair)
        }
    }
}