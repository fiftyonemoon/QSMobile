package com.quicksolveplus.covid.covid_result

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.quicksolveplus.api.Api
import com.quicksolveplus.api.RequestParameters
import com.quicksolveplus.api.ResponseStatus
import com.quicksolveplus.covid.covid_form.CovidFormActivity
import com.quicksolveplus.covid.covid_result.adapter.SupportDocAdapter
import com.quicksolveplus.covid.covid_result.test_result.TestingResultActivity
import com.quicksolveplus.covid.covid_result.viewmodel.CovidResultViewModel
import com.quicksolveplus.covid.covid_tracking.models.CovidItem
import com.quicksolveplus.covid.covid_tracking.testing_site.CovidSiteActivity
import com.quicksolveplus.dialogs.QSAlert
import com.quicksolveplus.dialogs.QSImageView
import com.quicksolveplus.modifiers.Actifiers.openActivity
import com.quicksolveplus.modifiers.Actifiers.saveBitmapToCached
import com.quicksolveplus.qspmobile.R
import com.quicksolveplus.qspmobile.databinding.ActivityCovidTestResultBinding
import com.quicksolveplus.utils.*
import okhttp3.ResponseBody

class CovidTestResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCovidTestResultBinding
    private val viewModel: CovidResultViewModel by viewModels()
    private var vaccinationSupportDocAdapter: SupportDocAdapter? = null
    private var vaccineList: ArrayList<CovidItem> = arrayListOf()
    private var supportDocList: ArrayList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCovidTestResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
        setObservers()
        getCovidResultData()

    }

    private fun getCovidResultData() {
        Preferences.instance?.user?.run {
            val body = RequestParameters.forGetEmployeeCovidComplianceForms(uID)
            viewModel.getEmployeeCovidComplianceForms(body = body)
        }
    }

    private fun initUI() {
        binding.apply {
            toolBar.tvTitle.setText(R.string.str_covid_tracking)
            toolBar.ivRepeat.visibility = View.VISIBLE
            toolBar.ivTracking.visibility = View.VISIBLE
            toolBar.ivBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
            clTestReport.setOnClickListener { openActivity(TestingResultActivity::class.java) }
            toolBar.ivTracking.setOnClickListener { openActivity(CovidSiteActivity::class.java) }
            toolBar.ivRepeat.setOnClickListener { openRefreshVaccineAlert() }
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
            Api.getEmployeeCovidComplianceForms -> {
                if (success.data is CovidItem) {
                    setData(success.data)
                }
            }
            Api.getImageFile -> {
                if (success.data is ResponseBody) {
                    if (success.other is Pair<*, *>) {
                        val array = success.data.bytes()
                        val bitmap = BitmapFactory.decodeByteArray(array, 0, array.size)
                        val position = success.other.first as Int
                        val client = success.other.second as String
                        this.saveBitmapToCached(client, bitmap)
                        vaccinationSupportDocAdapter?.notifyItemChanged(position)
                    }
                }
            }
        }
    }

    private fun setData(covidData: CovidItem) {
        vaccineList.add(covidData)
        supportDocList = vaccineList[0].SupportingImageNames
        binding.tvType.text = covidData.ComplianceType
        binding.tvExemptionType.text = covidData.ExemptionType
        binding.rvSupportDocument.visibility = View.VISIBLE
        binding.apply {
            vaccinationSupportDocAdapter =
                SupportDocAdapter(this@CovidTestResultActivity, supportDocList, viewModel) { pos ->
                    val intent = Intent(this@CovidTestResultActivity, QSImageView::class.java)
                    intent.putExtra(Constants.covidVaccine,supportDocList[pos] )
                    startActivity(intent)
                }
            binding.rvSupportDocument.adapter = vaccinationSupportDocAdapter
        }
    }

    private fun openRefreshVaccineAlert() {
        QSAlert(
            context = this,
            title = null,
            message = getString(R.string.do_you_want_to_remove_existing_vaccination_data),
            positiveButtonText = getString(R.string.action_yes),
            negativeButtonText = getString(R.string.str_cancel),
            cancelable = false
        ) { isPositive ->
            if (isPositive) {
                val intent = Intent(this, CovidFormActivity::class.java)
                resultLauncher.launch(intent)
                finish()
            }
        }.show()
    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {

        }
    }
}

