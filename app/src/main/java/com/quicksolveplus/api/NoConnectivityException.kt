package com.quicksolveplus.api

import java.io.IOException

class NoConnectivityException(private val exception: String = "") : IOException() {
    override val message: String
        get() = exception
}