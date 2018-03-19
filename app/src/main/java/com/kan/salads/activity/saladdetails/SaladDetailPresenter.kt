package com.kan.salads.activity.saladdetails

import com.google.firebase.database.*
import com.kan.salads.model.Salad

class SaladDetailPresenter(val view: SaladDetailView, saladId: String) {
    private val firebaseData: DatabaseReference = FirebaseDatabase.getInstance().reference

    init {
        val listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //TODO fail log
                val salad: Salad = dataSnapshot.getValue<Salad>(Salad::class.java)
                        ?: return
                view.setTitle(salad.name)
                view.setImage(salad.photo)
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