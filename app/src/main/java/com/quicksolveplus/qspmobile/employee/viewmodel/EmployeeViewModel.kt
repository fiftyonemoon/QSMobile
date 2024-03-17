package com.quicksolveplus.qspmobile.employee.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.quicksolveplus.qspmobile.employee.model.EmployeesItem
import com.quicksolveplus.qspmobile.employee.repository.EmployeeRepository
import kotlinx.coroutines.launch

class EmployeeViewModel(application: Application) : AndroidViewModel(application) {
    private var repository: EmployeeRepository

    init {
        repository = EmployeeRepository(application)
    }

    private val responseStatus = repository.getResponseStatus()

    fun responseStatus() = responseStatus

    fun getWorkers(body: JsonObject) {
        viewModelScope.launch {
            repository.getWorkers(body)
        }
    }

    fun getWorkersByMyCaseload(body: JsonObject) {
        viewModelScope.launch {
            repository.getWorkersByMyCaseload(body)
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