package com.quicksolveplus.covid.covid_result.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.quicksolveplus.covid.covid_result.repository.CovidResultRepository
import com.quicksolveplus.covid.covid_tracking.models.CovidItem
import kotlinx.coroutines.launch

class CovidResultViewModel(application: Application) : AndroidViewModel(application) {

    private var repository: CovidResultRepository

    init {
        repository = CovidResultRepository(application)
    }

    private val responseStatus = repository.getResponseStatus()

    fun responseStatus() = responseStatus

    fun getEmployeeCovidComplianceForms(body: JsonObject) {
        viewModelScope.launch {
            repository.getEmployeeCovidComplianceForms(body)
        }
    }

    fun getImageFile(body: JsonObject, pair: Pair<Int, String>) {
        viewModelScope.launch {
            repository.getImageFile(body,pair)
        }
    }


}