package com.quicksolveplus.covid.covid_form.declaration_form.repository

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.quicksolveplus.api.Api
import com.quicksolveplus.api.Client
import com.quicksolveplus.api.QSRepo
import com.quicksolveplus.api.ResponseStatus

class CovidFormRepository(application: Application) : QSRepo(application) {

    private val responseStatus = MutableLiveData<ResponseStatus>()

    fun getResponseStatus() = responseStatus

    suspend fun getImageFile(map: JsonObject) {
        runApi(
            apiName = Api.getImageFile,
            apiCall = {
                Client.api().getImageFile(map)
            },
            responseStatus = responseStatus
        )
    }

    suspend fun getSignatureFile(map: JsonObject) {
        runApi(
            apiName = Api.getSignatureFile,
            apiCall = {
                Client.api().getSignatureFile(map)
            },
            responseStatus = responseStatus
        )
    }

}