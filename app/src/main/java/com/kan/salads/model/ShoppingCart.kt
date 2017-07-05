package com.kan.salads.model

import com.google.firebase.database.FirebaseDatabase

private const val USERS = "users"
private const val CART = "cart"

class ShoppingCart {

    private var firebaseData = FirebaseDatabase.getInstance().reference

    fun addItem(userId: String, shopItemId: String) {
        firebaseData
                .child(USERS)
                .child(userId)
                .child(CART)
                .child(shopItemId)
                .setValue(true)
    }

    fun removeItem(userId: String, shopItemId: String) {
        firebaseData
                .child(USERS)
                .child(userId)
                .child(CART)
                .child(shopItemId)
                .setValue(null)
    }
}