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

package com.ivianuu.essentials.util.ext

import android.app.Activity
import android.app.Application
import android.content.ComponentCallbacks2
import android.content.res.Configuration
import android.os.Bundle

fun Application.doOnActivityCreated(block: (activity: Activity, savedInstanceState: Bundle?) -> Unit) =
    registerActivityLifecycleCallbacks(onActivityCreated = block)

fun Application.doOnActivityStarted(block: (activity: Activity) -> Unit) =
    registerActivityLifecycleCallbacks(onActivityStarted = block)

fun Application.doOnActivityResumed(block: (activity: Activity) -> Unit) =
    registerActivityLifecycleCallbacks(onActivityResumed = block)

fun Application.doOnActivityPaused(block: (activity: Activity) -> Unit) =
    registerActivityLifecycleCallbacks(onActivityPaused = block)

fun Application.doOnActivityStopped(block: (activity: Activity) -> Unit) =
    registerActivityLifecycleCallbacks(onActivityStopped = block)

fun Application.doOnActivitySaveInstanceState(block: (activity: Activity, outState: Bundle) -> Unit) =
    registerActivityLifecycleCallbacks(onActivitySaveInstanceState = block)

fun Application.doOnActivityDestroyed(block: (activity: Activity) -> Unit) =
    registerActivityLifecycleCallbacks(onActivityDestroyed = block)

fun Application.registerActivityLifecycleCallbacks(
    onActivityCreated: ((activity: Activity, savedInstanceState: Bundle?) -> Unit)? = null,
    onActivityStarted: ((activity: Activity) -> Unit)? = null,
    onActivityResumed: ((activity: Activity) -> Unit)? = null,
    onActivityPaused: ((activity: Activity) -> Unit)? = null,
    onActivityStopped: ((activity: Activity) -> Unit)? = null,
    onActivitySaveInstanceState: ((activity: Activity, outState: Bundle) -> Unit)? = null,
    onActivityDestroyed: ((activity: Activity) -> Unit)? = null
): Application.ActivityLifecycleCallbacks {
    val callbacks = object : Application.ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            onActivityCreated?.invoke(activity, savedInstanceState)
        }

        override fun onActivityStarted(activity: Activity) {
            onActivityStarted?.invoke(activity)
        }

        override fun onActivityResumed(activity: Activity) {
            onActivityResumed?.invoke(activity)
        }

        override fun onActivityPaused(activity: Activity) {
            onActivityPaused?.invoke(activity)
        }

        override fun onActivityStopped(activity: Activity) {
            onActivityStopped?.invoke(activity)
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            onActivitySaveInstanceState?.invoke(activity, outState)
        }

        override fun onActivityDestroyed(activity: Activity) {
            onActivityDestroyed?.invoke(activity)
        }
    }
    registerActivityLifecycleCallbacks(callbacks)
    return callbacks
}

fun Application.doOnConfigurationChanged(block: (newConfig: Configuration) -> Unit) =
    registerComponentCallbacks(onConfigurationChanged = block)

fun Application.doOnLowMemory(block: () -> Unit) =
    registerComponentCallbacks(onLowMemory = block)

fun Application.doOnTrimMemory(block: (level: Int) -> Unit) =
    registerComponentCallbacks(onTrimMemory = block)

fun Application.registerComponentCallbacks(
    onConfigurationChanged: ((newConfig: Configuration) -> Unit)? = null,
    onLowMemory: (() -> Unit)? = null,
    onTrimMemory: ((level: Int) -> Unit)? = null
): ComponentCallbacks2 {
    val callbacks = object : ComponentCallbacks2 {
        override fun onConfigurationChanged(newConfig: Configuration) {
            onConfigurationChanged?.invoke(newConfig)
        }

        override fun onLowMemory() {
            onLowMemory?.invoke()
        }

        override fun onTrimMemory(level: Int) {
            onTrimMemory?.invoke(level)
        }
    }
    registerComponentCallbacks(callbacks)
    return callbacks
}