package com.quicksolveplus.covid.covid_form

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.quicksolveplus.covid.covid_form.declaration_form.CovidVaccineDeclarationActivity
import com.quicksolveplus.modifiers.Actifiers.openActivity
import com.quicksolveplus.qspmobile.R
import com.quicksolveplus.qspmobile.databinding.ActivityCovidFormBinding

class CovidFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCovidFormBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCovidFormBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
    }

    private fun initUI() {

        binding.clVaccineDeclaration.setOnClickListener {
            openActivity(CovidVaccineDeclarationActivity::class.java)
        }
        binding.toolBar.apply {
            tvTitle.text = getString(R.string.str_covid_tracking)
            ivBack.isVisible = true
            ivBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }


    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount != 0) {
            onBackPressedDispatcher.onBackPressed()
        }
    }

}