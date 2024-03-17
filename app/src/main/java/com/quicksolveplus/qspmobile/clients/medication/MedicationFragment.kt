package com.quicksolveplus.qspmobile.clients.medication

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.quicksolveplus.api.Api
import com.quicksolveplus.api.RequestParameters
import com.quicksolveplus.api.ResponseStatus
import com.quicksolveplus.qspmobile.R
import com.quicksolveplus.qspmobile.clients.dr_visits.AddDoctorActivity
import com.quicksolveplus.qspmobile.clients.medication.adapter.MedicationAdapter
import com.quicksolveplus.qspmobile.clients.medication.model.Medication
import com.quicksolveplus.qspmobile.clients.medication.model.MedicationViewModel
import com.quicksolveplus.qspmobile.clients.models.ClientsItem
import com.quicksolveplus.qspmobile.databinding.FragmentMedicationBinding
import com.quicksolveplus.utils.*

class MedicationFragment : Fragment() {
    lateinit var binding: FragmentMedicationBinding
    private var clientDataString: String = ""
    private var client: ClientsItem? = null
    private var medicationAdapter: MedicationAdapter? = null
    private val viewModel by lazy {
        ViewModelProvider(this)[MedicationViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            clientDataString = it.getString(Constants.clientData).toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMedicationBinding.inflate(layoutInflater)
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String) =
            MedicationFragment().apply {
                arguments = Bundle().apply {
                    putString(Constants.clientData, param1)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getData()
        setObservers()
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

    private fun proceedFailure(it: ResponseStatus.Failed) {
        when (it.apiName) {
            Api.getClientContacts -> {
                if (it.msg == getString(R.string.str_no_more_medication)) {
                    binding.tvNoData.isVisible = true
                    binding.rvMedication.isGone = true
                    binding.tvNoData.text = it.msg

                } else {
                    toast(requireActivity(), it.msg)
                }
            }
        }
    }


    private fun proceedResponse(success: ResponseStatus.Success) {
        when (success.apiName) {
            Api.getClientMedicines -> {
                if (success.data is Medication) {
                    processedMedicationData(success.data)
                }
            }
        }
    }

    private fun processedMedicationData(data: Medication) {
        if (data.isNotEmpty()) {
            binding.rvMedication.isVisible = true
            binding.tvNoData.isGone = true
            setAdapter(data)
        } else {
            binding.tvNoData.isVisible = true
            binding.rvMedication.isGone = true
            binding.tvNoData.text = getString(R.string.str_no_more_medication)
        }
    }

    private fun setAdapter(medication: Medication) {
        medicationAdapter = MedicationAdapter(medication,false) { position ->
            if (QSPermissions.hasClientModuleAdminPermission() || QSPermissions.hasPermissionClientMedRxMaintenanceAccess() || QSPermissions.hasPermissionClientMedRxMaintenanceUpdate()) {
                val medicationItem = Gson().toJson(medication[position])
                val intent = Intent(requireContext(), AddDoctorActivity::class.java)
                intent.putExtra(Constants.isEdit, true)
                intent.putExtra(Constants.clientUID, client?.ClientUID)
                intent.putExtra(Constants.medicationData, medicationItem)
                requireActivity().startActivity(intent)
            }
        }
        binding.rvMedication.adapter = medicationAdapter
    }

    private fun getClientMedicines() {
        val body = client?.ClientUID?.let { RequestParameters.forMedicationList(it) }
        if (body != null) {
            viewModel.getClientMedicines(body)
        }
    }

    override fun onResume() {
        super.onResume()
        getClientMedicines()
    }

}