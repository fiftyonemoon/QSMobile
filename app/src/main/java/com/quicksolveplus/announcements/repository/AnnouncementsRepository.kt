package com.quicksolveplus.announcements.repository

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.quicksolveplus.api.Api
import com.quicksolveplus.api.Client
import com.quicksolveplus.api.QSRepo
import com.quicksolveplus.api.ResponseStatus

class AnnouncementsRepository(application: Application) : QSRepo(application) {

    private val responseStatus = MutableLiveData<ResponseStatus>()

    fun getResponseStatus() = responseStatus

    suspend fun getEmployeeAnnouncements(map: JsonObject) {
        runApi(
            apiName = Api.getEmployeeAnnouncements,
            apiCall = {
                Client.api().getEmployeeAnnouncements(map)
            },
            responseStatus = responseStatus
        )
    }

}