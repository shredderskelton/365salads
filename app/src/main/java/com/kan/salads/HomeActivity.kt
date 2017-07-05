package com.kan.salads

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.kan.salads.model.DatabasePrimer
import com.kan.salads.model.Salad
import kotlinx.android.synthetic.main.activity_home.*


class HomeActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseData: DatabaseReference

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
            if (!firebaseAuth.currentUser!!.isAnonymous) {
                firebaseAuth.signOut()
                updateUI()
            }
        }
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseData = FirebaseDatabase.getInstance().reference

        home_recycle_view.adapter
        home_recycle_view.layoutManager = LinearLayoutManager(this)
        home_recycle_view.adapter = SaladListAdapter(this, menu)
    }

    private fun initShoppingCartListener() {
        val cartListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                button_buy.text = "Buy ${dataSnapshot.childrenCount} items"
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
        updateUI()
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
        initShoppingCartListener()
        initSaladMenu()
    }

    private fun updateUIloggedIn(currentUser: FirebaseUser) {
        text_log.setText("Welcome to 365 Salads ${currentUser.displayName}")
        button_logout.setText("Logout")
    }

    private fun updateUIanonymous() {
        text_log.setText("Signed in Anonymously")
        button_logout.setText("Login")
    }
}

fun Context.HomeActivityIntent(): Intent {
    return Intent(this, HomeActivity::class.java).apply {
        //        putExtra(INTENT_USER_ID, user.id)
    }
}
