package com.ivianuu.essentials.ui.traveler.destination

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri
import com.ivianuu.compass.ActivityRouteFactory
import com.ivianuu.compass.Destination
import com.ivianuu.compass.DoNotSerialize
import com.ivianuu.compass.RouteFactory

/**
 * Open s the app info of the app
 */
@DoNotSerialize
@RouteFactory(UrlDestination.RouteFactory::class)
@Destination
data class UrlDestination(val url: String) {
    open class RouteFactory : ActivityRouteFactory<UrlDestination> {
        override fun createActivityIntent(
            context: Context,
            destination: UrlDestination
        ): Intent = Intent(Intent.ACTION_VIEW).apply {
            data = destination.url.toUri()
        }
    }
}