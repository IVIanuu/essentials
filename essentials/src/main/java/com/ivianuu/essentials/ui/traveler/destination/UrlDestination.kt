package com.ivianuu.essentials.ui.traveler.destination

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.ivianuu.traveler.android.ActivityKey

/**
 * Opens the url in the browser
 */
data class UrlDestination(val url: String) : ActivityKey {
    override fun createIntent(context: Context, data: Any?) =
        Intent(Intent.ACTION_VIEW).apply { this.data = url.toUri() }
}