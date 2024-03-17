package com.quicksolveplus.qspmobile.clients.dr_visits.repository

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.quicksolveplus.api.Api
import com.quicksolveplus.api.Client
import com.quicksolveplus.api.QSRepo
import com.quicksolveplus.api.ResponseStatus

class DoctorRepository(application: Application):QSRepo(application) {

    private val responseStatus = MutableLiveData<ResponseStatus>()

    fun getResponseStatus() = responseStatus

    suspend fun getDoctorVisits(map: JsonObject) {
        runApi(
            apiName = Api.getDoctorVisits, apiCall = {
                Client.api().getDoctorVisits(map)
            }, responseStatus = responseStatus
        )
    }

    suspend fun getDoctors(map: JsonObject) {
        runApi(
            apiName = Api.getDoctors, apiCall = {
                Client.api().getDoctors(map)
            }, responseStatus = responseStatus
        )
    }

    suspend fun updateDoctor(map: JsonObject) {
        runApi(
            apiName = Api.updateDoctor, apiCall = {
                Client.api().updateDoctor(map)
            }, responseStatus = responseStatus
        )
    }

    suspend fun addNewDoctor(map: JsonObject) {
        runApi(
            apiName = Api.addNewDoctor, apiCall = {
                Client.api().addNewDoctor(map)
            }, responseStatus = responseStatus
        )
    }
}