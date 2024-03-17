package com.quicksolveplus.covid.covid_tracking

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import com.quicksolveplus.api.Api
import com.quicksolveplus.api.RequestParameters
import com.quicksolveplus.api.ResponseStatus
import com.quicksolveplus.covid.covid_tracking.adapter.CovidPagerAdapter
import com.quicksolveplus.covid.covid_tracking.models.Covid
import com.quicksolveplus.covid.covid_tracking.models.CovidItem
import com.quicksolveplus.covid.covid_tracking.testing_site.CovidSiteActivity
import com.quicksolveplus.covid.covid_tracking.viewmodel.CovidViewModel
import com.quicksolveplus.dialogs.QSAlert
import com.quicksolveplus.modifiers.Actifiers.openActivity
import com.quicksolveplus.qspmobile.R
import com.quicksolveplus.qspmobile.databinding.ActivityCovidTrackingBinding
import com.quicksolveplus.utils.*

class CovidTrackingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCovidTrackingBinding
    private val viewModel: CovidViewModel by viewModels()

    companion object {
        var boosterList: ArrayList<CovidItem> = arrayListOf()
        var vaccinationList: ArrayList<CovidItem> = arrayListOf()
        var supportDocumentList: ArrayList<String> = arrayListOf()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCovidTrackingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
        setObservers()
        getCovidData()
    }


    private fun initUI() {
        binding.apply {
            toolBar.tvTitle.setText(R.string.str_covid_tracking)
            toolBar.ivRepeat.visibility = View.VISIBLE
            toolBar.ivTracking.visibility = View.VISIBLE
            toolBar.ivBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
            toolBar.ivTracking.setOnClickListener {
                openActivity(CovidSiteActivity::class.java)
            }
            toolBar.ivRepeat.setOnClickListener {
                openRefreshVaccineAlert()
            }
        }

    }

    private fun openRefreshVaccineAlert() {
        QSAlert(
            context = this,
            title = null,
            message = getString(R.string.str_vaccine_record_can_not_be_changed),
            positiveButtonText = getString(R.string.str_ok),
            cancelable = false
        ).show()
    }

    private fun setTabBarUI() {

        binding.apply {

            tabCovid.addTab(tabCovid.newTab().setText(getString(R.string.str_vaccination)))
            tabCovid.addTab(tabCovid.newTab().setText(getString(R.string.str_booster)))

            val adapter = CovidPagerAdapter(supportFragmentManager, tabCovid.tabCount)

            viewPager.adapter = adapter
            viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabCovid))
            tabCovid.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    viewPager.currentItem = tab.position
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {}
                override fun onTabReselected(tab: TabLayout.Tab) {}
            })
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
            Api.getAllEmployeeCovidComplianceForms -> {
                if (success.data is Covid) {
                    val covidData = success.data
                    vaccinationList.clear()
                    boosterList.clear()
                    supportDocumentList.clear()
                    for (pos in covidData.indices) {

                        if (covidData[pos].ComplianceType != null && !covidData[pos].ComplianceType.equals(resources.getString(R.string.str_booster)))
                            vaccinationList.add(covidData[pos])
                        else
                            boosterList.add(covidData[pos])

//                        if (covidData[pos].ComplianceType == getString(R.string.str_booster)) {
//                            boosterList.add(covidData[pos])
//                        } else {
//                            vaccinationList.add(covidData[pos])
//                        }
                    }
                }
            }
        }
        setTabBarUI()
    }


    private fun getCovidData() {
        Preferences.instance?.user?.run {
            val body = RequestParameters.forGetAllEmployeeCovidComplianceForms(uID)
            viewModel.getAllEmployeeCovidComplianceForms(body = body)
        }
    }


}