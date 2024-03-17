package com.quicksolveplus.api

import android.app.Application
import androidx.lifecycle.AndroidViewModel

abstract class QSViewModel(application: Application) : AndroidViewModel(application) {
    abstract val repository: QSRepo
}