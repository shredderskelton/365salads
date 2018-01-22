package com.kan.salads.activity.home

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import com.kan.salads.ShoppingCartItemViewModel
import com.kan.salads.model.Salad
import com.kan.salads.model.UserDataHandler

const val currentUsersNotificationKey = "currentUsers"

class HomePresenter(val view: HomeView, context: Context) {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firebaseData: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val firebaseAnalytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(context)
    private val userDataHandler: UserDataHandler
    private val availableSalads: MutableList<Salad> = mutableListOf()

    init {
        userDataHandler = UserDataHandler({ newData: HashSet<String> -> updateData(newData) })
    }

    private fun updateData(newData: HashSet<String>) {
        view.setSelectedCartCount(newData.size)
        updateList()
    }

    private fun initSaladMenu() {
        val menuListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                availableSalads.clear()
                dataSnapshot.children.mapNotNullTo(availableSalads) { it.getValue<Salad>(Salad::class.java) }
                updateList()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }
        }
        firebaseData.child("salads").addListenerForSingleValueEvent(menuListener)
    }

    private fun updateList() {
        val items: MutableList<ShoppingCartItemViewModel> = mutableListOf()
        availableSalads.forEach {
            val item = ShoppingCartItemViewModel(it, userDataHandler.data.contains( it.uuid))
            items.add(item)
        }
        view.setShoppingCartItems(items)
        firebaseAnalytics.setUserProperty("shoppingCartCount", userDataHandler.data.size.toString())
        firebaseAnalytics.setUserProperty("shoppingCartLastUsed", System.currentTimeMillis().toString())
    }

    private fun updateUI() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            firebaseAuth.signInAnonymously().addOnCompleteListener {
                updateUI()
            }
            return
        }
        if (currentUser.isAnonymous) {
            view.setIsLoggedInAnonymously()
        } else {
            val name = if (currentUser.displayName.isNullOrBlank()) "" else currentUser.displayName
            view.setIsLoggedInAsUser(name!!)
        }
        userDataHandler.onLogin(currentUser.uid)
        initSaladMenu()
    }

    fun start() {
        updateUI()
        FirebaseMessaging
                .getInstance()
                .subscribeToTopic(currentUsersNotificationKey)
    }

    fun stop() {
        FirebaseMessaging
                .getInstance()
                .unsubscribeFromTopic(currentUsersNotificationKey)
    }

    fun onLoginClicked() {
        view.showLoginActivity()
    }

    fun onLogoutClicked() {
        firebaseAuth.signOut()
        updateUI()
    }

    fun onItemSelected(itemId: String) {
        println("In presenter")
        userDataHandler.addItem(itemId)

    }

    fun onItemDeSelected(itemId: String) {
        userDataHandler.removeItem(itemId)
    }

    fun onBuyClicked() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null || currentUser.isAnonymous) {
            view.showLoginActivity()
        } else {
           view.showNotImplemented()
        }
    }
}