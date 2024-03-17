package com.quicksolveplus.qspmobile.dashboard.pos

import android.os.Bundle
import android.view.Gravity
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.quicksolveplus.api.Api
import com.quicksolveplus.api.RequestParameters
import com.quicksolveplus.api.ResponseStatus
import com.quicksolveplus.dialogs.QSFilterItems
import com.quicksolveplus.dialogs.QSMonthYearPicker
import com.quicksolveplus.qspmobile.R
import com.quicksolveplus.qspmobile.clients.models.ClientsItem
import com.quicksolveplus.qspmobile.dashboard.pos.models.ClientPOS
import com.quicksolveplus.qspmobile.dashboard.pos.viewmodel.ClientPOSViewModel
import com.quicksolveplus.qspmobile.databinding.ActivityPosViewBinding
import com.quicksolveplus.utils.*

/**
 * 10/04/23.
 *
 * @author hardkgosai.
 */
class POSViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPosViewBinding
    private val viewModel: ClientPOSViewModel by viewModels()
    private var client: ClientsItem? = null

    private var selectedMonth = -1
    private var selectedYear = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPosViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getIntentData()
        initUI()
        initMonthYearPicker()
        setObservers()
        fetchData()
    }

    private fun getIntentData() {
        intent.extras?.run {
            if (containsKey(Constants.client_map)) {
                client = Gson().fromJson(
                    getString(Constants.client_map), object : TypeToken<ClientsItem?>() {}.type
                )
            }
        }
    }

    private fun initUI() {
        binding.toolbar.ivBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.tvPOSType.setOnClickListener { openPOSTypeDialog() }

        client?.run {
            binding.toolbar.tvTitle.text = String.format("%s %s", FirstName, LastName)
            if (ClientServiceTypes.isNotEmpty()) {
                val type = ClientServiceTypes[0].ServiceType
                binding.tvPOSType.text = type
                binding.tvSelectedPOSType.text = type
            }
        }
    }

    private fun initMonthYearPicker() {
        val qsMonthYearPicker = QSMonthYearPicker(this) { monthName, month, year ->
            selectedMonth = month
            selectedYear = year
            binding.tvMonth.text = monthName
            binding.tvYear.text = year.toString()
            fetchData()
        }

        selectedMonth = qsMonthYearPicker.getSelectedMonth()
        selectedYear = qsMonthYearPicker.getSelectedYear()
        binding.tvMonth.text = qsMonthYearPicker.getSelectedMonthName()
        binding.tvYear.text = qsMonthYearPicker.getSelectedYear().toString()

        binding.tvMonth.setOnClickListener { qsMonthYearPicker.show() }
        binding.tvYear.setOnClickListener { qsMonthYearPicker.show() }
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
                    if (it.msg == getString(R.string.msg_server_error)) {
                        toast(this, it.msg)
                    }
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
            Api.getClientPOS -> {
                if (success.data is ClientPOS) {
                    setData(success.data)
                }
            }
        }
    }

    private fun fetchData() {
        val body = RequestParameters.forClientPOS(
            clientUID = client?.ClientUID,
            dMonth = selectedMonth + 1,
            dYear = selectedYear,
            POSType = binding.tvPOSType.text.toString()
        )

        viewModel.getClientPOS(body)
    }

    private fun setData(clientPOS: ClientPOS) {
        clientPOS.map {
            binding.tvPOS.text = if (it.pOS != null) trimFloatValue(it.pOS.toDouble()) else "-"
            binding.tvSchedule.text =
                if (it.sHrsScheduled != null) trimFloatValue(it.sHrsScheduled.toDouble()) else "-"
            binding.tvTotal.text = if (it.schedTotal != null) String.format(
                "%s %%", trimFloatValue(it.schedTotal.toDouble())
            ) else "-"
            binding.tvWorked.text =
                if (it.sHrsWorked != null) trimFloatValue(it.sHrsWorked.toDouble()) else "-"
            binding.tvNotWorked.text =
                if (it.sHrsNotWorked != null) trimFloatValue(it.sHrsNotWorked.toDouble()) else "-"
            binding.tvTotalPercentage.text = if (it.totalPercent != null) String.format(
                "%s %%", trimFloatValue(it.totalPercent.toDouble())
            ) else "-"
        }
    }

    private fun openPOSTypeDialog() {
        val items = arrayListOf<String>()
        client?.ClientServiceTypes?.map {
            items.add(it.ServiceType)
        }
        QSFilterItems(
            context = this,
            title = getString(R.string.str_select_pos_type),
            items = items,
            itemsTextGravity = Gravity.CENTER,
        ) {
            val type = items[it]
            binding.tvPOSType.text = type
            binding.tvSelectedPOSType.text = type
            fetchData()
        }.show()
    }
}