package com.quicksolveplus.qspmobile.schedule.dialogs

import android.app.Dialog
import android.graphics.BitmapFactory

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.quicksolveplus.api.RequestParameters
import com.quicksolveplus.api.ResponseStatus
import com.quicksolveplus.modifiers.Actifiers.saveBitmapToCached
import com.quicksolveplus.modifiers.Glidifiers.loadGlide
import com.quicksolveplus.qspmobile.R
import com.quicksolveplus.qspmobile.databinding.DialogLinkedClientsBinding
import com.quicksolveplus.qspmobile.schedule.models.LinkedSchedulesItem
import com.quicksolveplus.qspmobile.schedule.viewmodel.LinkedClientViewModel
import okhttp3.ResponseBody
import java.io.File

/**
 * 06/04/23.
 *
 * @author hardkgosai.
 */
class LinkedClients(
    private val activity: AppCompatActivity,
    private val items: List<LinkedSchedulesItem> = arrayListOf()
) : Dialog(activity) {

    private lateinit var binding: DialogLinkedClientsBinding
    private val viewModel: LinkedClientViewModel by lazy {
        ViewModelProvider(activity)[LinkedClientViewModel::class.java]
    }
    private var adapter: ArrayAdapter<LinkedSchedulesItem>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.dialog_insets))

        binding = DialogLinkedClientsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
        setObserver()
        setListView()
    }

    private fun setObserver() {
        viewModel.getResponseStatus().observe(activity) {
            when (it) {
                is ResponseStatus.Success -> {
                    if (isShowing && it.data is ResponseBody) {
                        if (it.other is String) {
                            val array = it.data.bytes()
                            val bitmap = BitmapFactory.decodeByteArray(array, 0, array.size)
                            activity.saveBitmapToCached(it.other, bitmap)
                            adapter?.notifyDataSetChanged()
                        }
                    }
                }
                else -> {}
            }
        }
    }

    private fun initUI() {
        binding.tvOk.setOnClickListener { dismiss() }
    }

    private fun setListView() {
        adapter = object : ArrayAdapter<LinkedSchedulesItem>(
            /* context = */ context,
            /* resource = */ R.layout.layout_item_linked_clients,
            /* textViewResourceId = */ R.id.tvTitle,
            /* objects = */ items
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val textview = view.findViewById<TextView>(R.id.tvTitle)
                val ivProgress = view.findViewById<ProgressBar>(R.id.ivProgress)
                val ivProfile = view.findViewById<ImageView>(R.id.ivProfile)
                val item = items[position]
                textview.text = String.format(
                    "%s %s, %s",
                    item.linkedClientFirstName?.trim(),
                    item.linkedClientLastName?.trim(),
                    item.linkedTaskName?.trim()
                )

                val filename = item.linkedClientProfilePic ?: ""
                val file = File(context.cacheDir, filename)

                if (file.exists() || filename.isEmpty()) {

                    ivProgress.isVisible = false
                    ivProfile.loadGlide(
                        file, ContextCompat.getDrawable(context, R.drawable.ic_avatar)
                    )

                } else {

                    ivProgress.isVisible = true
                    ivProfile.loadGlide(R.drawable.ic_avatar)
                    val body = RequestParameters.forProfilePicture(photo = filename)
                    viewModel.getClientProfilePic(body = body, filename = filename)
                }

                return view
            }
        }

        binding.lv.adapter = adapter
    }
}