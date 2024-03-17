package com.quicksolveplus.covid.covid_tracking.testing_site.repository

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.quicksolveplus.api.Api
import com.quicksolveplus.api.Client
import com.quicksolveplus.api.QSRepo
import com.quicksolveplus.api.ResponseStatus

class CovidSiteRepository(application: Application) : QSRepo(application) {

    private val responseStatus = MutableLiveData<ResponseStatus>()

    fun getResponseStatus() = responseStatus

    suspend fun getTestingSites(map: JsonObject) {
        runApi(
            apiName = Api.getTestingSites,
            apiCall = {
                Client.api().getTestingSites(map)
            },
            responseStatus = responseStatus
        )
    }

}