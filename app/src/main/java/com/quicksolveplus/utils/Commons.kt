package com.quicksolveplus.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.ContentUris
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.Telephony
import android.text.format.DateFormat
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.quicksolveplus.dialogs.QSProgress
import com.quicksolveplus.qspmobile.R
import java.io.*
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

/**
 * 17/03/23.
 *
 * @author hardkgosai.
 */

/**
 * Global level toast.
 */
fun toast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

private var qsProgress: QSProgress? = null

fun showQSProgress(activity: Activity) {
    if (activity.isFinishing || activity.isDestroyed || qsProgress != null) return
    qsProgress = QSProgress(activity)
    qsProgress?.show()
}

fun dismissQSProgress() {
    try {
        qsProgress?.dismiss()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    qsProgress = null
}

fun showAlertDialog(
    context: Context,
    title: String = "",
    message: String = "",
    positiveButtonText: String? = null,
    negativeButtonText: String? = null,
    cancelable: Boolean = true,
    onClick: (isPositive: Boolean) -> Unit = {}
) {
    val alertDialog = AlertDialog.Builder(context)
    alertDialog.setTitle(title)
    alertDialog.setMessage(message)
    alertDialog.setCancelable(cancelable)
    if (positiveButtonText != null) {
        alertDialog.setPositiveButton(positiveButtonText) { dialog, _ ->
            dialog.dismiss()
            onClick(true)
        }
    }
    if (negativeButtonText != null) {
        alertDialog.setNegativeButton(negativeButtonText) { dialog, _ ->
            dialog.dismiss()
            onClick(false)
        }
    }
    alertDialog.show()
}

fun removeDelimiters(value: String): String {
    return value.replace("[-+.^:,() ]".toRegex(), "")
}

fun trimFloatValue(value: Double): String {
    return try {
        val format = DecimalFormat("##.##", DecimalFormatSymbols(Locale.ENGLISH))
        format.format(value)
    } catch (_: Exception) {
        ""
    }
}

fun getPath(activity: Activity?, uri: Uri): String? {
    try {
        if (DocumentsContract.isDocumentUri(activity, uri)) {
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), id.toLong()
                )
                return getDataColumn(activity!!, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                when (type) {
                    "image" -> {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    }
                    "video" -> {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    }
                    "audio" -> {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])
                return getDataColumn(activity!!, contentUri, selection, selectionArgs)
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            return if (isGooglePhotosUri(uri)) uri.lastPathSegment else getDataColumn(
                activity!!, uri, null, null
            )
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
    return null
}


fun isExternalStorageDocument(uri: Uri): Boolean {
    return "com.android.externalstorage.documents" == uri.authority
}

fun isDownloadsDocument(uri: Uri): Boolean {
    return "com.android.providers.downloads.documents" == uri.authority
}

fun isMediaDocument(uri: Uri): Boolean {
    return "com.android.providers.media.documents" == uri.authority
}

fun isGooglePhotosUri(uri: Uri): Boolean {
    return "com.google.android.apps.photos.content" == uri.authority
}

fun getDataColumn(
    activity: Activity, uri: Uri?, selection: String?, selectionArgs: Array<String>?
): String? {
    var cursor: Cursor? = null
    val column = "_data"
    val projection = arrayOf(column)
    try {
        cursor = activity.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
        if (cursor != null && cursor.moveToFirst()) {
            val index = cursor.getColumnIndexOrThrow(column)
            return cursor.getString(index)
        }
    } finally {
        cursor?.close()
    }
    return null
}

fun getImageUri(activity: Activity, inImage: Bitmap): Uri? {
    val bytes = ByteArrayOutputStream()
    inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
    val path = MediaStore.Images.Media.insertImage(activity.contentResolver, inImage, "Title", null)
    return Uri.parse(path)
}


fun fileConvertToBase64(filePath: String?): String? {
    var videoBase64: String? = ""
    try {
        val file = File(filePath!!)
        val bytes: ByteArray = readByte(file)
        val encodedVideo = Base64.encodeToString(bytes, Base64.DEFAULT)
        videoBase64 = encodedVideo
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
    return videoBase64
}

fun readByte(path: File): ByteArray {
    var bytes: ByteArray? = null
    try {
        val size = path.length().toInt()
        bytes = if (size / 1024 / 1024 <= 10) ByteArray(size) else ByteArray(1024 * 1024 * 10)
        try {
            val buf = BufferedInputStream(FileInputStream(path))
            buf.read(bytes, 0, bytes.size)
            buf.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bytes
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
    return bytes!!
}

fun openDatePickerDialog(activity: Activity, textView: AppCompatTextView, strDate: String) {
    try {
        var mYear: Int
        var mMonth: Int
        var mDay: Int
        val c = Calendar.getInstance()
        mYear = c[Calendar.YEAR]
        mMonth = c[Calendar.MONTH]
        mDay = c[Calendar.DAY_OF_MONTH]
        if (strDate != "") {
            val date = QSCalendar.formatDate(strDate, QSCalendar.DateFormats.MMDDYYYY.label)
            mMonth = Integer.parseInt(DateFormat.format("MM", date).toString()) - 1
            mDay = Integer.parseInt(DateFormat.format("dd", date).toString())
            mYear = Integer.parseInt(DateFormat.format("yyyy", date).toString())
        }

        val datePickerDialog = DatePickerDialog(
            activity, { view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->

                val date: String =
                    formatNumber(dayOfMonth) + "/" + formatNumber(monthOfYear + 1) + "/" + year
                Log.e("TAG", "openDatePickerDialog: $date")
                textView.text = QSCalendar.formatDate(
                    date,
                    QSCalendar.DateFormats.MMDDYYYY.label,
                    QSCalendar.DateFormats.MMDDYYYY.label
                )
            }, mYear, mMonth, mDay
        )
        datePickerDialog.show()
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
}

fun openTimePickerDialog(activity: Activity, textView: AppCompatTextView, strdate: String?) {
    var hour = 0
    var minute = 0
    try {
        val c = Calendar.getInstance()
        if (strdate != null && strdate.trim { it <= ' ' }.isNotEmpty()) {
            val date = QSCalendar.formatDate(strdate, QSCalendar.DateFormats.HHMMA.label)
            if (date != null) {
                c.time = date
                hour = c[Calendar.HOUR_OF_DAY]
                minute = c[Calendar.MINUTE]
            }
        } else {
            hour = c[Calendar.HOUR_OF_DAY]
            minute = c[Calendar.MINUTE]
        }
        val timePickerDialog = TimePickerDialog(
            activity, { view: TimePicker?, selectedHour: Int, selectedMinute: Int ->
                hour = selectedHour
                minute = selectedMinute
                val timeSet: String
                if (hour > 12) {
                    hour -= 12
                    timeSet = "PM"
                } else if (hour == 0) {
                    hour += 12
                    timeSet = "AM"
                } else if (hour == 12) timeSet = "PM" else timeSet = "AM"
                val minutes: String = if (minute < 10) "0$minute" else minute.toString()
                val aTime: String = "$hour:$minutes $timeSet"
                textView.text = aTime
            }, hour, minute, false
        )
        val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.dialog_title, null)
        timePickerDialog.setCustomTitle(view)
        val tvTitle = view.findViewById<AppCompatTextView>(R.id.tv_title)
        tvTitle.text = activity.resources.getString(R.string.str_select_time)
        timePickerDialog.show()
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
}

fun formatNumber(c: Int): String {
    return if (c >= 10) c.toString() else "0$c"
}

fun canAccessLocation(context: Context): Boolean {
    return hasPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) && hasPermission(
        context, Manifest.permission.ACCESS_COARSE_LOCATION
    )
}

fun hasPermission(context: Context, perm: String): Boolean {
    return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(context, perm)
}

fun createLocationAddressFromParameters(parameters: Map<String, String?>?): String {
    val PARAMETER_DELIMITER_COMMA = ", "

    val paramtersAsString = StringBuilder()
    if (parameters != null) {
        var firstParameter = true
        for (parameterName in parameters.keys) {
            if (!firstParameter) {
                paramtersAsString.append(PARAMETER_DELIMITER_COMMA)
            }
            paramtersAsString.append(parameters[parameterName])
            firstParameter = false
        }
    }
    return paramtersAsString.toString()
}

fun openMapActivityFromAddress(activity: Activity, address: String) {
    if (address.isNotEmpty()) {
        val uri = String.format("geo:0,0?q=%s", address)
        if (checkPlayServices(activity)) {
            try {
                val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                mapIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                activity.startActivity(mapIntent)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    } else {
        Toast.makeText(
            activity,
            activity.resources.getString(R.string.str_location_not_available),
            Toast.LENGTH_SHORT
        ).show()
    }
}

fun checkPlayServices(activity: Activity): Boolean {
    var errorDialog: Dialog? = null
    val googleAPI = GoogleApiAvailability.getInstance()
    val resultCode = googleAPI.isGooglePlayServicesAvailable(activity)
    if (resultCode != ConnectionResult.SUCCESS) {
        if (googleAPI.isUserResolvableError(resultCode)) {
            if (errorDialog == null) {
                errorDialog = googleAPI.getErrorDialog(activity, resultCode, 9000)
                if (errorDialog != null) {
                    errorDialog.setOnCancelListener(DialogInterface.OnCancelListener { dialogInterface: DialogInterface? ->
                        errorDialog = null
                    })
                    errorDialog?.setOnDismissListener(DialogInterface.OnDismissListener { dialog: DialogInterface? ->
                        errorDialog = null
                    })
                    errorDialog?.show()
                }
            }
        } else {
            Toast.makeText(
                activity,
                activity.resources.getString(R.string.str_device_not_supported),
                Toast.LENGTH_LONG
            ).show()
        }
        return false
    }
    return true
}

fun dialPhoneNumber(activity: Activity, phoneNumber: String) {
    TedPermission.create().setPermissionListener(object : PermissionListener {
        override fun onPermissionGranted() {
            if (ContextCompat.checkSelfPermission(
                    activity, Manifest.permission.CALL_PHONE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val intent = Intent(Intent.ACTION_CALL)
                intent.data = Uri.parse("tel:$phoneNumber")
                activity.startActivity(intent)
            }
        }

        override fun onPermissionDenied(deniedPermissions: List<String>) {

        }
    }).setRationaleMessage(R.string.call_phone_message).setDeniedMessage(R.string.denied_message)
        .setGotoSettingButtonText(R.string.str_ok).setPermissions(Manifest.permission.CALL_PHONE)
        .check()
}

fun composeMessage(activity: Activity, phoneNumber: String) {
    val intent = Intent(Intent.ACTION_SENDTO)
    intent.data = Uri.parse("smsto:$phoneNumber")

    //added@hardkgosai
    val defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(activity)
    if (defaultSmsPackageName != null) {
        intent.setPackage(defaultSmsPackageName)
    }
    if (intent.resolveActivity(activity.packageManager) != null) {
        activity.startActivity(intent)
    }
}

fun sendEmail(activity: Activity, emailId: String = "") {
    if (emailId.isNotEmpty()) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "message/rfc822"
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(emailId))
        if (intent.resolveActivity(activity.packageManager) != null) {
            activity.startActivity(Intent.createChooser(intent, "Send Email"))
        }
    } else {
        Toast.makeText(
            activity,
            activity.resources.getString(R.string.reg_error_MSG_ENTER_MAIL),
            Toast.LENGTH_SHORT
        ).show()
    }
}

fun Context.isImageExistInCache(filename: String): Boolean {
    return File(cacheDir, filename).exists()
}

fun Any?.notNull(): Boolean {
    return this != null
}

@SuppressLint("ClickableViewAccessibility")
fun makeEditTextScrollable(editText: AppCompatEditText) {
    editText.setOnTouchListener { v: View, event: MotionEvent ->
        v.parent.requestDisallowInterceptTouchEvent(true)
        v.performClick()
        if (event.action and MotionEvent.ACTION_MASK == MotionEvent.ACTION_UP) {
            v.parent.requestDisallowInterceptTouchEvent(false)
        }
        false
    }
}