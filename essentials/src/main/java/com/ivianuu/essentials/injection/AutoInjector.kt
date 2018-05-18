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

package com.ivianuu.essentials.injection

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import com.ivianuu.essentials.util.ext.d
import com.ivianuu.essentials.util.ext.doOnActivityCreated
import com.ivianuu.essentials.util.ext.doOnFragmentAttached
import dagger.android.AndroidInjection
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector

/**
 * Automatically injects into childs
 */
object AutoInjector {

    fun start(application: Application) {
        application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
                d { "created $activity" }
            }

            override fun onActivityStarted(activity: Activity?) {
                d { "started $activity" }
            }

            override fun onActivityResumed(activity: Activity?) {
                d { "resumed $activity" }
            }

            override fun onActivityPaused(activity: Activity?) {
                d { "paused $activity" }
            }

            override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {

            }

            override fun onActivityStopped(activity: Activity?) {
                d { "stopped $activity" }
            }

            override fun onActivityDestroyed(activity: Activity?) {
                d { "destroyed $activity" }
            }
        })
        application.doOnActivityCreated { activity, _ ->
            d { "inject $activity" }
            handleActivity(activity)
        }
    }

    private fun handleActivity(activity: Activity) {
        if (activity is Injectable) {
            AndroidInjection.inject(activity)
        }

        if (activity is FragmentActivity && activity is HasSupportFragmentInjector) {
            activity.supportFragmentManager.doOnFragmentAttached(true) { _: FragmentManager, fragment: Fragment, _: Context ->
                if (fragment is Injectable) {
                    d { "inject $fragment" }
                    AndroidSupportInjection.inject(fragment)
                }
            }
        }
    }
}