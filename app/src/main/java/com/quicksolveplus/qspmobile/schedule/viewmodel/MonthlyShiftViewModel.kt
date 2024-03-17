package com.quicksolveplus.qspmobile.schedule.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.quicksolveplus.api.ResponseStatus
import com.quicksolveplus.qspmobile.schedule.repository.MonthlyShiftRepository
import kotlinx.coroutines.launch

/**
 * 31/03/23.
 *
 * @author hardkgosai.
 */
class MonthlyShiftViewModel(
    application: Application,
) : AndroidViewModel(application) {

    private var repository: MonthlyShiftRepository

    init {
        repository = MonthlyShiftRepository(application)
    }

    private val responseStatus = repository.getResponseStatus()

    fun getResponseStatus() = responseStatus

    fun getStaffPreferenceForScheduleByMonth(body: JsonObject) {
        viewModelScope.launch {
            repository.getStaffPreferenceForScheduleByMonth(map = body)
        }
    }

    fun getStaffTORForScheduleByMonth(body: JsonObject) {
        viewModelScope.launch {
            repository.getStaffTORForScheduleByMonth(map = body)
        }
    }

    fun getSchedulesByMonth(body: JsonObject) {
        viewModelScope.launch {
            repository.getSchedulesByMonth(map = body)
        }
    }

    fun getSchedules(body: JsonObject) {
        viewModelScope.launch {
            repository.getSchedules(map = body)
        }
    }

    fun getStaffPreferenceForSchedule(body: JsonObject) {
        viewModelScope.launch {
            repository.getStaffPreferenceForSchedule(map = body)
        }
    }

    fun getStaffTORForSchedule(body: JsonObject) {
        viewModelScope.launch {
            repository.getStaffTORForSchedule(map = body)
        }
    }

    fun getClientProfilePic(
        body: JsonObject, filename: String, responseStatus: MutableLiveData<ResponseStatus>
    ) {
        viewModelScope.launch {
            repository.getClientProfilePic(
                map = body, filename = filename, responseStatus = responseStatus
            )
        }
    }
}