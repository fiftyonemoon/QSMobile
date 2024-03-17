package com.quicksolveplus.qspmobile.clients.dr_visits.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.quicksolveplus.qspmobile.clients.dr_visits.repository.DoctorRepository
import kotlinx.coroutines.launch

class DoctorViewModel(application: Application) : AndroidViewModel(application) {

    private var repository: DoctorRepository

    init {
        repository = DoctorRepository(application)
    }

    private val responseStatus = repository.getResponseStatus()

    fun responseStatus() = responseStatus

    fun getDoctorVisits(body: JsonObject) {
        viewModelScope.launch {
            repository.getDoctorVisits(body)
        }
    }

    fun getDoctors(body: JsonObject) {
        viewModelScope.launch {
            repository.getDoctors(body)
        }
    }

    fun updateDoctor(body: JsonObject) {
        viewModelScope.launch {
            repository.updateDoctor(body)
        }
    }

    fun addNewDoctor(body: JsonObject) {
        viewModelScope.launch {
            repository.addNewDoctor(body)
        }
    }
}