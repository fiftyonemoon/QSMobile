package com.quicksolveplus.dialogs

import android.app.AlertDialog

import android.content.Context
import androidx.core.content.ContextCompat
import com.quicksolveplus.qspmobile.R

/**
 * 24/03/23.
 * Image Picker Dialog.
 *
 * @author hardkgosai.
 */
class QSImagePicker(context: Context, private val onActionPick: (isCamera: Boolean) -> Unit) :
    AlertDialog.Builder(context) {

    init {
        val userOption = arrayOf<CharSequence>(
            context.getString(R.string.str_camera),
            context.getString(R.string.str_gallery),
            context.getString(R.string.str_cancel)
        )
        setTitle(context.getString(R.string.str_choose_image))
        setItems(userOption) { dialog, which ->
            if (which != 2) {
                onActionPick(which == 0)
            }
            dialog.dismiss()
        }
        setCancelable(false)
        val alertDialog = create()
        alertDialog.show()
        alertDialog.window?.setBackgroundDrawable(
            ContextCompat.getDrawable(
                context, R.drawable.dialog_insets
            )
        )
    }
}

