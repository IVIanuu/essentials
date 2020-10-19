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
import androidx.compose.runtime.LaunchedTask
import com.ivianuu.essentials.ui.common.compositionActivity
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.Route
import com.ivianuu.injekt.Binding
import com.ivianuu.injekt.android.ApplicationContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

typealias startUi = suspend () -> Activity
@Binding
fun startUi(
    applicationContext: ApplicationContext,
    buildInfo: BuildInfo,
    navigator: Navigator,
    packageManager: PackageManager,
): startUi = {
    val intent = packageManager.getLaunchIntentForPackage(buildInfo.packageName)!!
    suspendCoroutine { continuation ->
        var completed = false
        navigator.push(
            Route(opaque = true) {
                if (!completed) {
                    completed = true
                    navigator.popTop()
                    val activity = compositionActivity
                    LaunchedTask { continuation.resume(activity) }
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
