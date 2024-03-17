package com.quicksolveplus.covid.covid_tracking.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.quicksolveplus.covid.covid_tracking.models.CovidItem
import com.quicksolveplus.covid.covid_tracking.repository.CovidRepository
import kotlinx.coroutines.launch


class CovidViewModel(application: Application) : AndroidViewModel(application) {

    private var repository: CovidRepository

    init {
        repository = CovidRepository(application)
    }

    private val responseStatus = repository.getResponseStatus()

    fun responseStatus() = responseStatus

    fun getAllEmployeeCovidComplianceForms(body: JsonObject) {
        viewModelScope.launch {
            repository.getAllEmployeeCovidComplianceForms(body)
        }
    }

    fun getImageFile(body: JsonObject, pair: Pair<Int, String>) {
        viewModelScope.launch {
            repository.getImageFile(body,pair)
        }
    }

}