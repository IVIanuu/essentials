package com.ivianuu.essentials.ui.traveler.destination

import com.ivianuu.compass.Destination
import com.ivianuu.compass.DoNotSerialize
import com.ivianuu.compass.RouteFactory
import com.ivianuu.essentials.util.ext.playStoreDetailsUrl

@DoNotSerialize
@RouteFactory(PlayStoreDetailsDestination.RouteFactory::class)
@Destination
data class PlayStoreDetailsDestination(val packageName: String) {
    class RouteFactory : UrlRouteFactory<PlayStoreDetailsDestination>() {
        override fun createUrl(destination: PlayStoreDetailsDestination) =
            playStoreDetailsUrl(destination.packageName)
    }
}

