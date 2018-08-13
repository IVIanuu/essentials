package com.ivianuu.essentials.ui.traveler.destination

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
    class RouteFactory : UrlRouteFactory<UrlDestination>() {
        override fun createUrl(destination: UrlDestination) = destination.url
    }
}