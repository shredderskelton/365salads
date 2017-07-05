package com.kan.salads

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : LoginGoogleActivity() {

    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        button_login_with_twitter.setOnClickListener { onTwitterLoginClicked() }
        button_login_with_facebook.setOnClickListener { onFacebookClickedListener() }
        button_login_with_google.setOnClickListener { onGoogleClickedListener() }
        button_login_anonymous.setOnClickListener {
            finish()
        }
        button_logout.setOnClickListener { firebaseAuth.signOut() }

        firebaseAuth = FirebaseAuth.getInstance()
    }

    override fun authoriseWithFirebase(credential: AuthCredential) {
        mergeWithCurrentUser(credential)
    }

    private fun mergeWithCurrentUser(credential: AuthCredential) {
        firebaseAuth.currentUser!!.linkWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        finish()
                    } else {
                        val exception = task.exception
                        if (exception == null) {
                            text_log.text = "Firebase Authorisation Failed: Unknown reason"
                            return@addOnCompleteListener
                        }
                        if (exception is FirebaseAuthUserCollisionException) {
                            signInNewUser(credential)
                        } else {
                            text_log.text = "Firebase Authorisation Failed: ${exception.localizedMessage}"
                        }
                    }
                }
    }

    private fun signInNewUser(credential: AuthCredential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        finish()
                    } else {
                        val exception = task.exception
                        if (exception == null) {
                            text_log.text = "Firebase Authorisation Failed: Unknown reason"
                            return@addOnCompleteListener
                        }
                        if (exception is FirebaseAuthUserCollisionException) {
                            text_log.text = "Someone with this email has already created an account, please try a different login method"
                        } else {
                            text_log.text = "Firebase Authorisation Failed: ${exception.localizedMessage}"
                        }
                    }
                }
    }

    override fun onError(errorMessage: String) {
        text_log.setText(errorMessage)
    }
}

fun Context.LoginActivityIntent(): Intent {
    return Intent(this, LoginActivity::class.java).apply {
        //        putExtra(INTENT_USER_ID, user.id)
    }
}
