package com.ivianuu.essentials.ui.traveler.destination

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.ivianuu.compass.ActivityRouteFactory
import com.ivianuu.compass.Destination
import com.ivianuu.compass.RouteFactory

@RouteFactory(PlayStoreDetailsDestination.RouteFactory::class)
@Destination
data class PlayStoreDetailsDestination(val packageName: String) {

    object RouteFactory : ActivityRouteFactory<PlayStoreDetailsDestination> {
        override fun createActivityIntent(
                context: Context,
                destination: PlayStoreDetailsDestination
        ) = Intent(Intent.ACTION_VIEW,
                "https://play.google.com/store/apps/details?id=com.ivianuu.hidenavbar".toUri()
        )
    }

}
