package com.quicksolveplus.dashboard

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.quicksolveplus.QSMobile
import com.quicksolveplus.modifiers.Glidifiers.loadGlide
import com.quicksolveplus.announcements.AnnouncementsActivity
import com.quicksolveplus.api.Api
import com.quicksolveplus.api.RequestParameters
import com.quicksolveplus.api.ResponseStatus
import com.quicksolveplus.authentication.models.CovidComplianceFormDetails
import com.quicksolveplus.covid.covid_form.CovidFormActivity
import com.quicksolveplus.covid.covid_result.CovidTestResultActivity
import com.quicksolveplus.covid.covid_tracking.CovidTrackingActivity
import com.quicksolveplus.dashboard.dialogs.CovidVaccineDeclarationForm
import com.quicksolveplus.dashboard.models.Announcements
import com.quicksolveplus.dashboard.models.AnnouncementsItem
import com.quicksolveplus.dashboard.models.Worker
import com.quicksolveplus.dashboard.viewmodel.DashboardViewModel
import com.quicksolveplus.dialogs.QSImagePicker
import com.quicksolveplus.modifiers.Actifiers.cameraIntent
import com.quicksolveplus.modifiers.Actifiers.imageIntent
import com.quicksolveplus.modifiers.Actifiers.openActivity
import com.quicksolveplus.modifiers.Actifiers.registerActivityResultLauncher
import com.quicksolveplus.qsbase.Base
import com.quicksolveplus.qspmobile.QSMobileActivity
import com.quicksolveplus.qspmobile.R
import com.quicksolveplus.qspmobile.databinding.ActivityDashboardBinding
import com.quicksolveplus.settings.SettingsActivity
import com.quicksolveplus.utils.*
import okhttp3.ResponseBody

//TODO - check language condition on activity create
//TODO = check [proceedEmployeeAnnouncement] and add translate words function check old project

class DashboardActivity : Base() {

    private lateinit var binding: ActivityDashboardBinding
    private val viewModel: DashboardViewModel by viewModels()

    private var highPriorityAnnouncements = ArrayList<AnnouncementsItem>()
    private var unreadAnnouncementsCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window?.statusBarColor = getColor(R.color.app_bg)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
        setObservers()
        //updateUI()
    }

    private fun initUI() {
        binding.ivProfileImage.setOnClickListener { openQSImagePicker() }
        binding.clAnnouncements.setOnClickListener { openActivity(AnnouncementsActivity::class.java) }
        binding.clQsMobile.setOnClickListener { openActivity(QSMobileActivity::class.java) }
        binding.btnLogout.setOnClickListener { (application as QSMobile).logout(this) }
        binding.ivCovid.setOnClickListener { navigateToCovid() }
        binding.ivSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            languageResultLauncher.launch(intent)
        }
    }

    private fun setObservers() {
        viewModel.getResponseStatus().observe(this) {
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
                    proceedFailure(it)
                    dismissQSProgress()
                }
                else -> {
                    toast(this, "Something goes wrong")
                    dismissQSProgress()
                }
            }
        }
    }

    private fun updateUI() {
        setUserInfo()
        requestProfilePicture()
    }

    private fun setUserInfo() {
        Preferences.instance?.user?.run {

            val username = "${getString(R.string.str_welcome)}, $message $message2"
            val hasAnnouncements = QSPermissions.hasAnnouncementsPermission()

            binding.tvUserName.text = username
            binding.tvCompCoinBalance.isVisible = isCompCoinCompany
            binding.clAnnouncements.isVisible = hasAnnouncements
            binding.clQsClock.isVisible = canAccessClock
            binding.clQsMed.isVisible = canAccessQSMed
        }
    }

    private fun navigateToCovid() {
        Preferences.instance?.user?.run {
            if (!hasSubmittedVaccination && !hasSubmittedExemption) {
                openActivity(CovidFormActivity::class.java)
            } else if (hasSubmittedVaccination && hasSubmittedExemption) {
                openActivity(CovidTrackingActivity::class.java)
            } else if (hasSubmittedVaccination && !hasSubmittedExemption) {
                openActivity(CovidTrackingActivity::class.java)
            } else if (!hasSubmittedVaccination && hasSubmittedExemption) {
                openActivity(CovidTestResultActivity::class.java)
            }
        }
    }

    private fun proceedResponse(success: ResponseStatus.Success) {
        when (success.apiName) {
            Api.getClientProfilePic -> {
                if (success.data is ResponseBody) {
                    val bitmap = BitmapFactory.decodeStream(success.data.byteStream())
                    binding.ivProgress.isVisible = false
                    binding.ivProfileImage.loadGlide(
                        any = bitmap,
                        placeholder = ContextCompat.getDrawable(this, R.drawable.ic_avatar)
                    )
                }
                if (binding.tvCompCoinBalance.isVisible) {
                    requestWorker()
                } else if (binding.clAnnouncements.isVisible) {
                    requestEmployeeAnnouncements()
                }
            }
            Api.getWorker -> {
                if (success.data is Worker) {
                    val balance =
                        "${getString(R.string.str_compCoin_balance)}: ${success.data.compCoinBalance.toInt()} cc"
                    binding.tvCompCoinBalance.text = balance
                    requestEmployeeAnnouncements()
                }
            }
            Api.getEmployeeAnnouncements -> {
                if (success.data is Announcements) {
                    proceedEmployeeAnnouncements(success.data)
                }
                requestCovidComplianceFormDetails()
            }
            Api.getCovidComplianceFormDetails -> {
                if (success.data is CovidComplianceFormDetails) {
                    proceedCovidComplianceFormDetails(success.data)
                }
            }
        }
    }

    private fun proceedFailure(failed: ResponseStatus.Failed) {
        when (failed.apiName) {
            Api.getClientProfilePic -> {
                if (binding.tvCompCoinBalance.isVisible) {
                    requestWorker()
                } else if (binding.clAnnouncements.isVisible) {
                    requestEmployeeAnnouncements()
                }
            }
            Api.getWorker -> {
                requestEmployeeAnnouncements()
            }
            Api.getEmployeeAnnouncements -> {
                requestCovidComplianceFormDetails()
            }
            Api.getCovidComplianceFormDetails -> {
                openCovidVaccineDeclarationFormDialog()
            }
        }
    }

    private fun requestProfilePicture() {
        Preferences.instance?.user?.run {
            val body = RequestParameters.forProfilePicture(
                photo = employeePhoto, level1 = userLevel1
            )
            viewModel.getClientProfilePic(body)
        }
    }

    private fun requestWorker() {
        Preferences.instance?.user?.run {
            if (isCompCoinCompany) {
                val body = RequestParameters.forWorker(staffID = uID)
                viewModel.getWorker(body)
            }
        }
    }

    private fun requestEmployeeAnnouncements() {
        if (QSPermissions.hasAnnouncementsPermission()) {
            Preferences.instance?.user?.run {
                val body = RequestParameters.forEmployeeAnnouncements(employeeId = uID)
                viewModel.getEmployeeAnnouncements(body)
            }
        }
    }

    private fun requestCovidComplianceFormDetails() {
        Preferences.instance?.user?.run {
            val body = RequestParameters.forCovidComplianceFormDetails(employeeId = uID)
            viewModel.getCovidComplianceFormDetails(body)
        }
    }

    private fun proceedEmployeeAnnouncements(announcements: Announcements) {
        unreadAnnouncementsCount = 0
        highPriorityAnnouncements.clear()

        announcements.map {
            if (!it.isRead) {
                unreadAnnouncementsCount++
                if (it.isHighPriorityAnnouncement) {
                    highPriorityAnnouncements.add(it)
                }
            }
        }

        binding.tvAnnouncement.text =
            if (unreadAnnouncementsCount > 0) "${getString(R.string.str_announcements)} ($unreadAnnouncementsCount)"
            else getString(R.string.str_announcements)

        //todo translate announcement message here - old project
    }

    private fun proceedCovidComplianceFormDetails(details: CovidComplianceFormDetails) {
        val user = Preferences.instance?.user
        user?.let {
            it.hasSubmittedExemption = details.hasSubmittedExemption
            it.hasSubmittedVaccination = details.hasSubmittedVaccination
            it.hasSubmittedBooster = details.hasSubmittedBooster
            it.covidComplianceFormDetails = details
            Preferences.instance?.user = it
        }
        openCovidVaccineDeclarationFormDialog()
    }

    private fun openCovidVaccineDeclarationFormDialog() {
        Preferences.instance?.user?.run {
            if (!hasSubmittedExemption && !hasSubmittedVaccination && showCovidFormFeature) {
                CovidVaccineDeclarationForm(this@DashboardActivity) { isContinue ->
                    if (isContinue) {
                        //openActivity()
                    }
                }.show()
            }
        }
    }

    private fun openQSImagePicker() {
        QSImagePicker(this) { isCamera ->
            if (isCamera) {
                cameraLauncher.launch(cameraIntent)
            } else {
                galleryLauncher.launch(imageIntent)
            }
        }
    }

    private val cameraLauncher = registerActivityResultLauncher {
        if (it.resultCode == RESULT_OK) {
            val intent = it.data
            intent?.run {
                binding.ivProfileImage.loadGlide(this.data)
            }
        }
    }

    private val galleryLauncher = registerActivityResultLauncher {
        if (it.resultCode == RESULT_OK) {
            val intent = it.data
            intent?.run {
                binding.ivProfileImage.loadGlide(this.data)
            }
        }
    }

    private val languageResultLauncher = registerActivityResultLauncher {
        if (it.resultCode == RESULT_OK) {
            recreate()
        }
    }
}