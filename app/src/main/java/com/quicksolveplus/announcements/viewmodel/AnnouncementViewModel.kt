package com.quicksolveplus.announcements.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.quicksolveplus.announcements.repository.AnnouncementsRepository
import kotlinx.coroutines.launch

class AnnouncementViewModel(application: Application) : AndroidViewModel(application) {

    private var repository: AnnouncementsRepository

    init {
        repository = AnnouncementsRepository(application)
    }

    private val responseStatus = repository.getResponseStatus()

    fun responseStatus() = responseStatus

    fun getEmployeeAnnouncements(body: JsonObject) {
        viewModelScope.launch {
            repository.getEmployeeAnnouncements(body)
        }
    }


}