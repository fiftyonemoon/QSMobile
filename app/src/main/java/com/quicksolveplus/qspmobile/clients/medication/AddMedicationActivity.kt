package com.quicksolveplus.qspmobile.clients.medication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.gson.Gson
import com.quicksolveplus.qspmobile.clients.medication.model.MedicationItem
import com.quicksolveplus.qspmobile.databinding.ActivityAddMedicationBinding
import com.quicksolveplus.utils.Constants

class AddMedicationActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddMedicationBinding
    var clientUID = 0
    var isEditMode = false
    private var medication: MedicationItem? = null
    private var medicationData = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMedicationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getData()
    }

    private fun getData() {
        if (intent.hasExtra(Constants.clientUID)) {
            clientUID = intent.getIntExtra(Constants.clientUID, 0)
        }
        if (intent.hasExtra(Constants.isEdit)) {
            isEditMode = intent.getBooleanExtra(Constants.isEdit, false)
        }
        if (intent.hasExtra(Constants.medicationData)) {
            val gson = Gson()
            medicationData = intent.getStringExtra(Constants.medicationData).toString()
            medication = gson.fromJson(
                medicationData, MedicationItem::class.java
            )
        }
    }
}