package com.kan.salads.model

import com.google.firebase.database.DatabaseReference


class DatabasePrimer {

    fun loadDatabase(firebaseData: DatabaseReference) {
        val availableSalads: List<Salad> = mutableListOf(
                Salad("Gherkin", "Fresh and delicious"),
                Salad("Lettuce", "Easy to prepare"),
                Salad("Tomato", "Boring but healthy"),
                Salad("Zucchini", "Healthy and gross")
        )
        availableSalads.forEach {
            val key = firebaseData.child("salads").push().key
            it.uuid = key
            firebaseData.child("salads").child(key).setValue(it)
        }
    }
}