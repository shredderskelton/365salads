package com.kan.salads.activity.saladdetails

import android.content.Context
import android.content.Intent
import com.kan.salads.activity.ActivityArgs

class SaladDetailActivityArgs(val saladId: String) : ActivityArgs {

    override fun intent(activity: Context): Intent = Intent(
            activity, SaladDetailActivity::class.java
    ).apply {
        putExtra(SALAD_ID_EXTRA, saladId)
    }

    companion object {
        private const val SALAD_ID_EXTRA = "saladId"

        fun deserializeFrom(intent: Intent): SaladDetailActivityArgs {
            return SaladDetailActivityArgs(intent.getStringExtra(SALAD_ID_EXTRA))
        }
    }
}