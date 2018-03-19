package com.kan.salads.activity.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.Toast
import com.kan.salads.R
import com.kan.salads.SaladListAdapter
import com.kan.salads.ShoppingCartItemViewModel
import com.kan.salads.activity.login.LoginActivityArgs
import com.kan.salads.activity.saladdetails.SaladDetailActivityArgs
import kotlinx.android.synthetic.main.activity_home.*
import java.util.*

class HomeActivity : AppCompatActivity(), HomeView {
    private lateinit var presenter: HomePresenter
    private val adapter = SaladListAdapter(this, mutableListOf())
    var lastItem: ShoppingCartItemViewModel? = null

    override fun showNotImplemented() {
        Toast.makeText(this, "Sorry this is not yet implemented", Toast.LENGTH_SHORT).show()

    }

    @SuppressLint("SetTextI18n")
    override fun setSelectedCartCount(selectedCount: Int) {
        buyButton.text = "Buy $selectedCount items"
    }

    override fun setShoppingCartItems(items: List<ShoppingCartItemViewModel>) {
        if (items.isNotEmpty()) {
            val seconds = Calendar.getInstance().get(Calendar.SECOND)
            val index = seconds % items.size
            lastItem = items[index]
        }
        adapter.setNewItems(items)
    }

    override fun setIsLoggedInAnonymously() {
        updateUIanonymous()
        logoutButton.setOnClickListener {
            presenter.onLoginClicked()
        }
    }

    override fun setIsLoggedInAsUser(userName: String) {
        updateUIloggedIn(userName)
        logoutButton.setOnClickListener {
            presenter.onLogoutClicked()
        }
    }

    override fun showLoginActivity() {
        LoginActivityArgs().launch(this)
    }

    fun startDetails() {
        lastItem?.let {
            SaladDetailActivityArgs(it.salad.uuid).launch(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        buyButton.setOnClickListener {
            presenter.onBuyClicked()
        }

        buyButton.setOnLongClickListener {
            startDetails()
            true
//            val primer = DatabasePrimer()
//            primer.loadDatabase(firebaseData)
//            false
        }

        presenter = HomePresenter(this, this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter.onItemSelected = presenter::onItemSelected
        adapter.onItemDeSelected = presenter::onItemDeSelected
        recyclerView.adapter = adapter


        val mes = intent?.extras?.getString("message") ?: "Nuttin"
        Log.d("Home", mes)

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val mes = intent?.extras?.getString("message") ?: "Nuttin"
        Log.d("Home", mes)
    }

    override fun onStart() {
        super.onStart()
        presenter.start()
    }

    override fun onStop() {
        super.onStop()
        presenter.stop()
    }

    private fun updateUIloggedIn(currentUser: String) {
        logTextView.text = "${currentUser}"
        logoutButton.text = "Logout"
    }

    private fun updateUIanonymous() {
        logTextView.text = "Signed in Anonymously"
        logoutButton.text = "Login"
    }
}

