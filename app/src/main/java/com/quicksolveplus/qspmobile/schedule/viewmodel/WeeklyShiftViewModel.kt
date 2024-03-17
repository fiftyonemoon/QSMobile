package com.quicksolveplus.qspmobile.schedule.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.quicksolveplus.qspmobile.schedule.repository.WeeklyShiftRepository
import kotlinx.coroutines.launch

/**
 * 12/04/23.
 *
 * @author hardkgosai.
 */
class WeeklyShiftViewModel(
    application: Application,
) : AndroidViewModel(application) {

    private var repository: WeeklyShiftRepository

    init {
        repository = WeeklyShiftRepository(application)
    }

    private val responseStatus = repository.getResponseStatus()

    fun getResponseStatus() = responseStatus

    fun getStaffPreferenceForScheduleByWeek(body: JsonObject) {
        viewModelScope.launch {
            repository.getStaffPreferenceForScheduleByWeek(map = body)
        }
    }

    fun getStaffTORForScheduleByWeek(body: JsonObject) {
        viewModelScope.launch {
            repository.getStaffTORForScheduleByWeek(map = body)
        }
    }

    fun getSchedulesByWeek(body: JsonObject) {
        viewModelScope.launch {
            repository.getSchedulesByWeek(map = body)
        }
    }
}