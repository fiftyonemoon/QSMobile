package com.quicksolveplus.dialogs

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.quicksolveplus.modifiers.Glidifiers.loadGlide
import com.quicksolveplus.qspmobile.R
import com.quicksolveplus.qspmobile.databinding.DialogImageViewBinding
import com.quicksolveplus.utils.Constants
import java.io.File

class QSImageView : AppCompatActivity() {

    private lateinit var binding: DialogImageViewBinding
    private var clientProfilePic: String = ""
    private var documentPic: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window?.statusBarColor = ContextCompat.getColor(this, R.color.black)
        val windowInsetController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetController.isAppearanceLightStatusBars = false

        binding = DialogImageViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
    }

    private fun initUI() {
        if (intent.hasExtra(Constants.clientProfilePic)) {
            clientProfilePic = intent.getStringExtra(Constants.clientProfilePic).toString()
            val file = File(application.cacheDir, clientProfilePic)
            binding.ivProfile.loadGlide(
                file,
                ContextCompat.getDrawable(this@QSImageView, R.drawable.ic_avatar)
            )
        } else if (intent.hasExtra(Constants.covidVaccine)) {
            documentPic = intent.getStringExtra(Constants.covidVaccine).toString()
            val file = File(application.cacheDir, documentPic)
            binding.ivProfile.loadGlide(
                file,
                ContextCompat.getDrawable(this@QSImageView, R.drawable.ic_avatar)
            )
        }
    }
}