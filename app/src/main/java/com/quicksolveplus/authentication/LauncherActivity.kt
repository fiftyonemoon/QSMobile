package com.quicksolveplus.authentication

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.quicksolveplus.qspmobile.databinding.ActivityLauncherBinding

/**
 * 16/03/23.
 *
 * @author hardkgosai.
 */
class LauncherActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLauncherBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLauncherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
    }

    private fun initUI() {
        handler.postDelayed(runnable, 3000)
    }

    private val handler = Handler(Looper.myLooper()!!)
    private val runnable = Runnable {
        navigateToLogin()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

}