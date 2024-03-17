package com.quicksolveplus.announcements

import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.google.gson.Gson
import com.quicksolveplus.dashboard.models.AnnouncementsItem
import com.quicksolveplus.qsbase.Base
import com.quicksolveplus.qspmobile.R
import com.quicksolveplus.qspmobile.databinding.ActivityAnnouncementDetailsBinding
import com.quicksolveplus.utils.Constants

class AnnouncementDetailsActivity : Base() {

    private lateinit var binding: ActivityAnnouncementDetailsBinding
    private var announcement: AnnouncementsItem? = null
    private var announcementData = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnnouncementDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()

    }

    private fun initUI() {

        if (intent.hasExtra(Constants.announcement_data)) {
            val gson = Gson()
            announcementData = intent.getStringExtra(Constants.announcement_data).toString()
            announcement = gson.fromJson(announcementData, AnnouncementsItem::class.java)
        }

        binding.apply {
            toolBar.tvTitle.setText(R.string.str_announcements)
            toolBar.ivBack.setOnClickListener(onClickListener)

            val sent = getString(R.string.str_sent_by) + announcement?.senderName
            tvSubject.text = announcement?.announcementSubject
            tvSentBy.text = sent

            val announcementDescription =
                Html.fromHtml(announcement?.announcementMessage, HtmlCompat.FROM_HTML_MODE_LEGACY)
                    .toString()
            tvDescription.text = announcementDescription


            if (announcement?.isRead == true) {
                btnRead.visibility = View.VISIBLE
                btnAcknowledge.visibility = View.GONE
            } else {
                btnRead.visibility = View.GONE
                btnAcknowledge.visibility = View.VISIBLE
            }
        }

    }

    private var onClickListener = View.OnClickListener {
        when (it.id) {
            R.id.ivBack -> {
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }
}