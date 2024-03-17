package com.quicksolveplus.qspmobile.schedule.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.quicksolveplus.qspmobile.schedule.repository.ScheduleShiftDetailsRepository
import kotlinx.coroutines.launch

/**
 * 17/04/23.
 *
 * @author hardkgosai.
 */
class ScheduleShiftDetailsViewModel(
    application: Application,
) : AndroidViewModel(application) {

    private var repository: ScheduleShiftDetailsRepository

    init {
        repository = ScheduleShiftDetailsRepository(application)
    }

    private val responseStatus = repository.getResponseStatus()

    fun getResponseStatus() = responseStatus

    fun getCustomClientServiceTypes(body: JsonObject) {
        viewModelScope.launch {
            repository.getCustomClientServiceTypes(map = body)
        }
    }

    fun getCustomEmployeeServiceTypes(body: JsonObject) {
        viewModelScope.launch {
            repository.getCustomEmployeeServiceTypes(map = body)
        }
    }

    fun getOptionListWithUID(body: JsonObject) {
        viewModelScope.launch {
            repository.getOptionListWithUID(map = body)
        }
    }

    fun getAttendanceTracking(body: JsonObject) {
        viewModelScope.launch {
            repository.getAttendanceTracking(map = body)
        }
    }

    fun getClientRemainingPOS(body: JsonObject) {
        viewModelScope.launch {
            repository.getClientRemainingPOS(map = body)
        }
    }

    fun getClientsForShiftPopup(body: JsonObject) {
        viewModelScope.launch {
            repository.getClientsForShiftPopup(map = body)
        }
    }

    fun getWorkersForShiftPopup(body: JsonObject) {
        viewModelScope.launch {
            repository.getWorkersForShiftPopup(map = body)
        }
    }

    fun getShiftSubCodes(body: JsonObject) {
        viewModelScope.launch {
            repository.getShiftSubCodes(map = body)
        }
    }

    fun getShiftObjectives(body: JsonObject) {
        viewModelScope.launch {
            repository.getShiftObjectives(map = body)
        }
    }

    fun getISPObjectivesForSchedule(body: JsonObject) {
        viewModelScope.launch {
            repository.getISPObjectivesForSchedule(map = body)
        }
    }
}