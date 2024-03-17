package com.quicksolveplus.authentication

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.biometric.BiometricPrompt
import com.quicksolveplus.api.Api
import com.quicksolveplus.api.RequestParameters
import com.quicksolveplus.api.ResponseStatus
import com.quicksolveplus.authentication.dialogs.AddBiometricUser
import com.quicksolveplus.authentication.dialogs.RequestForgotPassword
import com.quicksolveplus.authentication.models.ForgotPassword
import com.quicksolveplus.authentication.models.User
import com.quicksolveplus.authentication.viewmodel.LoginViewModel
import com.quicksolveplus.companies.CompanyActivity
import com.quicksolveplus.companies.dialogs.SavedCompaniesDialog
import com.quicksolveplus.dashboard.DashboardActivity
import com.quicksolveplus.dialogs.QSAlert
import com.quicksolveplus.qsbase.Base
import com.quicksolveplus.qspmobile.QSMobileActivity
import com.quicksolveplus.qspmobile.R
import com.quicksolveplus.qspmobile.databinding.ActivityLoginBinding
import com.quicksolveplus.utils.*

/**
 * 16/03/23.
 *
 * @author hardkgosai.
 */
class LoginActivity : Base() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
        setObservers()
        updateUsername()
        updateCompany()
    }

    private fun initUI() {
        binding.loginPanel.tvLogin.setOnClickListener { emailPasswordLogin() }
        binding.loginPanel.tvFaceTouchId.setOnClickListener { proceedBiometric() }
        binding.loginPanel.tvForgotPassword.setOnClickListener { openRequestForgotPasswordDialog() }
        binding.loginPanel.tvCompany.setOnClickListener { proceedCompany() }
    }

    private fun updateUsername() {
        Preferences.instance?.let {
            binding.loginPanel.etUsername.setText(it.username)
            binding.loginPanel.cbRememberPassword.isChecked =
                it.username.isNullOrEmpty() == false
        }
    }

    private fun updateCompany() {
        binding.loginPanel.tvCompany.text =
            Preferences.instance?.defaultCompany?.companyAbbreviation ?: ""
    }

    private fun setObservers() {
        viewModel.getResponseStatus().observe(this) {
            when (it) {
                is ResponseStatus.Running -> {
                    showQSProgress(this)
                }
                is ResponseStatus.Success -> {
                    proceedResponse(it)
                    dismissQSProgress()
                }
                is ResponseStatus.Failed -> {
                    toast(this, it.msg)
                    dismissQSProgress()
                }
                else -> {
                    toast(this, "Something goes wrong")
                    dismissQSProgress()
                }
            }
        }
    }

    private fun proceedResponse(success: ResponseStatus.Success) {
        when (success.apiName) {
            Api.validateUserLogon -> {
                if (success.data is User) {
                    proceedLoginResponse(success.data)
                }
            }
            Api.forgotPassword -> {
                if (success.data is ForgotPassword) {
                    proceedForgotPasswordResponse(success.data)
                }
            }
        }
    }

    private fun proceedLoginResponse(user: User) {
        if (user.success == "Success") {
            //always comment this line while developing
            //initFirebaseCrashlytics()

            //save user info temporary
            saveUserInfo(user)
            //check qs permissions
            checkQSPermissions(user)
        } else {
            toast(this, user.message)
        }
    }

    private fun proceedForgotPasswordResponse(forgotPassword: ForgotPassword) {
        if (forgotPassword.statusCode == 200) {
            toast(context = this, message = getString(R.string.str_email_reset_password))
        } else {
            toast(context = this, message = forgotPassword.message)
        }
    }

    private fun proceedCompany() {
        if (Preferences.instance?.shortListedCompanies == null) {
            navigateToCompany()
        } else openCompanyDialog()
    }

    private fun proceedBiometric() {
        QSBiometric(this).show(title = getString(R.string.str_face_touch_id),
            description = getString(R.string.str_place_finger_scanner_identify),
            authenticationCallback = object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    loginWithBiometric()
                }
            })
    }

    private fun openCompanyDialog() {
        SavedCompaniesDialog(this) { qspMobileWebClient ->
            Preferences.instance?.defaultCompany = qspMobileWebClient
            updateCompany()
        }.show()
    }

    private fun openRequestForgotPasswordDialog() {
        RequestForgotPassword(this) { email ->
            val requestBody = RequestParameters.forForgotPassword(
                email = email, appType = getString(R.string.app_name)
            )
            viewModel.forgotPassword(requestBody = requestBody)
        }.show()
    }

    /*private fun initFirebaseCrashlytics() {
        FirebaseCrashlytics.getInstance()
            .setCustomKey("Username", binding.loginPanel.etUsername.text.toString())
        FirebaseCrashlytics.getInstance()
            .setCustomKey("Company", binding.loginPanel.tvCompany.text.toString())
    }*/

    private fun loginWithBiometric() {
        Preferences.instance?.run {
            val hasBiometricUser =
                !biometricUsername.isNullOrEmpty() && !biometricPassword.isNullOrEmpty()
            if (hasBiometricUser) {
                biometricLogin(
                    username = biometricUsername!!, password = biometricPassword!!, saveInfo = false
                )
            } else {
                openAddBiometricUserWarningDialog()
            }
        }
    }

    private fun openAddBiometricUserWarningDialog() {
        QSAlert(
            this,
            message = getString(R.string.str_msg_touch_id_err),
            positiveButtonText = getString(R.string.action_yes),
            negativeButtonText = getString(R.string.action_no)
        ) { isPositive ->
            if (isPositive) {
                openAddBiometricUserDialog()
            }
        }.show()
    }

    private fun openAddBiometricUserDialog() {
        AddBiometricUser(this@LoginActivity) { username, password -> //onOkClicked
            biometricLogin(username = username, password = password, saveInfo = true)
        }.show()
    }

    private fun openNoCompanySelectedDialog() {
        QSAlert(
            context = this,
            message = getString(R.string.str_msg_select_default_company_dialog),
            positiveButtonText = getString(R.string.str_ok)
        ).show()
    }

    private fun emailPasswordLogin() {
        val username = binding.loginPanel.etUsername.text.toString()
        val password = binding.loginPanel.etPassword.text.toString()
        login(username = username, password = password)
    }

    private fun biometricLogin(username: String, password: String, saveInfo: Boolean = false) {
        if (saveInfo) {
            binding.loginPanel.tvFaceTouchId.run {
                isSelected = true
                tag = username.plus(",").plus(password)
            }
        }
        login(username = username, password = password)
    }

    private fun login(username: String, password: String) {
        Validation(this, username = username, password = password).run {
            if (isUsernameValid() && isPasswordValid(password)) {

                if (Preferences.instance?.defaultCompany == null) {
                    openNoCompanySelectedDialog()
                } else {
                    val requestBody =
                        RequestParameters.forLogin(userId = username, password = password)
                    viewModel.validateUserLogon(requestBody = requestBody)
                }
            }
        }
    }

    private fun saveUserInfo(user: User) {
        //save user data
        Preferences.instance?.user = user
        //remember username for next login if checked
        rememberUsername()
        //this will check and save credentials if it's biometric login
        saveBiometricLoginInfo()
    }

    private fun checkQSPermissions(user: User) {
        if (QSPermissions.hasQSMessagesAccessPermission()) {
            //implement signalR hub connection for chat messages
        }

        if (user.canAccessQSMed || user.canAccessClock) {
            navigateToDashboard()
        } else {
            navigateToQSMobile()
        }
    }

    private fun rememberUsername() {
        Preferences.instance?.username = if (binding.loginPanel.cbRememberPassword.isChecked) {
            binding.loginPanel.etUsername.text.toString()
        } else ""
    }

    private fun saveBiometricLoginInfo() {
        binding.loginPanel.tvFaceTouchId.run {
            if (isSelected) {
                isSelected = false
                if (tag is String) {
                    tag.toString().run {
                        if (contains(",")) {
                            split(",").let {
                                Preferences.instance?.run {
                                    biometricUsername = it[0]
                                    biometricPassword = it[1]
                                }
                                toast(
                                    context = this@LoginActivity,
                                    message = getString(R.string.touch_id_success_attached)
                                )
                            }
                        }
                    }
                }
                tag = null
            }
        }
    }

    private fun navigateToDashboard() {
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        finish()
    }

    private fun navigateToQSMobile() {
        val intent = Intent(this, QSMobileActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        finish()
    }

    private fun navigateToCompany() {
        val intent = Intent(this, CompanyActivity::class.java)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        startActivity(intent)
    }
}