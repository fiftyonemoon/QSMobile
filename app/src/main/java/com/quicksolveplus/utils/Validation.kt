package com.quicksolveplus.utils

import android.app.Activity
import android.content.Context
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import com.quicksolveplus.qspmobile.R

/**
 * 17/03/23
 *
 * @author hardkgosai.
 */
class Validation(
    val context: Context,
    private val username: String? = null,
    private val email: String? = null,
    private val password: String? = null,
    private val phoneNumber: String? = null
) {

    fun isValueValid(value: String?): Boolean {
        if (value == null || value.trim().isEmpty()) {
            return false
        }
        return true
    }

    /**
     * @return true if username is valid else false.
     */
    fun isUsernameValid(name: String? = this.username): Boolean {
        if (name == null || name.trim().isEmpty()) {
            toast(context, "Please enter username")
            return false
        }
        return true
    }

    /**
     * @return true on email is valid else false.
     */
    fun isEmailValid(email: String? = this.email): Boolean {
        if (email == null || email.trim().isEmpty()) {
            toast(context = context, message = "Please enter email")
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            toast(context = context, message = "Please enter valid email")
            return false
        }
        return true
    }

    /**
     * @return true on password is valid else false.
     */
    fun isPasswordValid(password: String? = this.password): Boolean {
        if (password == null || password.trim().isEmpty()) {
            toast(context = context, message = "Please enter password")
            return false
        } /*else if (password.trim().length < 6) {
            toast(context = context, message = "Password must be greater than 6 digits")
            return false
        }*/ else if (password.trim().contains(" ")) {
            toast(context = context, message = "Password must not contain space")
            return false
        }
        return true
    }

    /**
     * @return true on phone number is valid else false.
     */
    fun isPhoneNumberValid(phoneNumber: String? = this.phoneNumber): Boolean {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            toast(context = context, message = "Please enter phone number")
            return false
        } else if (!Patterns.PHONE.matcher(phoneNumber.trim()).matches()) {
            toast(context = context, message = "Please enter valid phone number")
            return false
        }
        return true
    }

    /*------------------------------------------ Preeti -----------------------------------------*/

    fun phoneNumberValidation(activity: Activity?, phoneNumber: String): Boolean {
        val number: String = phoneNumber.replace("[-+.^:,() ]".toRegex(), "")
        if (number.isNotEmpty() && number.length != 10) {
            Toast.makeText(activity, R.string.error_MSG_ENTER_PHONE_NO, Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    /**
     * @return true if field is valid else false.
     */
    fun isFieldValid(value: String?, message: String? = ""): Boolean {
        if (value == null || value.trim().isEmpty()) {
            message?.let {
                toast(context, it)
            }
            return false
        }
        return true
    }

    fun isValidOptionId(value: Int?, message: String? = ""): Boolean {
        if (value == null || value == 0) {
            message?.let {
                toast(context, it)
            }
            return false
        }
        return true
    }

    fun isValidNextVisitDate(
        tvNextVisit: AppCompatTextView,
        nextFrom: AppCompatTextView,
        nextTo: AppCompatTextView
    ): Boolean {
        if (tvNextVisit.text.toString().isNotEmpty()) {
            return isFieldValid(
                nextFrom.text.toString(),
                context.getString(R.string.str_select_next_visit_start_time)
            ) && isFieldValid(
                nextTo.text.toString(),
                context.getString(R.string.str_select_next_visit_end_time)
            )

        }
        return true
    }

    fun isValidFromToDate(
        tvFrom: AppCompatTextView,
        tvTo: AppCompatTextView,
        tvNextFrom: AppCompatTextView,
        tvNextTo: AppCompatTextView
    ): Boolean {
        val dateTimeStart = QSCalendar.formatDate(
            tvFrom.text.toString(),
            QSCalendar.DateFormats.HHMMA.label
        )
        val dateTimeEnd = QSCalendar.formatDate(
            tvTo.text.toString(),
            QSCalendar.DateFormats.HHMMA.label
        )

        if (dateTimeEnd != null && dateTimeStart != null) {
            if (dateTimeEnd.time <= dateTimeStart.time) {
                toast(
                    context,
                    context.getString(R.string.str_visit_start_time_earlier_than_end_time)
                )
                return false
            }
            if (tvNextFrom.text.toString()
                    .trim { it <= ' ' }.isNotEmpty() && tvNextTo.text.toString()
                    .trim { it <= ' ' }.isNotEmpty()
            ) {
                val nextDateTimeStart = QSCalendar.formatDate(
                    tvNextFrom.text.toString(),
                    QSCalendar.DateFormats.HHMMA.label
                )
                val nextDateTimeEnd = QSCalendar.formatDate(
                    tvNextTo.text.toString(),
                    QSCalendar.DateFormats.HHMMA.label
                )

                //if (DateTimeEnd.getTime() <= DateTimeStart.getTime()) {
                if (nextDateTimeEnd != null && nextDateTimeStart != null && nextDateTimeEnd.time <= nextDateTimeStart.time) {
                    toast(
                        context,
                        context.getString(R.string.str_next_visit_start_time_earlier_than_next_visit_end_time)
                    )
                    return false
                }
            }
            return true
        }
        return false
    }
}