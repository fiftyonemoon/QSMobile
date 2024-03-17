package com.quicksolveplus.covid.covid_result.repository

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.quicksolveplus.api.Api
import com.quicksolveplus.api.Client
import com.quicksolveplus.api.QSRepo
import com.quicksolveplus.api.ResponseStatus

class CovidResultRepository(application: Application) : QSRepo(application) {

    private val responseStatus = MutableLiveData<ResponseStatus>()

    fun getResponseStatus() = responseStatus

    suspend fun getEmployeeCovidComplianceForms(map: JsonObject) {
        runApi(
            apiName = Api.getEmployeeCovidComplianceForms,
            apiCall = {
                Client.api().getEmployeeCovidComplianceForms(map)
            },
            responseStatus = responseStatus
        )
    }

    suspend fun getImageFile(map: JsonObject,pair : Pair<Int, String>) {
        runApi(
            apiName = Api.getImageFile,
            apiCall = {
                Client.api().getImageFile(map)
            },
            responseStatus = responseStatus , pair
        )
    }


}