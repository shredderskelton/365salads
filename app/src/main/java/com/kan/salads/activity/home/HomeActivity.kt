package com.kan.salads.activity.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.kan.salads.R
import com.kan.salads.SaladListAdapter
import com.kan.salads.ShoppingCartItemViewModel
import com.kan.salads.activity.login.LoginActivityIntent
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity(), HomeView {
    private lateinit var presenter: HomePresenter
    val adapter = SaladListAdapter(this, mutableListOf())

    override fun showNotImplemented() {
        Toast.makeText(this, "Sorry this is not yet implemented", Toast.LENGTH_SHORT).show()
    }

    override fun setSelectedCartCount(selectedCount: Int) {
        button_buy.text = "Buy ${selectedCount} items"
    }

    override fun setShoppingCartItems(items: List<ShoppingCartItemViewModel>) {
        adapter.setNewItems(items)
    }

    override fun setIsLoggedInAnonymously() {
        updateUIanonymous()
        button_logout.setOnClickListener {
            presenter.onLoginClicked()
        }
    }

    override fun setIsLoggedInAsUser(userName: String) {
        updateUIloggedIn(userName)
        button_logout.setOnClickListener {
            presenter.onLogoutClicked()
        }
    }

    override fun showLoginActivity() {
        startActivity(LoginActivityIntent())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        button_buy.setOnClickListener {
            presenter.onBuyClicked()
        }
//        button_buy.setOnLongClickListener {
//            val primer = DatabasePrimer()
//            primer.loadDatabase(firebaseData)
//            false
//        }
        presenter = HomePresenter(this, this)
        home_recycle_view.layoutManager = LinearLayoutManager(this)
        adapter.onItemSelected = presenter::onItemSelected
        adapter.onItemDeSelected = presenter::onItemDeSelected
        home_recycle_view.adapter = adapter
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
        text_log.text = "${currentUser}"
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
