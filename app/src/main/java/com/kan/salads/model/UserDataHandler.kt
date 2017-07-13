package com.kan.salads.model

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId

private const val USERS = "users"
private const val CLOUDID = "cloudId"

class UserDataHandler(val userUniqueId: String) {
    private var firebaseData = FirebaseDatabase.getInstance().reference
    private var deviceToken = FirebaseInstanceId.getInstance().token

    fun onLogin(){
        setCloudMessagingId(deviceToken)
    }

    fun onLogout(){
        setCloudMessagingId(null)
    }

    private fun setCloudMessagingId(cloudMessagingId: String?) {
        firebaseData.child(USERS)
                .child(userUniqueId)
                .child(CLOUDID)
                .setValue(cloudMessagingId)
    }
}