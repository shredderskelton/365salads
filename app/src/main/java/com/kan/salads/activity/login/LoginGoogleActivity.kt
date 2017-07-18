package com.kan.salads.activity.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.GoogleAuthProvider
import com.kan.salads.LoginTwitterActivity
import com.kan.salads.R


abstract class LoginGoogleActivity : LoginTwitterActivity() {
    val RC_SIGN_IN = 9001
    private lateinit var googleSignInOptions: GoogleSignInOptions
    private lateinit var googleApiClient: GoogleApiClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        googleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this, {
                    Log.d("DEBUG", "OnConnectionFailed")
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build()
    }

    fun onGoogleClickedListener() {
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            handleSignInResult(result)
        }
    }

    private fun handleSignInResult(result: GoogleSignInResult) {
        if (result.isSuccess && result.signInAccount != null){
            firebaseAuthWithGoogle(result.signInAccount!!)
        } else {
            onError("Failed to Google Sign In")
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        authoriseWithFirebase(credential)
    }

}

