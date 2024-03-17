package com.quicksolveplus.qspmobile.clients.medication.repository

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.quicksolveplus.api.Api
import com.quicksolveplus.api.Client
import com.quicksolveplus.api.QSRepo
import com.quicksolveplus.api.ResponseStatus

class MedicationRepository(application: Application) : QSRepo(application) {

    private val responseStatus = MutableLiveData<ResponseStatus>()

    fun getResponseStatus() = responseStatus

    suspend fun getClientMedicines(map: JsonObject) {
        runApi(
            apiName = Api.getClientMedicines, apiCall = {
                Client.api().getClientMedicines(map)
            }, responseStatus = responseStatus
        )
    }
}