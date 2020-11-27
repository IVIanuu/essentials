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

package com.ivianuu.essentials.util

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.runtime.LaunchedEffect
import com.ivianuu.essentials.ui.animatedstack.NoOpStackTransition
import com.ivianuu.essentials.ui.common.RetainedObjectsAmbient
import com.ivianuu.essentials.ui.common.compositionActivity
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.Route
import com.ivianuu.essentials.ui.navigation.RouteAmbient
import com.ivianuu.essentials.ui.navigation.pop
import com.ivianuu.essentials.ui.navigation.push
import com.ivianuu.injekt.FunBinding
import com.ivianuu.injekt.android.ApplicationContext
import kotlinx.coroutines.DisposableHandle
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@FunBinding
suspend fun startUi(
    applicationContext: ApplicationContext,
    buildInfo: BuildInfo,
    navigator: Navigator,
    packageManager: PackageManager,
): Activity {
    val intent = packageManager.getLaunchIntentForPackage(buildInfo.packageName)!!
    return suspendCoroutine { continuation ->
        navigator.push(
            Route(opaque = true, transition = NoOpStackTransition) {
                val activity = compositionActivity
                // we use a little hack to ensure that we only run when the route is popped and the animation is finished
                val retainedObjects = RetainedObjectsAmbient.current
                retainedObjects[continuation] = object : DisposableHandle {
                    override fun dispose() {
                        continuation.resume(activity)
                    }
                }

                val route = RouteAmbient.current
                LaunchedEffect(true) {
                    navigator.pop(route)
                }
            }
        )

        applicationContext.startActivity(
            intent.apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        )
    }
}
