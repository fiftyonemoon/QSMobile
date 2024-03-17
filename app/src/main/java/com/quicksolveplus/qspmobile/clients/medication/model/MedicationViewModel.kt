package com.quicksolveplus.qspmobile.clients.medication.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.quicksolveplus.qspmobile.clients.medication.repository.MedicationRepository
import kotlinx.coroutines.launch

class MedicationViewModel(application: Application) : AndroidViewModel(application) {
    private var repository: MedicationRepository

    init {
        repository = MedicationRepository(application)
    }

    private val responseStatus = repository.getResponseStatus()

    fun responseStatus() = responseStatus

    fun getClientMedicines(body: JsonObject) {
        viewModelScope.launch {
            repository.getClientMedicines(body)
        }
    }
}