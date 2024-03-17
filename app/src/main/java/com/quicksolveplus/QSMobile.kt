package com.quicksolveplus

import android.app.Activity
import android.app.Application
import com.quicksolveplus.authentication.LoginActivity
import com.quicksolveplus.languist.Linguist
import com.quicksolveplus.languist.Translatable
import com.quicksolveplus.modifiers.Actifiers.openActivityWithClearTop
import com.quicksolveplus.utils.Preferences
import com.quicksolveplus.utils.QSLanguage

/**
 * 16/03/23.
 *
 * @author hardkgosai.
 */
class QSMobile : Application(), Translatable {

    private lateinit var linguist: Linguist

    companion object {
        var instance: QSMobile? = null
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        //clear cache first
        clearCacheDir()
        //initialize app preferences
        Preferences(this)
        //setup language after preference
        setupQSLanguage()
    }

    fun setupQSLanguage() {
        val languageCode = Preferences.instance?.appLanguage
        linguist = QSLanguage.initialize(this, languageCode)
    }

    //clear cache directory
    //if any profile picture is saved than remove it for new load
    private fun clearCacheDir() {
        val list = cacheDir.listFiles()
        list?.map {
            if (it.exists()) {
                it.delete()
            }
        }
    }

    private fun clearPreferences() {
        Preferences.instance?.run {
            user = null
        }
    }

    override fun getLinguist(): Linguist {
        return linguist
    }

    fun logout(activity: Activity) {
        clearCacheDir()
        clearPreferences()
        activity.openActivityWithClearTop(LoginActivity::class.java)
        activity.finish()
    }
}