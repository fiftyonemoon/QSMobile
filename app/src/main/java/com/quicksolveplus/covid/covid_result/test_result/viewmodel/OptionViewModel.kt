package com.quicksolveplus.covid.covid_result.test_result.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.quicksolveplus.covid.covid_result.test_result.repository.OptionsRepository
import kotlinx.coroutines.launch

class OptionViewModel (application: Application) : AndroidViewModel(application) {

    private var repository: OptionsRepository

    init {
        repository = OptionsRepository(application)
    }

    private val responseStatus = repository.getResponseStatus()

    fun responseStatus() = responseStatus

    fun forGetOptionList(body: JsonObject) {
        viewModelScope.launch {
            repository.forGetOptionList(body)
        }
    }
}