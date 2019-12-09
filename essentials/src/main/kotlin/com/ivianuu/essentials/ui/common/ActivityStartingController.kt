/*
 * Copyright 2019 Manuel Wrage
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

package com.ivianuu.essentials.ui.common

import android.app.Activity
import android.content.Intent
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ShareCompat
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.ivianuu.director.requireActivity
import com.ivianuu.essentials.ui.base.EsController
import com.ivianuu.essentials.ui.navigation.director.ControllerRoute
import com.ivianuu.essentials.ui.navigation.director.dialog
import com.ivianuu.injekt.Factory
import com.ivianuu.injekt.Param
import com.ivianuu.injekt.parametersOf

fun ActivityRoute(intentFactory: (Activity) -> Intent) =
    ControllerRoute<ActivityStartingController>(options = ControllerRoute.Options().dialog()) {
        parametersOf(intentFactory)
    }

fun AppInfoRoute(packageName: String) = ActivityRoute {
    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        this.data = "package:$packageName".toUri()
    }
}

fun AppRoute(packageName: String) = ActivityRoute {
    it.packageManager.getLaunchIntentForPackage(packageName)!!
}

fun ShareRoute(text: String) = ActivityRoute {
    ShareCompat.IntentBuilder
        .from(it)
        .setType("text/plain")
        .setText(text)
        .createChooserIntent()
}

fun UrlRoute(url: String) = ActivityRoute {
    Intent(Intent.ACTION_VIEW).apply { this.data = url.toUri() }
}

@Factory
internal class ActivityStartingController(
    @Param private val intentFactory: (Activity) -> Intent
) : EsController() {

    init {
        lifecycleScope.launchWhenResumed {
            requireActivity().startActivity(intentFactory(requireActivity()))
            navigator.pop()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup
    ) = View(requireActivity()) // dummy
}
