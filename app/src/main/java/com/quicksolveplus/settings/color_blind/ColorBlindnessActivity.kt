package com.quicksolveplus.settings.color_blind

import android.os.Bundle
import com.quicksolveplus.qsbase.Base
import com.quicksolveplus.qspmobile.R
import com.quicksolveplus.qspmobile.databinding.ActivityColorBlindnessBinding

class ColorBlindnessActivity : Base() {

    private lateinit var binding: ActivityColorBlindnessBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityColorBlindnessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
    }

    private fun initUI() {
        setToolbarUI()
        binding.apply {
            toolBar.ivBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        }
    }

    private fun setToolbarUI() {
        binding.toolBar.tvTitle.text = getString(R.string.str_setting)
    }

}