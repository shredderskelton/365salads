package com.kan.salads.model

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

private const val USERS = "users"
private const val CART = "cart"

class UserDataHandler(dataChanged: (newData:HashSet<String>) -> Unit) {
    private var firebaseData = FirebaseDatabase.getInstance().reference

    val data: HashSet<String> = hashSetOf()
    var currentUser:String = ""

    private var cartListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            data.clear()
            for (postSnapshot in dataSnapshot.children) {
                postSnapshot.key?.let {
                    data.add(it)
                }
            }
            dataChanged(data)
        }

        override fun onCancelled(databaseError: DatabaseError) {
            println("shopping cart changed listener failed:onCancelled ${databaseError.toException()}")
        }
    }

    fun onLogin(newUser: String) {
        initShoppingCartListener(newUser)
    }

    fun onLogout() {
        removeCurrentUser()
    }

    private fun initShoppingCartListener(newUser: String) {
        removeCurrentUser()
        addNewUser(newUser)
        currentUser = newUser
    }

    private fun addNewUser(newUser: String) {
        firebaseData
                .child(USERS)
                .child(newUser)
                .child(CART).addValueEventListener(cartListener)
    }

    private fun removeCurrentUser() {
        firebaseData
                .child(USERS)
                .child(currentUser)
                .child(CART).removeEventListener(cartListener)
    }

    fun removeItem(itemId:String) {
        firebaseData
                .child(USERS)
                .child(currentUser)
                .child(CART)
                .child(itemId)
                .setValue(null)
    }

    fun addItem(itemId:String) {
        firebaseData
                .child(USERS)
                .child(currentUser)
                .child(CART)
                .child(itemId)
                .setValue(true)
    }
}