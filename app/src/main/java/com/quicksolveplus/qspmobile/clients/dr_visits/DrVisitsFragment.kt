package com.quicksolveplus.qspmobile.clients.dr_visits

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
import com.quicksolveplus.qspmobile.clients.dr_visits.adapter.DoctorVisitAdapter
import com.quicksolveplus.qspmobile.clients.dr_visits.model.VisitDoctor
import com.quicksolveplus.qspmobile.clients.dr_visits.viewmodel.DoctorViewModel
import com.quicksolveplus.qspmobile.clients.models.ClientsItem
import com.quicksolveplus.qspmobile.databinding.FragmentDrVisitsBinding
import com.quicksolveplus.utils.*

class DrVisitsFragment : Fragment() {

    private lateinit var binding: FragmentDrVisitsBinding
    private var clientDataString: String = ""
    private var client: ClientsItem? = null
    private var doctorVisitAdapter: DoctorVisitAdapter? = null
    private val viewModel by lazy {
        ViewModelProvider(this)[DoctorViewModel::class.java]
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
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDrVisitsBinding.inflate(layoutInflater)
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String) =
            DrVisitsFragment().apply {
                arguments = Bundle().apply {
                    putString(Constants.clientData, param1)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObservers()
        getData()
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
                if (it.msg == resources.getString(R.string.str_no_doctor_visit)) {
                    binding.tvNoData.isVisible = true
                    binding.rvDoctor.isGone = true
                    binding.tvNoData.text = it.msg

                } else {
                    toast(requireActivity(), it.msg)
                }
            }
        }
    }


    private fun proceedResponse(success: ResponseStatus.Success) {
        when (success.apiName) {
            Api.getDoctorVisits -> {
                if (success.data is VisitDoctor) {
                    proceedDoctorVisit(success.data)
                }
            }
        }
    }

    private fun proceedDoctorVisit(doctorVisit: VisitDoctor) {
        if (doctorVisit.isNotEmpty()) {
            binding.rvDoctor.isVisible = true
            binding.tvNoData.isGone = true
            setAdapter(doctorVisit)
        } else {
            binding.tvNoData.isVisible = true
            binding.rvDoctor.isGone = true
            binding.tvNoData.text = getString(R.string.str_no_doctor_visit)
        }
    }

    private fun setAdapter(doctorVisit: VisitDoctor) {
        doctorVisitAdapter = DoctorVisitAdapter(doctorVisit) { position ->
            if(QSPermissions.hasClientModuleAdminPermission() || QSPermissions.hasPermissionClientDoctorVisitMaintenanceAccess()){
                val doctor = Gson().toJson(doctorVisit[position])
                val intent = Intent(requireContext(), AddDoctorActivity::class.java)
                intent.putExtra(Constants.isEdit, true)
                intent.putExtra(Constants.clientUID, client?.ClientUID)
                intent.putExtra(Constants.doctorData, doctor)
                requireActivity().startActivity(intent)
            }
        }
        binding.rvDoctor.adapter = doctorVisitAdapter
    }

    private fun getDoctorVisits() {
        client?.run {
            val body = RequestParameters.forDoctorVisits(clientID = ClientUID)
            viewModel.getDoctorVisits(body = body)
        }
    }

    override fun onResume() {
        super.onResume()
        getDoctorVisits()
    }
}