/*
 * Copyright 2020 Manuel Wrage
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

package com.ivianuu.essentials.ui.navigation

import android.app.Activity
import android.content.Intent
import android.provider.Settings
import androidx.compose.runtime.onActive
import androidx.core.app.ShareCompat
import androidx.core.net.toUri
import com.ivianuu.essentials.ui.animatedstack.NoOpStackTransition
import com.ivianuu.essentials.ui.common.compositionActivity

fun ActivityRoute(intentFactory: (Activity) -> Intent) = Route(
    opaque = true,
    transition = NoOpStackTransition
) {
    val activity = compositionActivity
    val navigator = NavigatorAmbient.current
    onActive {
        activity.startActivity(intentFactory(activity))
        navigator.popTop()
    }
}

fun AppInfoRoute(packageName: String) =
    ActivityRoute {
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            this.data = "package:$packageName".toUri()
        }
    }

fun AppRoute(packageName: String) =
    ActivityRoute {
        it.packageManager.getLaunchIntentForPackage(packageName)!!
    }

fun ShareRoute(text: String) =
    ActivityRoute {
        ShareCompat.IntentBuilder
            .from(it)
            .setType("text/plain")
            .setText(text)
            .createChooserIntent()
    }

fun UrlRoute(url: String) =
    ActivityRoute {
        Intent(Intent.ACTION_VIEW).apply { this.data = url.toUri() }
    }
