package com.kan.salads

import android.app.Application
import com.google.firebase.iid.FirebaseInstanceId
import com.squareup.picasso.Picasso
import com.twitter.sdk.android.core.Twitter
import timber.log.Timber


class SaladsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initLogging()
        Twitter.initialize(this)
        val refreshedToken = FirebaseInstanceId.getInstance().token
        Timber.d("Firebase token: $refreshedToken")
        Picasso.with(this).apply {
            setIndicatorsEnabled(true)
            isLoggingEnabled = true
        }
    }

    private fun initLogging() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
