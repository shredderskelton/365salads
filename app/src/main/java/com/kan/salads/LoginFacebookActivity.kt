package com.kan.salads

import android.support.v7.app.AppCompatActivity
import com.google.firebase.auth.AuthCredential

abstract class LoginFacebookActivity : AppCompatActivity() {

    abstract fun onError(errorMessage:String)
    abstract fun authoriseWithFirebase(credential: AuthCredential)

    fun onFacebookClickedListener(){

    }
}