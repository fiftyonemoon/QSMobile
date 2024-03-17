package com.quicksolveplus.covid.covid_tracking.testing_site.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.quicksolveplus.covid.covid_tracking.testing_site.repository.CovidSiteRepository
import kotlinx.coroutines.launch

class CovidSiteViewModel(application: Application) : AndroidViewModel(application) {

    private var repository: CovidSiteRepository

    init {
        repository = CovidSiteRepository(application)
    }

    private val responseStatus = repository.getResponseStatus()

    fun responseStatus() = responseStatus

    fun getTestingSites(body: JsonObject) {
        viewModelScope.launch {
            repository.getTestingSites(body)
        }
    }
}