package com.kan.salads.activity

import android.content.Context
import android.content.Intent


open interface ActivityArgs {

    /**
     * @return returns an intent that can be used to launch this activity.
     */
    fun intent(activity: Context): Intent

    /**
     * Launches the activity given your activity context.
     *
     * The default implementation uses the intent generated from [intent]
     */
    fun launch(activity: Context) = activity.startActivity(intent(activity))
}
