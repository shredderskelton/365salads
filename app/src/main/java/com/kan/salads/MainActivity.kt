package com.kan.salads

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private var loginCount = 0
    val RC_SIGN_IN = 9001

    lateinit var firebaseAuth: FirebaseAuth
    lateinit var googleSignInOptions: GoogleSignInOptions
    lateinit var googleApiClient: GoogleApiClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button_login_with_facebook.setOnClickListener { onFacebookClickedListener() }
        button_login_with_google.setOnClickListener { onGoogleClickedListener() }
        button_logout.setOnClickListener { firebaseAuth.signOut() }

        firebaseAuth = FirebaseAuth.getInstance()
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

    public override fun onStart() {
        super.onStart()
        loginUser(firebaseAuth.currentUser)
    }

    private fun onGoogleClickedListener() {
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
        if (result.isSuccess && result.signInAccount != null) {
            firebaseAuthWithGoogle(result.signInAccount!!)
        } else {
            text_log.text = "Failed to Google Sign In"
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        loginUser(firebaseAuth.currentUser)
                    } else {
                        text_log.text = "Firebase Auth Failed"
                    }
                }
    }

    private fun loginUser(user: FirebaseUser?) {
        loginCount++
        if (user == null) {
            text_log.text = "Logged out ($loginCount)"
            return
        }
        text_log.text = "User ${user.displayName} logged in"
    }

    private fun onFacebookClickedListener() {

    }
}
