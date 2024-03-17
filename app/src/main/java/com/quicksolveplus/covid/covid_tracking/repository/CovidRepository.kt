package com.quicksolveplus.covid.covid_tracking.repository

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.quicksolveplus.api.Api
import com.quicksolveplus.api.Client
import com.quicksolveplus.api.QSRepo
import com.quicksolveplus.api.ResponseStatus
import com.quicksolveplus.covid.covid_tracking.models.CovidItem

class CovidRepository (application: Application) : QSRepo(application) {

    private val responseStatus = MutableLiveData<ResponseStatus>()

    fun getResponseStatus() = responseStatus

    suspend fun getAllEmployeeCovidComplianceForms(map: JsonObject) {
        runApi(
            apiName = Api.getAllEmployeeCovidComplianceForms,
            apiCall = {
                Client.api().getAllEmployeeCovidComplianceForms(map)
            },
            responseStatus = responseStatus
        )
    }

    suspend fun getImageFile(map: JsonObject,pair: Pair<Int, String?>) {
        runApi(
            apiName = Api.getImageFile,
            apiCall = {
                Client.api().getImageFile(map)
            },
            responseStatus = responseStatus, other =pair
        )
    }

}