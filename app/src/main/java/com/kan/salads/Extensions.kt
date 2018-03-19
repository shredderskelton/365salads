package com.kan.salads

import android.content.Context
import android.content.Intent

fun Context.shareText(msg: String) {
    val sendIntent = Intent()
    sendIntent.action = Intent.ACTION_SEND
    sendIntent.putExtra(Intent.EXTRA_TEXT, msg)
    sendIntent.type = "text/plain"
    startActivity(sendIntent)
}