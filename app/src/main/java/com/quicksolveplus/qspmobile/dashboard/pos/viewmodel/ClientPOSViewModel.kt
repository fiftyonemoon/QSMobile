package com.quicksolveplus.qspmobile.dashboard.pos.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.quicksolveplus.qspmobile.dashboard.pos.repository.ClientPOSRepository
import kotlinx.coroutines.launch

class ClientPOSViewModel(application: Application) : AndroidViewModel(application) {

    private var repository: ClientPOSRepository

    init {
        repository = ClientPOSRepository(application)
    }

    private val responseStatus = repository.getResponseStatus()

    fun responseStatus() = responseStatus

    fun getClientPOS(body: JsonObject) {
        viewModelScope.launch {
            repository.getClientPOS(body)
        }
    }

}