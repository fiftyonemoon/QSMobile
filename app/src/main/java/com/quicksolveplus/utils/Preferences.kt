package com.quicksolveplus.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.quicksolveplus.api.Api
import com.quicksolveplus.authentication.models.User
import com.quicksolveplus.companies.models.QSPMobileWebClient
import com.quicksolveplus.qspmobile.R

/**
 * 16/03/23.
 * To store temp data.
 *
 * Optimizing the old project so,
 * keep the preference's account name and it's data key same as old one.
 *
 * Must initialize on application level [com.quicksolveplus.QSMobile].
 *
 * @author hardkgosai.
 */
class Preferences(context: Context) {

    private val baseUrlKey = "baseURL"
    private val deviceTokenKey = "deviceToken"
    private val shortListedCompaniesKey = "web_clients_list"
    private val defaultCompanyKey = "selected_default_company"
    private val userKey = "user_map"
    private val appLanguageKey = "AppSelectedLanguage"
    private val latitudeKey = "Latitude"
    private val longitudeKey = "Longitude"

    //both username and biometricUsername key is same but preference account is different.
    private val usernameKey = "userId"
    private val biometricUsernameKey = "userId"
    private val biometricPasswordKey = "password"

    private var qsPreferences: SharedPreferences? = null
    private var devicePreferences: SharedPreferences? = null
    private var qsEditor: SharedPreferences.Editor? = null
    private var deviceEditor: SharedPreferences.Editor? = null

    companion object {
        var instance: Preferences? = null
    }

    init {
        instance = this
        qsPreferences =
            context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)
        devicePreferences = context.getSharedPreferences(
            context.getString(R.string.shared_pref_devicetoken), Context.MODE_PRIVATE
        )
        qsEditor = qsPreferences?.edit()
        deviceEditor = devicePreferences?.edit()
    }

    var baseUrl: String?
        get() {
            defaultCompany?.run {
                return "https://$companyURL"
            }
            return qsPreferences?.getString(baseUrlKey, Api.defaultSever)
        }
        set(value) {
            qsEditor?.putString(baseUrlKey, value)?.apply()
        }

    var username: String?
        get() = qsPreferences?.getString(usernameKey, "")
        set(value) {
            qsEditor?.putString(usernameKey, value)?.apply()
        }

    var appLanguage: String?
        get() = qsPreferences?.getString(appLanguageKey, "en")
        set(value) {
            qsEditor?.putString(appLanguageKey, value)?.apply()
        }

    var shortListedCompanies: ArrayList<QSPMobileWebClient>?
        get() {
            val companies = qsPreferences?.getString(shortListedCompaniesKey, "")
            return Gson().fromJson(
                companies, object : TypeToken<ArrayList<QSPMobileWebClient?>?>() {}.type
            )
        }
        set(value) {
            qsEditor?.putString(shortListedCompaniesKey, Gson().toJson(value))?.apply()
        }

    var defaultCompany: QSPMobileWebClient?
        get() {
            val company = qsPreferences?.getString(defaultCompanyKey, "")
            return Gson().fromJson(company, object : TypeToken<QSPMobileWebClient?>() {}.type)
        }
        set(value) {
            qsEditor?.putString(defaultCompanyKey, Gson().toJson(value))?.apply()
        }

    var user: User?
        get() {
            val user = qsPreferences?.getString(userKey, "")
            return Gson().fromJson(user, object : TypeToken<User?>() {}.type)
        }
        set(value) {
            qsEditor?.putString(userKey, Gson().toJson(value))?.apply()
        }

    var latitude: String?
        get() = qsPreferences?.getString(latitudeKey, "")
        set(value) {
            qsEditor?.putString(latitudeKey, value)?.apply()
        }

    var longitude: String?
        get() = qsPreferences?.getString(longitudeKey, "")
        set(value) {
            qsEditor?.putString(longitudeKey, value)?.apply()
        }

    /*--------------------------------- Device's Preferences -------------------------------------*/

    var deviceToken: String?
        get() = devicePreferences?.getString(deviceTokenKey, null)
        set(value) {
            deviceEditor?.putString(deviceTokenKey, value)?.apply()
        }

    var biometricUsername: String?
        get() = devicePreferences?.getString(biometricUsernameKey, null)
        set(value) {
            deviceEditor?.putString(biometricUsernameKey, value)?.apply()
        }

    var biometricPassword: String?
        get() = devicePreferences?.getString(biometricPasswordKey, null)
        set(value) {
            deviceEditor?.putString(biometricPasswordKey, value)?.apply()
        }
}