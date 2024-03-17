package com.quicksolveplus.authentication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.quicksolveplus.authentication.repository.LoginRepository
import kotlinx.coroutines.launch

/**
 * 17/03/23.
 *
 * @author hardkgosai.
 */
class LoginViewModel(
    application: Application,
) : AndroidViewModel(application) {

    private var repository: LoginRepository

    init {
        repository = LoginRepository(application)
    }

    private val responseStatus = repository.getResponseStatus()

    fun getResponseStatus() = responseStatus

    fun validateUserLogon(requestBody: JsonObject) {
        viewModelScope.launch {
            repository.validateUserLogon(requestBody)
        }
    }

    fun forgotPassword(requestBody: JsonObject) {
        viewModelScope.launch {
            repository.forgotPassword(requestBody)
        }
    }

    fun getQSMobileWebClients() {
        viewModelScope.launch {
            repository.getQSMobileWebClients()
        }
    }

}