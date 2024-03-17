package com.quicksolveplus.qspmobile.clients.dr_visits

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.google.gson.Gson
import com.quicksolveplus.api.Api
import com.quicksolveplus.api.RequestParameters
import com.quicksolveplus.api.ResponseStatus
import com.quicksolveplus.dialogs.QSFilterItems
import com.quicksolveplus.dialogs.QSSuccess
import com.quicksolveplus.qspmobile.R
import com.quicksolveplus.qspmobile.clients.dr_visits.model.ListDoctor
import com.quicksolveplus.qspmobile.clients.dr_visits.model.ListDoctorItem
import com.quicksolveplus.qspmobile.clients.dr_visits.model.VisitDoctorItem
import com.quicksolveplus.qspmobile.clients.dr_visits.viewmodel.DoctorViewModel
import com.quicksolveplus.qspmobile.databinding.ActivityAddDoctorBinding
import com.quicksolveplus.utils.*
import okhttp3.ResponseBody

class AddDoctorActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddDoctorBinding
    var clientUID = 0
    private var doctor: VisitDoctorItem? = null
    private var doctorData = ""
    private var isEditMode = false
    private var doctorList: ArrayList<ListDoctorItem> = arrayListOf()
    private val viewModel: DoctorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddDoctorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getData()
        initUI()
        setToolBar()
        initControls()
        setObservers()
    }

    private fun initUI() {
        binding.clDoctor.setOnClickListener {
            if (doctorList.isEmpty()) {
                getDoctorList()
            } else {
                openDoctorDialog(doctorList as ListDoctor)
            }
        }

        binding.clVisitDate.setOnClickListener {
            openDatePickerDialog(
                this@AddDoctorActivity,
                binding.tvVisitDate,
                binding.tvVisitDate.text.toString()
            )
        }

        binding.clFrom.setOnClickListener {
            openTimePickerDialog(
                this@AddDoctorActivity,
                binding.tvFrom,
                binding.tvFrom.text.toString()
            )
        }

        binding.clTo.setOnClickListener {
            openTimePickerDialog(
                this@AddDoctorActivity,
                binding.tvTo,
                binding.tvTo.text.toString()
            )
        }

        binding.clNextVisit.setOnClickListener {
            openDatePickerDialog(
                this@AddDoctorActivity,
                binding.tvNextVisit,
                binding.tvNextVisit.text.toString()
            )
        }

        binding.clNextFrom.setOnClickListener {
            openTimePickerDialog(
                this@AddDoctorActivity,
                binding.tvNextFrom,
                binding.tvNextFrom.text.toString()
            )
        }

        binding.clNextTo.setOnClickListener {
            openTimePickerDialog(
                this@AddDoctorActivity,
                binding.tvNextTo,
                binding.tvNextTo.text.toString()
            )
        }

    }

    private fun setToolBar() {
        binding.toolbar.apply {
            if (isEditMode) {
                tvTitle.text = getString(R.string.str_update_doctor_apts)
            } else {
                tvTitle.text = getString(R.string.str_add_doctor_apts)
            }

            if (QSPermissions.hasClientModuleAdminPermission() || QSPermissions.hasPermissionClientDoctorVisitMaintenanceCreate() || QSPermissions.hasPermissionClientDoctorVisitMaintenanceUpdate()) {
                ivSave.isVisible = true
            } else {
                ivSave.isGone = true
            }

            ivSave.setOnClickListener {
                if (isValidFields() && (QSPermissions.hasClientModuleAdminPermission() || QSPermissions.hasPermissionClientDoctorVisitMaintenanceCreate() || QSPermissions.hasPermissionClientDoctorVisitMaintenanceUpdate())) {
                    saveDoctor()
                }
            }
            ivBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        }
    }

    private fun saveDoctor() {
        var uId: Int? = 0
        if (isEditMode && (QSPermissions.hasClientModuleAdminPermission() || QSPermissions.hasPermissionClientDoctorVisitMaintenanceUpdate())) {
            uId = doctor?.UID
            val body = RequestParameters.forSaveDoctor(
                binding.tvDoctor.text.toString(),
                clientUID,
                binding.tvVisitDate.text.toString(),
                binding.tvFrom.text.toString(),
                binding.tvTo.text.toString(),
                binding.etReason.text.toString(),
                binding.etOutCome.text.toString(),
                binding.tvMedication.text.toString(),
                binding.tvNextVisit.text.toString(),
                binding.tvNextFrom.text.toString(),
                binding.tvNextTo.text.toString(),
                uId
            )
            viewModel.updateDoctor(body)
        } else {
            val body = RequestParameters.forSaveDoctor(
                binding.tvDoctor.text.toString(),
                clientUID,
                binding.tvVisitDate.text.toString(),
                binding.tvFrom.text.toString(),
                binding.tvTo.text.toString(),
                binding.etReason.text.toString(),
                binding.etOutCome.text.toString(),
                binding.tvMedication.text.toString(),
                binding.tvNextVisit.text.toString(),
                binding.tvNextFrom.text.toString(),
                binding.tvNextTo.text.toString(),
                uId
            )
            viewModel.addNewDoctor(body)
        }
    }

    private fun initControls() {
        if (isEditMode) {
            if (doctor != null) {
                if (QSPermissions.hasClientModuleAdminPermission() || QSPermissions.hasPermissionClientDoctorVisitMaintenanceUpdate()) {
                    fillData()
                } else if (QSPermissions.hasPermissionClientDoctorVisitMaintenanceRead() || QSPermissions.hasPermissionClientDoctorVisitMaintenanceAccess()) {
                    fillData()
                    makeScreenReadOnly()
                }
            }
        }
    }

    private fun fillData() {
        doctor?.run {
            binding.tvDoctor.text = Doctor
            binding.tvVisitDate.text = VisitDate
            binding.tvFrom.text = VisitStartTime
            binding.tvTo.text = VisitEndTime
            binding.etReason.setText(Reason)
            binding.etOutCome.setText(Outcome)
            binding.etMedication.setText(NewMedication)
            binding.tvNextVisit.text = NextVisitDate
            binding.tvNextFrom.text = NextVisitStartTime
            binding.tvNextTo.text = NextVisitEndTime
        }
    }

    private fun makeScreenReadOnly() {
        binding.apply {
            clDoctor.isEnabled = false
            clVisitDate.isEnabled = false
            clFrom.isEnabled = false
            clTo.isEnabled = false
            etReason.isEnabled = false
            etOutCome.isEnabled = false
            etMedication.isEnabled = false
            clNextVisit.isEnabled = false
            clNextFrom.isEnabled = false
            clNextTo.isEnabled = false
        }
    }

    private fun setObservers() {
        viewModel.responseStatus().observe(this) {
            when (it) {
                is ResponseStatus.Running -> {
                    showQSProgress(this)
                }
                is ResponseStatus.Success -> {
                    proceedResponse(it)
                    dismissQSProgress()
                }
                is ResponseStatus.Failed -> {
                    toast(this, it.msg)
                    dismissQSProgress()
                }
                else -> {
                    toast(this, "Something goes wrong")
                    dismissQSProgress()
                }
            }
        }
    }

    private fun proceedResponse(success: ResponseStatus.Success) {
        when (success.apiName) {
            Api.getDoctors -> {
                if (success.data is ListDoctor) {
                    doctorList = success.data
                    openDoctorDialog(success.data)
                }
            }
            Api.addNewDoctor -> {
                if (success.data is ResponseBody) {
                    QSSuccess(
                        this@AddDoctorActivity,
                        getString(R.string.str_new_doctor_appointment_added_success)
                    ) { ok ->
                        if (ok) {
                            finish()
                        }
                    }.show()
                }
            }

            Api.updateDoctor -> {
                if (success.data is ResponseBody) {
                    QSSuccess(
                        this@AddDoctorActivity,
                        getString(R.string.str_doctor_appointment_saved_success)
                    ) { ok ->
                        if (ok) {
                            finish()
                        }
                    }.show()
                }
            }
        }
    }

    private fun openDoctorDialog(data: ListDoctor) {
        val list: ArrayList<String> = arrayListOf()
        for (item in data) {
            list.add(item.Text)
        }
        list.removeAt(0)
        QSFilterItems(
            context = this@AddDoctorActivity,
            items = list,
            title = getString(R.string.str_select_doctor)
        ) { position ->
            binding.tvDoctor.text = list[position]
        }.show()
    }

    private fun getDoctorList() {
        val body = RequestParameters.forDoctorList(clientUID)
        viewModel.getDoctors(body = body)
    }

    private fun isValidFields(): Boolean {
        return Validation(context = this@AddDoctorActivity).run {
            isFieldValid(
                binding.tvVisitDate.text.toString(),
                getString(R.string.str_select_visit_date)
            ) &&
                    isFieldValid(
                        binding.tvFrom.text.toString(),
                        getString(R.string.str_select_visit_start_time)
                    ) &&
                    isFieldValid(
                        binding.tvTo.text.toString(),
                        getString(R.string.str_select_visit_end_time)
                    ) &&
                    isValidNextVisitDate(
                        binding.tvNextVisit,
                        binding.tvNextFrom,
                        binding.tvNextTo
                    ) &&
                    isValidFromToDate(
                        binding.tvFrom,
                        binding.tvTo,
                        binding.tvNextFrom,
                        binding.tvNextTo
                    )
        }
    }

    private fun getData() {
        if (intent.hasExtra(Constants.clientUID)) {
            clientUID = intent.getIntExtra(Constants.clientUID, 0)
        }

        if (intent.hasExtra(Constants.doctorData)) {
            val gson = Gson()
            doctorData = intent.getStringExtra(Constants.doctorData).toString()
            doctor = gson.fromJson(
                doctorData, VisitDoctorItem::class.java
            )
        }
        if (intent.hasExtra(Constants.isEdit)) {
            isEditMode = intent.getBooleanExtra(Constants.isEdit, false)
        }
    }
}