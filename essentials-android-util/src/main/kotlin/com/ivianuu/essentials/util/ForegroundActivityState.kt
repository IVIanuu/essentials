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
import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.ivianuu.essentials.activity.EsActivity
import com.ivianuu.essentials.coroutines.GlobalScope
import com.ivianuu.injekt.Binding
import com.ivianuu.injekt.Eager
import com.ivianuu.injekt.Scoped
import com.ivianuu.injekt.merge.ApplicationComponent
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.shareIn

typealias ForegroundActivity = ComponentActivity?

@Eager
@Scoped(ApplicationComponent::class)
@Binding
fun foregroundActivityState(
    application: Application,
    globalScope: GlobalScope,
): Flow<ForegroundActivity> = callbackFlow<ForegroundActivity> {
    application.registerActivityLifecycleCallbacks(
        object : Application.ActivityLifecycleCallbacks {
            override fun onActivityStarted(activity: Activity) {
                if (activity is EsActivity) offer(activity)
            }

            override fun onActivityStopped(activity: Activity) {
                if (activity is EsActivity) offer(null)
            }

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            }

            override fun onActivityDestroyed(activity: Activity) {
            }

            override fun onActivityResumed(activity: Activity) {
            }

            override fun onActivityPaused(activity: Activity) {
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            }
        }
    )

    awaitClose()
}.shareIn(globalScope, SharingStarted.Eagerly, 1)