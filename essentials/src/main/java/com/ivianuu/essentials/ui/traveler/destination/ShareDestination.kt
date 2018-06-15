/*
 * Copyright 2018 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.ui.traveler.destination

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v4.app.ShareCompat
import com.ivianuu.compass.ActivityRouteFactory
import com.ivianuu.compass.Destination
import com.ivianuu.compass.DoNotSerialize
import com.ivianuu.compass.RouteFactory

/**
 * Shares the [text]
 */
@DoNotSerialize
@RouteFactory(ShareDestination.RouteFactory::class)
@Destination
data class ShareDestination(val text: String) {
    object RouteFactory : ActivityRouteFactory<ShareDestination> {
        override fun createIntent(context: Context, destination: ShareDestination): Intent {
            return ShareCompat.IntentBuilder
                .from(context as Activity)
                .setType("text/plain")
                .setText(destination.text)
                .createChooserIntent()
        }
    }
}