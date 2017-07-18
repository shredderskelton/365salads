package com.kan.salads

import android.content.Intent
import android.os.Bundle
import com.google.firebase.auth.TwitterAuthProvider
import com.kan.salads.activity.login.LoginFacebookActivity
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.TwitterSession
import com.twitter.sdk.android.core.identity.TwitterAuthClient


abstract class LoginTwitterActivity : LoginFacebookActivity() {
    private lateinit var mTwitterAuthClient: TwitterAuthClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mTwitterAuthClient = TwitterAuthClient()
    }

    fun onTwitterLoginClicked() {
        mTwitterAuthClient.authorize(this, object : com.twitter.sdk.android.core.Callback<TwitterSession>() {

            override fun success(twitterSessionResult: Result<TwitterSession>) {
                val session = twitterSessionResult.data
                val credential = TwitterAuthProvider.getCredential(session.authToken.token, session.authToken.secret)
                authoriseWithFirebase(credential)
            }

            override fun failure(e: TwitterException) {
                onError(e.localizedMessage)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, responseCode: Int, intent: Intent) {
        super.onActivityResult(requestCode, responseCode, intent)
        mTwitterAuthClient.onActivityResult(requestCode, responseCode, intent)
    }
}