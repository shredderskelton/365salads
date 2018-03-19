package com.kan.salads.activity.saladdetails

import com.google.firebase.database.*
import com.kan.salads.model.Salad
import timber.log.Timber

class SaladDetailPresenter(val view: SaladDetailView, saladId: String) {
    private val firebaseData: DatabaseReference = FirebaseDatabase.getInstance().reference

    init {
        val listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val salad = dataSnapshot.getValue<Salad>(Salad::class.java)
                if (salad == null) Timber.e("Failed to serialise the salad")
                salad?.let {
                    view.setTitle(it.name)
                    view.setImage(it.photo)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }
        }
        firebaseData.child("salads").child(saladId).addListenerForSingleValueEvent(listener)
    }

    interface SaladDetailView {
        fun setTitle(text: String)
        fun setImage(imageUrl: String)
    }
}