package com.kan.salads.activity.saladdetails

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.kan.salads.BuildConfig
import com.kan.salads.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_details.*
import timber.log.Timber


class SaladDetailActivity : AppCompatActivity(), SaladDetailPresenter.SaladDetailView {

    private val DYNAMIC_LINK_DOMAIN = "ude3d.app.goo.gl"
    private val QUERY_PARAM_SALAD = "saladId"

    private lateinit var presenter: SaladDetailPresenter

    private val args by lazy {
        SaladDetailActivityArgs.deserializeFrom(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        FirebaseDynamicLinks.getInstance().getDynamicLink(intent).addOnFailureListener {
            // error
            Timber.e(it)
        }.addOnSuccessListener {
            // deep link
            val saladId = if (it == null) {
                args.saladId
            } else {
                it.link.getQueryParameter(QUERY_PARAM_SALAD)
            }
            init(saladId)
        }
    }

    fun init(extractedSaladId: String) {
        presenter = SaladDetailPresenter(this, extractedSaladId)
        versionText.text = BuildConfig.VERSION_CODE.toString()
        fab.setOnClickListener {
            share(extractedSaladId)
        }
    }

    private fun share(id: String) {
        val myUri = createShareUri(id)
        Timber.d("Shared Link: $myUri")

        val dynamicLinkUri = createDynamicUri(myUri)
        Timber.d("Dynamic Link: $dynamicLinkUri")

        shortenLink(dynamicLinkUri)
    }

    private fun createShareUri(saladId: String): Uri {
        val builder = Uri.Builder()
        builder.scheme(getString(R.string.config_scheme)) // "http"
                .authority(getString(R.string.config_host)) // "365salads.xyz"
                .appendPath(getString(R.string.config_path_salads)) // "salads"
                .appendQueryParameter(QUERY_PARAM_SALAD, saladId)
        return builder.build()
    }

    private fun shortenLink(linkUri: Uri) {
        FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(linkUri)
                .buildShortDynamicLink()
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val shortLink = task.result.shortLink
                        val msg = "Hey, check out this nutritious salad I found: $shortLink"
                        val sendIntent = Intent()
                        sendIntent.action = Intent.ACTION_SEND
                        sendIntent.putExtra(Intent.EXTRA_TEXT, msg)
                        sendIntent.type = "text/plain"
                        startActivity(sendIntent)
                    } else {
                        Timber.e(task.exception)
                    }
                }
    }

    private fun createDynamicUri(myUri: Uri): Uri {
        val dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(myUri)
                .setDynamicLinkDomain(DYNAMIC_LINK_DOMAIN)
                .setAndroidParameters(DynamicLink.AndroidParameters.Builder()
                        .build())
//                .setIosParameters(DynamicLink.IosParameters.Builder("ibi").setFallbackUrl()build())
                .buildDynamicLink()
        return dynamicLink.uri
    }

//    private fun createShareUri(saladId: String): Uri {
//        val userId = FirebaseAuth.getInstance().currentUser?.uid
//        return createShareUri(saladId, userId)
//    }

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