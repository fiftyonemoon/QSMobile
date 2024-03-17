package com.quicksolveplus.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import androidx.core.content.ContextCompat
import com.quicksolveplus.qspmobile.R
import com.quicksolveplus.qspmobile.databinding.DialogProgressBinding

/**
 * 17/03/23.
 * Progress Dialog.
 *
 * @author hardkgosai.
 */
class QSProgress(context: Context) : Dialog(context) {

    private lateinit var binding: DialogProgressBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.dialog_insets))

        binding = DialogProgressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setCancelable(false)
    }
}