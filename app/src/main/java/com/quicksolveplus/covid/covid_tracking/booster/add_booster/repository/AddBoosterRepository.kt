package com.quicksolveplus.covid.covid_tracking.booster.add_booster.repository

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.quicksolveplus.api.Api
import com.quicksolveplus.api.Client
import com.quicksolveplus.api.QSRepo
import com.quicksolveplus.api.ResponseStatus

class AddBoosterRepository(application: Application) : QSRepo(application) {

    private val responseStatus = MutableLiveData<ResponseStatus>()

    fun getResponseStatus() = responseStatus

    suspend fun uploadImageFile(map: JsonObject) {
        runApi(
            apiName = Api.uploadImageFile,
            apiCall = {
                Client.api().uploadImageFile(map)
            },
            responseStatus = responseStatus
        )
    }

    suspend fun deleteImageFile(map: JsonObject) {
        runApi(
            apiName = Api.deleteImageFile,
            apiCall = {
                Client.api().deleteImageFile(map)
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