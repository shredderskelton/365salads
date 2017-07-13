package com.kan.salads

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import com.kan.salads.model.DatabasePrimer
import com.kan.salads.model.Salad
import com.kan.salads.model.UserDataHandler
import kotlinx.android.synthetic.main.activity_home.*

const val CURRENTUSERS = "currentUsers"

class HomeActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseData: DatabaseReference
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var userDataHandler: UserDataHandler

    private val menu: MutableList<Salad> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        button_buy.setOnClickListener {
            val currentUser = firebaseAuth.currentUser
            if (currentUser == null || currentUser.isAnonymous) {
                startActivity(LoginActivityIntent())
            } else {
                Toast.makeText(this, "Sorry ${currentUser.displayName} this is not yet implemented", Toast.LENGTH_SHORT).show()
            }
        }
        button_buy.setOnLongClickListener {
            val primer = DatabasePrimer()
            primer.loadDatabase(firebaseData)
            false
        }
        button_logout.setOnClickListener {
            if (firebaseAuth.currentUser!!.isAnonymous) {
                return@setOnClickListener
            }
            userDataHandler.onLogout()
            firebaseAuth.signOut()
            updateUI()
        }
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseData = FirebaseDatabase.getInstance().reference
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        home_recycle_view.adapter
        home_recycle_view.layoutManager = LinearLayoutManager(this)
        home_recycle_view.adapter = SaladListAdapter(this, menu)
    }

    private fun initShoppingCartListener() {
        val cartListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                button_buy.text = "Buy ${dataSnapshot.childrenCount} items"
                firebaseAnalytics.setUserProperty("shoppingCartCount", dataSnapshot.childrenCount.toString())
                firebaseAnalytics.setUserProperty("shoppingCartLastUsed", System.currentTimeMillis().toString())
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("shopping cart changed listener failed:onCancelled ${databaseError.toException()}")
            }
        }

        firebaseData
                .child("users")
                .child(firebaseAuth.currentUser!!.uid)
                .child("cart").addValueEventListener(cartListener)
    }

    private fun initSaladMenu() {
        val menuListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                menu.clear()
                dataSnapshot.children.mapNotNullTo(menu) { it.getValue<Salad>(Salad::class.java) }
                home_recycle_view.adapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }
        }
        firebaseData.child("salads").addListenerForSingleValueEvent(menuListener)
    }

    override fun onStart() {
        super.onStart()
        FirebaseMessaging.getInstance().subscribeToTopic(CURRENTUSERS)
        updateUI()
    }

    override fun onStop() {
        super.onStop()
        FirebaseMessaging.getInstance().unsubscribeFromTopic(CURRENTUSERS)
    }

    override fun onResume() {
        super.onResume()
        //TODO GoogleApiAvailability.makeGooglePlayServicesAvailable()
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
            updateUIanonymous()
        } else {
            updateUIloggedIn(currentUser)
        }
        userDataHandler = UserDataHandler(firebaseAuth.currentUser!!.uid)
        userDataHandler.onLogin()
        initShoppingCartListener()
        initSaladMenu()
    }

    private fun updateUIloggedIn(currentUser: FirebaseUser) {
        text_log.text = "Welcome to 365 Salads ${currentUser.displayName}"
        button_logout.text = "Logout"
    }

    private fun updateUIanonymous() {
        text_log.text = "Signed in Anonymously"
        button_logout.text = "Login"
    }
}

fun Context.HomeActivityIntent(): Intent {
    return Intent(this, HomeActivity::class.java).apply {
        //        putExtra(INTENT_USER_ID, user.id)
    }
}
