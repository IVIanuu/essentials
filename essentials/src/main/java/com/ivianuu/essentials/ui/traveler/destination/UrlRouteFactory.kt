package com.ivianuu.essentials.ui.traveler.destination

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.ivianuu.compass.android.ActivityRouteFactory

abstract class UrlRouteFactory<T : Any> : ActivityRouteFactory<T> {
    final override fun createActivityIntent(
        context: Context,
        destination: T
    ): Intent = Intent(Intent.ACTION_VIEW).apply {
        data = createUrl(destination).toUri()
    }

    protected abstract fun createUrl(destination: T): String
}