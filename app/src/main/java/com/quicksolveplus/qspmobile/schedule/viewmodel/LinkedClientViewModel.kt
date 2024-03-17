package com.quicksolveplus.qspmobile.schedule.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.quicksolveplus.qspmobile.schedule.repository.LinkedClientRepository
import kotlinx.coroutines.launch

/**
 * 31/03/23.
 *
 * @author hardkgosai.
 */
class LinkedClientViewModel(
    application: Application,
) : AndroidViewModel(application) {

    private var repository: LinkedClientRepository

    init {
        repository = LinkedClientRepository(application)
    }

    private val responseStatus = repository.getResponseStatus()

    fun getResponseStatus() = responseStatus

    fun getClientProfilePic(body: JsonObject, filename: String) {
        viewModelScope.launch {
            repository.getClientProfilePic(map = body, filename = filename)
        }
    }
}