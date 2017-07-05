package com.kan.salads

import android.app.Application
import com.twitter.sdk.android.core.Twitter


class SaladsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Twitter.initialize(this)
    }
}
