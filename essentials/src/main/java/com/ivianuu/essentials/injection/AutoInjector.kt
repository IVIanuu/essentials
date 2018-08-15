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
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import com.ivianuu.essentials.app.AppService
import com.ivianuu.essentials.util.ext.doOnActivityCreated
import com.ivianuu.essentials.util.ext.doOnFragmentPreAttached
import dagger.android.AndroidInjection
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

/**
 * Automatically injects [Injectable] [Activity]'s and [Fragment]'s
 */
class AutoInjector @Inject constructor(private val application: Application) : AppService {

    override fun start() {
        application.doOnActivityCreated { activity, _ ->
            handleActivity(activity)
        }
    }

    private fun handleActivity(activity: Activity) {
        if (activity is Injectable && activity !is Ignore) {
            AndroidInjection.inject(activity)
        }

        if (activity is FragmentActivity && activity is HasSupportFragmentInjector) {
            activity.supportFragmentManager
                .doOnFragmentPreAttached(true) { _: FragmentManager, fragment: Fragment, _: Context ->
                    if (fragment is Injectable && fragment !is Ignore) {
                        AndroidSupportInjection.inject(fragment)
                    }
                }
        }
    }

    interface Ignore
}