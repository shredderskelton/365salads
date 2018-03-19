package com.kan.salads.activity.login

import android.content.Context
import android.content.Intent
import com.kan.salads.activity.ActivityArgs

class LoginActivityArgs : ActivityArgs {
    override fun intent(activity: Context): Intent {
        return Intent(activity, LoginActivity::class.java)
    }
}