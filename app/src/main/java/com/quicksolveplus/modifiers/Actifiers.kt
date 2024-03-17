package com.quicksolveplus.modifiers

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ListView
import android.widget.PopupWindow
import androidx.activity.addCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.quicksolveplus.qspmobile.R
import com.quicksolveplus.qspmobile.adapter.DrawerAdapter
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

/**
 * 28/02/23.
 *
 * @author hardkgosai.
 */
object Actifiers {

    fun AppCompatActivity.registerActivityResultLauncher(result: (activityResult: ActivityResult) -> Unit): ActivityResultLauncher<Intent> {
        return registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result(it)
        }
    }

    fun Fragment.registerActivityResultLauncher(result: (activityResult: ActivityResult) -> Unit): ActivityResultLauncher<Intent> {
        return registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result(it)
        }
    }

    val Activity.cameraIntent: Intent by lazy {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    }

    val Activity.imageIntent: Intent by lazy {
        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            MediaStore.ACTION_PICK_IMAGES
        } else {
            Intent.ACTION_PICK
        }
        Intent(flag).apply {
            setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        }
    }

    val Activity.videoIntent: Intent by lazy {
        Intent(Intent.ACTION_PICK).apply {
            setDataAndType(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, "video/*")
        }
    }

    val Activity.audioIntent: Intent by lazy {
        Intent(Intent.ACTION_PICK).apply {
            setDataAndType(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, "audio/*")
        }
    }

    fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    fun Activity.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun Activity.openKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    fun Activity.openKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(view.findFocus(), 0)
    }

    fun Activity.makeIntent(it: Class<*>, extras: Bundle.() -> Unit = {}): Intent {
        val intent = Intent(this, it)
        intent.putExtras(Bundle().apply(extras))
        return intent
    }

    fun Activity.openActivity(it: Class<*>, extras: Bundle.() -> Unit = {}) {
        val intent = Intent(this, it)
        intent.putExtras(Bundle().apply(extras))
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    fun Activity.openActivityWithClearTop(it: Class<*>, extras: Bundle.() -> Unit = {}) {
        val intent = Intent(this, it)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.putExtras(Bundle().apply(extras))
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    fun Activity.openActivityForResult(
        it: Class<*>, launcher: ActivityResultLauncher<Intent>, extras: Bundle.() -> Unit = {}
    ) {
        val intent = Intent(this, it)
        intent.putExtras(Bundle().apply(extras))
        launcher.launch(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    fun AppCompatActivity.replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, fragment)
        transaction.commit()
    }

    fun Activity.saveBitmapToCached(filename: String, bitmap: Bitmap) {
        try {
            val fos = FileOutputStream("$cacheDir/$filename")
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos)
            fos.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun AppCompatActivity.onActivityBackPressed(onBackPressed: () -> Unit) {
        onBackPressedDispatcher.addCallback {
            if (this.isEnabled) {
                onBackPressed()
            }
        }
    }

    fun Activity.getPopupWindow(
        drawerItems: ArrayList<String>, listener: AdapterView.OnItemClickListener
    ): PopupWindow {
        val drawerAdapter = DrawerAdapter(this, drawerItems)
        val popupWindow = PopupWindow(this)
        val inflater = getSystemService(AppCompatActivity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = inflater.inflate(R.layout.drawer_layout, null)

        val drawer = view.findViewById<ListView>(R.id.lv_drawer)
        drawer.adapter = drawerAdapter
        drawer.onItemClickListener = listener

        popupWindow.isFocusable = true
        popupWindow.width = Toolbar.LayoutParams.WRAP_CONTENT
        popupWindow.height = Toolbar.LayoutParams.WRAP_CONTENT
        popupWindow.setBackgroundDrawable(
            ContextCompat.getDrawable(this, R.drawable.rectangle_white_with_border)
        )
        popupWindow.contentView = view
        return popupWindow
    }
}