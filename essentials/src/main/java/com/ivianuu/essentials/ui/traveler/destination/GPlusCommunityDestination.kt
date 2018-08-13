package com.ivianuu.essentials.ui.traveler.destination

import com.ivianuu.compass.Destination
import com.ivianuu.compass.DoNotSerialize
import com.ivianuu.compass.RouteFactory
import com.ivianuu.essentials.util.ext.googlePlusCommunityUrl

@DoNotSerialize
@RouteFactory(GPlusCommunityDestination.RouteFactory::class)
@Destination
data class GPlusCommunityDestination(val id: String) {
    class RouteFactory : UrlRouteFactory<GPlusCommunityDestination>() {
        override fun createUrl(destination: GPlusCommunityDestination) =
            googlePlusCommunityUrl(destination.id)
    }
}
