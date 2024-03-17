package com.quicksolveplus.covid.covid_result.test_result.repository

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.quicksolveplus.api.Api
import com.quicksolveplus.api.Client
import com.quicksolveplus.api.QSRepo
import com.quicksolveplus.api.ResponseStatus

class OptionsRepository (application: Application) : QSRepo(application) {

    private val responseStatus = MutableLiveData<ResponseStatus>()

    fun getResponseStatus() = responseStatus

    suspend fun forGetOptionList(map: JsonObject) {
        runApi(
            apiName = Api.getOptionList,
            apiCall = {
                Client.api().getOptionList(map)
            },
            responseStatus = responseStatus
        )
    }

}