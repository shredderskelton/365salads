package com.kan.salads

import android.app.Application
import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.twitter.sdk.android.core.Twitter


class SaladsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Twitter.initialize(this)
        val refreshedToken = FirebaseInstanceId.getInstance().token
        Log.d("Firebase", "Refreshed token: " + refreshedToken!!)
    }
}
