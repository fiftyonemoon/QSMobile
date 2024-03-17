package com.quicksolveplus.companies.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.quicksolveplus.companies.repository.CompanyRepository
import kotlinx.coroutines.launch

/**
 * 20/03/23.
 *
 * @author hardkgosai.
 */
class CompanyViewModel(
    application: Application,
) : AndroidViewModel(application) {

    private var repository: CompanyRepository

    init {
        repository = CompanyRepository(application)
    }

    private val responseStatus = repository.getResponseStatus()

    fun getResponseStatus() = responseStatus

    fun getQSMobileWebClients() {
        viewModelScope.launch {
            repository.getQSMobileWebClients()
        }
    }

}