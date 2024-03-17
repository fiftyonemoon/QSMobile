package com.quicksolveplus.announcements

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.view.View.VISIBLE
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.gson.Gson
import com.quicksolveplus.announcements.adapter.AnnouncementAdapter
import com.quicksolveplus.announcements.viewmodel.AnnouncementViewModel
import com.quicksolveplus.api.Api
import com.quicksolveplus.api.RequestParameters
import com.quicksolveplus.api.ResponseStatus
import com.quicksolveplus.dashboard.models.Announcements
import com.quicksolveplus.qsbase.Base
import com.quicksolveplus.qspmobile.R
import com.quicksolveplus.qspmobile.databinding.ActivityAnnouncementsBinding
import com.quicksolveplus.utils.*

/**
 * 17/03/23.
 * @author Bhavin
 */

class AnnouncementsActivity : Base() {

    private lateinit var binding: ActivityAnnouncementsBinding
    private var announcementAdapter: AnnouncementAdapter? = null
    private val viewModel: AnnouncementViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnnouncementsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
        setObservers()
        getAnnouncementsData()
    }


    private fun setObservers() {
        viewModel.responseStatus().observe(this) {
            when (it) {
                is ResponseStatus.Running -> {
                    showQSProgress(this)
                }
                is ResponseStatus.Success -> {
                    Log.e("TAG", "setObservers: $it")
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
            Api.getEmployeeAnnouncements -> {
                if (success.data is Announcements) {
                    setAnnouncementAdapter(success.data)
                }
            }
        }
    }


    private fun initUI() {

        binding.apply {
            toolBar.tvTitle.text = getString(R.string.str_announcements)
            toolBar.ivBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
            toolBar.ivSearch.isVisible = true
            toolBar.ivFilter.isVisible = true

            toolBar.ivBack.setOnClickListener(onClickListener)
            toolBar.ivSearch.setOnClickListener(onClickListener)
            toolBar.ivFilter.setOnClickListener(onClickListener)
        }
    }

    private fun getAnnouncementsData() {
        Preferences.instance?.user?.run {
            val body = RequestParameters.forGetEmployeeAnnouncements(uID)
            viewModel.getEmployeeAnnouncements(body = body)
        }
    }

    private var onClickListener = OnClickListener {
        when (it.id) {
            R.id.ivBack -> {
                onBackPressedDispatcher.onBackPressed()
            }
            R.id.ivSearch -> {
                searchAnnouncements()
            }
            R.id.ivFilter -> {
//                DialogUtils().openFilterDialog(this, object : DialogUtils.FilterDialogInterface {
//                    override fun onOk() {
//
//                    }
//
//                })
            }
        }
    }

    private fun searchAnnouncements() {
        if (binding.clSearch.visibility == VISIBLE) {
            binding.clSearch.visibility = View.GONE
        } else {
            binding.clSearch.visibility = VISIBLE
        }
        binding.etAnnouncementSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) { announcementAdapter!!.filter(s.toString()) }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {  }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {  }
        })
    }

    private fun setAnnouncementAdapter(announcements: Announcements) {
        announcements.reverse()
        announcementAdapter = AnnouncementAdapter(this, announcements) { position ->
            val announcementData = Gson().toJson(announcements[position])
            val intent = Intent(this@AnnouncementsActivity, AnnouncementDetailsActivity::class.java)
            intent.putExtra(Constants.announcement_data, announcementData)
            startActivity(intent)
        }
        binding.rvAnnouncements.adapter = announcementAdapter
    }


}