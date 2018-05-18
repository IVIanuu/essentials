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

package com.ivianuu.essentials.util

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import com.ivianuu.essentials.util.ext.doOnActivityCreated
import com.ivianuu.essentials.util.ext.doOnFragmentCreated
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Util class to help with screen logging
 */
@Singleton
class ScreenLogger @Inject constructor(private val application: Application) {

    private var logger: Logger? = null

    init {
        application.doOnActivityCreated { activity, savedInstanceState ->
            handleActivity(activity, savedInstanceState)
        }
    }

    fun setLogger(logger: Logger?) {
        this.logger = logger
    }

    fun setLogger(logger: (String) -> Unit) {
        this.logger = object : Logger {
            override fun logScreenName(screenName: String) {
                logger.invoke(screenName)
            }
        }
    }

    private fun handleActivity(activity: Activity, savedInstanceState: Bundle?) {
        if (activity is NamedScreen
            && activity.screenName.isNotEmpty()
            && savedInstanceState == null) {
            logger?.logScreenName(activity.screenName)
        }

        if (activity is FragmentActivity) {
            activity.supportFragmentManager.doOnFragmentCreated(true) {
                    _: FragmentManager, fragment: Fragment, bundle: Bundle? ->
                if (fragment is NamedScreen
                    && fragment.screenName.isNotEmpty()
                    && bundle == null) {
                    logger?.logScreenName(fragment.screenName)
                }
            }
        }
    }

    interface Logger {
        fun logScreenName(screenName: String)
    }
}