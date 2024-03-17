package com.quicksolveplus.qsbase

import android.content.Context

import androidx.appcompat.app.AppCompatActivity
import com.quicksolveplus.languist.Linguist

/**
 * 17/03/23.
 *
 * @author hardkgosai.
 */
abstract class Base : AppCompatActivity() {

    /**
     * Keep this for language change.
     */
    override fun attachBaseContext(newBase: Context?) {
        val context = Linguist.wrap(newBase)
        super.attachBaseContext(context)
    }

}