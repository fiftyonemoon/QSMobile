package com.quicksolveplus.covid.covid_result.test_result.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.quicksolveplus.covid.covid_result.test_result.repository.TestResultRepository
import kotlinx.coroutines.launch

class TestResultViewModel(application: Application) : AndroidViewModel(application) {
    private var repository: TestResultRepository

    init {
        repository = TestResultRepository(application)
    }

    private val responseStatus = repository.getResponseStatus()

    fun responseStatus() = responseStatus

    fun forGetEmployeeTestingResults(body: JsonObject) {
        viewModelScope.launch {
            repository.forGetEmployeeTestingResults(body)
        }
    }


}