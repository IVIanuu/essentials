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
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.ivianuu.essentials.app.AppService
import dagger.android.AndroidInjection
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

/**
 * Automatically injects [Injectable] [Activity]'s and [Fragment]'s
 */
class AutoInjector @Inject constructor(private val application: Application) : AppService {

    override fun start() {
        application.registerActivityLifecycleCallbacks(object :
            Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                handleActivity(activity)
            }

            override fun onActivityStarted(activity: Activity) {
            }

            override fun onActivityResumed(activity: Activity) {
            }

            override fun onActivityPaused(activity: Activity) {
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle?) {
            }

            override fun onActivityStopped(activity: Activity) {
            }

            override fun onActivityDestroyed(activity: Activity) {
            }
        })
    }

    private fun handleActivity(activity: Activity) {
        if (activity is Injectable && activity !is Ignore) {
            AndroidInjection.inject(activity)
        }

        if (activity is FragmentActivity && activity is HasSupportFragmentInjector) {
            activity.supportFragmentManager.registerFragmentLifecycleCallbacks(object :
                FragmentManager.FragmentLifecycleCallbacks() {
                override fun onFragmentPreAttached(
                    fm: FragmentManager,
                    f: Fragment,
                    context: Context
                ) {
                    super.onFragmentPreAttached(fm, f, context)
                    if (f is Injectable && f !is Ignore) {
                        AndroidSupportInjection.inject(f)
                    }
                }
            }, true)
        }
    }

    interface Ignore
}