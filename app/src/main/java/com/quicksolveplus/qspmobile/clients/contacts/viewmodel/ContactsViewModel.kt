package com.quicksolveplus.qspmobile.clients.contacts.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.quicksolveplus.qspmobile.clients.contacts.repository.ContactsRepository
import kotlinx.coroutines.launch

class ContactsViewModel(application: Application) : AndroidViewModel(application) {

    private var repository: ContactsRepository

    init {
        repository = ContactsRepository(application)
    }

    private val responseStatus = repository.getResponseStatus()

    fun responseStatus() = responseStatus

    fun getClientContacts(body: JsonObject) {
        viewModelScope.launch {
            repository.getClientContacts(body)
        }
    }

    fun getOptionListWithUID(body: JsonObject) {
        viewModelScope.launch {
            repository.getOptionListWithUID(body)
        }
    }

    fun updateClientContact(body: JsonObject) {
        viewModelScope.launch {
            repository.updateClientContact(body)
        }
    }

    fun addNewClientContact(body: JsonObject) {
        viewModelScope.launch {
            repository.addNewClientContact(body)
        }
    }
}