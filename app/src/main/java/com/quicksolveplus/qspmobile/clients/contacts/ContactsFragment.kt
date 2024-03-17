package com.quicksolveplus.qspmobile.clients.contacts

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.quicksolveplus.api.Api
import com.quicksolveplus.api.RequestParameters
import com.quicksolveplus.api.ResponseStatus
import com.quicksolveplus.qspmobile.R
import com.quicksolveplus.qspmobile.clients.contacts.adapter.ContactsAdapter
import com.quicksolveplus.qspmobile.clients.contacts.model.Contacts
import com.quicksolveplus.qspmobile.clients.contacts.viewmodel.ContactsViewModel
import com.quicksolveplus.qspmobile.clients.models.ClientsItem
import com.quicksolveplus.qspmobile.databinding.FragmentContactsBinding
import com.quicksolveplus.utils.*

class ContactsFragment : Fragment() {

    lateinit var binding: FragmentContactsBinding
    private var clientDataString: String = ""
    private var client: ClientsItem? = null

    private val viewModel by lazy {
        ViewModelProvider(this)[ContactsViewModel::class.java]
    }
    private var contactsAdapter: ContactsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            clientDataString = it.getString(Constants.clientData).toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentContactsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String) =
            ContactsFragment().apply {
                arguments = Bundle().apply {
                    putString(Constants.clientData, param1)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        getData()
        setObservers()
    }

    private fun initUI() {

    }

    private fun getData() {
        val gson = Gson()
        client = gson.fromJson(
            clientDataString,
            ClientsItem::class.java
        )
    }


    private fun setObservers() {
        viewModel.responseStatus().observe(viewLifecycleOwner) {
            when (it) {
                is ResponseStatus.Running -> {
                    showQSProgress(requireActivity())
                }
                is ResponseStatus.Success -> {
                    proceedResponse(it)
                    dismissQSProgress()
                }
                is ResponseStatus.Failed -> {
                    toast(requireContext(), it.msg)
                    proceedFailure(it)
                    dismissQSProgress()
                }
                else -> {
                    toast(requireContext(), "Something goes wrong")
                    dismissQSProgress()
                }
            }
        }
    }

    private fun getClientContacts() {
        client?.run {
            val body = RequestParameters.forClientContact(clientID = ClientUID)
            viewModel.getClientContacts(body = body)
        }
    }

    private fun proceedResponse(success: ResponseStatus.Success) {
        when (success.apiName) {
            Api.getClientContacts -> {
                if (success.data is Contacts) {
                    proceedContacts(success.data)
                }
            }
        }
    }

    private fun proceedFailure(it: ResponseStatus.Failed) {
        when (it.apiName) {
            Api.getClientContacts -> {
                if (it.msg == resources.getString(R.string.str_no_contact_msg)) {
                    binding.tvNoData.isVisible = true
                    binding.rvContacts.isGone = true
                    binding.tvNoData.text = it.msg

                } else {
                    toast(requireActivity(), it.msg)
                }
            }
        }
    }

    private fun proceedContacts(contacts: Contacts) {
        if (contacts.isNotEmpty()) {
            binding.rvContacts.isVisible = true
            binding.tvNoData.isGone = true
            setAdapter(contacts)
        } else {
            binding.tvNoData.isVisible = true
            binding.rvContacts.isGone = true
            binding.tvNoData.text = getString(R.string.str_no_contact_msg)
        }
    }

    private fun setAdapter(contacts: Contacts) {
        contactsAdapter = ContactsAdapter(requireContext(), contacts) { position ->
            if (QSPermissions.hasClientModuleAdminPermission() || QSPermissions.hasPermissionClientContactsMaintenanceAccess()) {
                val contact = Gson().toJson(contacts[position])
                val intent = Intent(requireContext(), AddContactActivity::class.java)
                intent.putExtra(Constants.isEdit, true)
                intent.putExtra(Constants.clientUID, client?.ClientUID)
                intent.putExtra(Constants.contactData, contact)
                requireActivity().startActivity(intent)
            }
        }
        binding.rvContacts.adapter = contactsAdapter
    }

    override fun onResume() {
        super.onResume()
        getClientContacts()
    }
}