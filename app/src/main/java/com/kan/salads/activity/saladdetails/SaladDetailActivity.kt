package com.kan.salads.activity.saladdetails

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.kan.salads.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_details.*
import timber.log.Timber


class SaladDetailActivity : AppCompatActivity(), SaladDetailPresenter.SaladDetailView {

    companion object {
        private val SALAD_ID = "saladId"

        fun start(context: Context, uuid: String) {
            val intent = Intent(context, SaladDetailActivity::class.java)
            intent.putExtra(SALAD_ID, uuid)
            context.startActivity(intent)
        }

        private fun extractSaladId(intent: Intent): String {
            return intent.extras[SALAD_ID].toString()
        }
    }

    private lateinit var presenter: SaladDetailPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        val id = extractSaladId(intent)
        presenter = SaladDetailPresenter(this, id)

//        ViewCompat.setOnApplyWindowInsetsListener(contentText) { v, insets ->
//            v.setPadding(v.paddingLeft, v.paddingTop, v.paddingRight, insets.systemWindowInsetBottom)
//            insets.consumeSystemWindowInsets()
//        }
    }

    override fun setTitle(text: String) {
        initToolbar(text)
    }

    override fun setImage(imageUrl: String) {
        Picasso.with(this).load(imageUrl).into(toolbarImage)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        Timber.d(item.toString())
        return super.onOptionsItemSelected(item)
    }

    private fun initToolbar(text: String) {
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
            title = text
        }
    }

}