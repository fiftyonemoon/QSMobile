package com.quicksolveplus.covid.covid_tracking.booster.add_booster.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.quicksolveplus.covid.covid_tracking.booster.add_booster.repository.AddBoosterRepository
import kotlinx.coroutines.launch

class AddBoosterViewModel(application: Application) : AndroidViewModel(application) {

    private var repository: AddBoosterRepository

    init {
        repository = AddBoosterRepository(application)
    }

    private val responseStatus = repository.getResponseStatus()

    fun responseStatus() = responseStatus

    fun uploadImageFile(body: JsonObject) {
        viewModelScope.launch {
            repository.uploadImageFile(body)
        }
    }

    fun getImageFile(body: JsonObject, pair: Pair<Int, String>) {
        viewModelScope.launch {
            repository.getImageFile(body,pair)
        }
    }

    fun deleteImageFile(body: JsonObject) {
        viewModelScope.launch {
            repository.deleteImageFile(body)
        }
    }

}