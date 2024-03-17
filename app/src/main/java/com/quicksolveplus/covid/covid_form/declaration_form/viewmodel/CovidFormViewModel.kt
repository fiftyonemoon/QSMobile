package com.quicksolveplus.covid.covid_form.declaration_form.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.quicksolveplus.covid.covid_form.declaration_form.repository.CovidFormRepository
import kotlinx.coroutines.launch

class CovidFormViewModel(application: Application) : AndroidViewModel(application) {

    private var repository: CovidFormRepository

    init {
        repository = CovidFormRepository(application)
    }

    private val responseStatus = repository.getResponseStatus()

    fun responseStatus() = responseStatus


    fun getImageFile(body: JsonObject) {
        viewModelScope.launch {
            repository.getImageFile(body)
        }
    }

    fun getSignatureFile(body: JsonObject) {
        viewModelScope.launch {
            repository.getSignatureFile(body)
        }
    }

}