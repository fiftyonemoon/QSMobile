package com.quicksolveplus.covid.covid_result.test_result

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.quicksolveplus.api.Api
import com.quicksolveplus.api.RequestParameters
import com.quicksolveplus.api.ResponseStatus
import com.quicksolveplus.covid.covid_result.test_result.adapter.TestResultAdapter
import com.quicksolveplus.covid.covid_result.test_result.models.TestingResult
import com.quicksolveplus.covid.covid_result.test_result.viewmodel.TestResultViewModel
import com.quicksolveplus.modifiers.Actifiers.openActivity
import com.quicksolveplus.qspmobile.R
import com.quicksolveplus.qspmobile.databinding.ActivityTestingResultBinding
import com.quicksolveplus.utils.Preferences
import com.quicksolveplus.utils.dismissQSProgress
import com.quicksolveplus.utils.showQSProgress
import com.quicksolveplus.utils.toast

class TestingResultActivity : AppCompatActivity() {

    private lateinit var binding : ActivityTestingResultBinding
    private val viewModel: TestResultViewModel by viewModels()
    private var testResultAdapter: TestResultAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestingResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
        setObservers()
        getTestResultData()
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


    private fun getTestResultData() {
        Preferences.instance?.user?.run {
            val body = RequestParameters.forGetEmployeeTestingResults(uID)
            viewModel.forGetEmployeeTestingResults(body = body)
        }
    }

    private fun initUI() {
        binding.apply {
            toolBar.tvTitle.setText(R.string.str_test_result)
            binding.toolBar.ivBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        }
    }


    private fun proceedResponse(success: ResponseStatus.Success) {
        when (success.apiName) {
            Api.getEmployeeTestingResults -> {
                if (success.data is TestingResult) {
                    Log.e("ApiResponse", "proceedResponse: "+success.data )
                    setTestResultAdapter(success.data)
                }
            }

        }
    }

    private fun setTestResultAdapter(testingResult: TestingResult) {
        Log.e("TAG", "setTestResultAdapter: "+testingResult.size)
        testResultAdapter = TestResultAdapter(this, testingResult){ position ->
            openActivity(TestingResultDetailsActivity::class.java)
        }
        binding.rvTestingResult.adapter = testResultAdapter
    }



}