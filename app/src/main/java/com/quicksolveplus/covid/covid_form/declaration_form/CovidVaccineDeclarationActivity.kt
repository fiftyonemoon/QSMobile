package com.quicksolveplus.covid.covid_form.declaration_form

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.quicksolveplus.covid.covid_form.declaration_form.fragments.CovidVaccineFragmentOne
import com.quicksolveplus.qspmobile.R
import com.quicksolveplus.qspmobile.databinding.ActivityCovidVaccineDeclarationBinding

class CovidVaccineDeclarationActivity : AppCompatActivity() {

    lateinit var binding: ActivityCovidVaccineDeclarationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCovidVaccineDeclarationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
    }

    private fun initUI() {
        binding.toolBar.tvTitle.text = getString(R.string.str_covid_vaccine_declaration_form)
        binding.toolBar.ivBack.isVisible = false
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val fragmentOne = CovidVaccineFragmentOne()
        fragmentTransaction.add(R.id.flDeclaration, fragmentOne, fragmentOne.javaClass.name)
        fragmentTransaction.commit()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount != 0) {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    fun setResult() {
        val returnIntent = Intent()
        setResult(RESULT_OK, returnIntent)
        finish()
    }

}