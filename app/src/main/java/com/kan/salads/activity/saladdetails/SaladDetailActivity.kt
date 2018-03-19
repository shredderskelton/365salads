package com.kan.salads.activity.saladdetails

import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.google.android.gms.appinvite.AppInviteReferral
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.kan.salads.R
import com.kan.salads.shareText
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_details.*
import timber.log.Timber


class SaladDetailActivity : AppCompatActivity(), SaladDetailPresenter.SaladDetailView {

    companion object {

        const val DYNAMIC_LINK_DOMAIN = "ude3d.app.goo.gl"
        private const val QUERY_PARAM_REFERRER = "referrer"
        private const val QUERY_PARAM_SALAD = "saladId"
        private const val PATH = "salads"
        private const val AUTH_DOMAIN = "kan.com"
        private const val SCHEME = "http"

        fun createShareUri(saladId: String, userId: String?): Uri {
            val builder = Uri.Builder()
            builder.scheme(SaladDetailActivity.SCHEME)
                    .authority(SaladDetailActivity.AUTH_DOMAIN)
                    .appendPath(SaladDetailActivity.PATH)
                    .appendQueryParameter(SaladDetailActivity.QUERY_PARAM_SALAD, saladId)

            userId?.let {
                builder.appendQueryParameter(SaladDetailActivity.QUERY_PARAM_REFERRER, userId)
            }

            return builder.build()
        }

    }

    private lateinit var presenter: SaladDetailPresenter

    private var referrer: String? = null

    private val args by lazy {
        SaladDetailActivityArgs.deserializeFrom(intent)
    }

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        val saladId = if (AppInviteReferral.hasReferral(intent)) {
            val link = AppInviteReferral.getDeepLink(intent)
            Timber.d("Deep Link: $link")
            val uri = Uri.parse(link)
            referrer = uri.getQueryParameter(QUERY_PARAM_REFERRER)
            val salad = uri.getQueryParameter(QUERY_PARAM_SALAD)
            salad
        } else {
            args.saladId
        }

        presenter = SaladDetailPresenter(this, saladId)

        fab.setOnClickListener {
            share(args.saladId)
        }

//        ViewCompat.setOnApplyWindowInsetsListener(contentText) { v, insets ->
//            v.setPadding(v.paddingLeft, v.paddingTop, v.paddingRight, insets.systemWindowInsetBottom)
//            insets.consumeSystemWindowInsets()
//        }
    }

    private fun share(id: String) {
        val myUri = createShareUri(id)
        Timber.d("Shared Link: $myUri")

        val dynamicLinkUri = createDynamicUri(myUri)
        Timber.d("Dynamic Link: $dynamicLinkUri")

        shortenLink(dynamicLinkUri)
    }

    private fun shortenLink(dynamicLinkUri: Uri) {
        FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(dynamicLinkUri)
                .buildShortDynamicLink()
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val shortLink = task.result.shortLink
                        Timber.d("Shortlink: $shortLink")
                        shareText("Hey, check out this nutritious salad I found: $shortLink")
                    } else {
                        Timber.e(task.exception)
                    }
                }
    }

    private fun createDynamicUri(myUri: Uri): Uri {
        val dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(myUri)
                .setDynamicLinkDomain(SaladDetailActivity.DYNAMIC_LINK_DOMAIN)
                .setAndroidParameters(DynamicLink.AndroidParameters.Builder().build())
                .buildDynamicLink()
        return dynamicLink.uri
    }

    private fun createShareUri(saladId: String): Uri {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        return SaladDetailActivity.createShareUri(saladId, userId)
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