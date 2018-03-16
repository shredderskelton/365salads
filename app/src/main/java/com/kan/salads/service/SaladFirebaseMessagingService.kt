package com.kan.salads.service

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class SaladFirebaseMessagingService : FirebaseMessagingService() {
    val TAG = "FirebaseMessaging"
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)
        if (remoteMessage == null) {
            Log.d(TAG, "Message was null ")
            return
        }
        val msg: RemoteMessage = remoteMessage
        Log.d(TAG, "From: ${msg.from}")

        Log.d(TAG, "From: ${msg.from}")
        Log.d(TAG, "To: ${msg.to}")
        Log.d(TAG, "Id: ${msg.messageId}")
        Log.d(TAG, "Type: ${msg.messageType}")
        Log.d(TAG, "SentAt: ${msg.sentTime}")
        Log.d(TAG, "TTL: ${msg.ttl}")
        Log.d(TAG, "DATA: ${msg.data}")
    }
}