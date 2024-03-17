package com.quicksolveplus.qspmobile.schedule.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.quicksolveplus.qspmobile.schedule.repository.ScheduleRepository
import kotlinx.coroutines.launch

/**
 * 31/03/23.
 *
 * @author hardkgosai.
 */
class ScheduleViewModel(
    application: Application,
) : AndroidViewModel(application) {

    private var repository: ScheduleRepository

    init {
        repository = ScheduleRepository(application)
    }

    private val responseStatus = repository.getResponseStatus()

    fun getResponseStatus() = responseStatus

    fun getDashboard(body: JsonObject) {
        viewModelScope.launch {
            repository.getDashboard(body)
        }
    }
}