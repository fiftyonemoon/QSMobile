package com.quicksolveplus.qspmobile.schedule.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.quicksolveplus.qspmobile.employee.model.EmployeesItem
import com.quicksolveplus.qspmobile.schedule.repository.TrainedEmployeesRepository
import kotlinx.coroutines.launch

/**
 * 11/04/23.
 *
 * @author hardkgosai.
 */
class TrainedEmployeesViewModel(
    application: Application,
) : AndroidViewModel(application) {

    private var repository: TrainedEmployeesRepository

    init {
        repository = TrainedEmployeesRepository(application)
    }

    private val responseStatus = repository.getResponseStatus()

    fun getResponseStatus() = responseStatus

    fun getClientTrainedEmployees(body: JsonObject) {
        viewModelScope.launch {
            repository.getClientTrainedEmployees(map = body)
        }
    }

    fun getWorkerProfilePic(body: JsonObject, pair: Pair<Int, EmployeesItem>) {
        viewModelScope.launch {
            repository.getWorkerProfilePic(body, pair)
        }
    }
}