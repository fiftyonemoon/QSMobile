package com.quicksolveplus.fcm

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.quicksolveplus.utils.Preferences

/**
 * 20/03/23.
 *
 * Initialize [FirebaseMessagingService].
 * Generate new token and use while login [com.quicksolveplus.authentication.LoginActivity].
 * Also to receive notifications.
 *
 * @author hardkgosai.
 */
class FCM : FirebaseMessagingService() {

    override fun onCreate() {
        super.onCreate()
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Preferences.instance?.deviceToken = token
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
    }
}