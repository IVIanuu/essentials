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

package com.ivianuu.essentials.util.analytics

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
 * Utility class to help with screen logging
 */
@Singleton
class ScreenLogger @Inject constructor(application: Application) {

    private var listener: Listener? = null

    init {
        application.doOnActivityCreated { activity, savedInstanceState ->
            handleActivity(activity, savedInstanceState)
        }
    }

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    fun setListener(listener: (String) -> Unit) {
        this.listener = object : Listener {
            override fun screenLaunched(name: String) {
                listener.invoke(name)
            }
        }
    }

    private fun handleActivity(activity: Activity, savedInstanceState: Bundle?) {
        if (activity is NamedScreen
            && activity !is IgnoreNamedScreen
            && savedInstanceState == null) {
            listener?.screenLaunched(getName(activity))
        }

        if (activity is FragmentActivity) {
            activity.supportFragmentManager.doOnFragmentCreated(true) { _: FragmentManager, fragment: Fragment, bundle: Bundle? ->
                if (fragment is NamedScreen
                    && activity !is IgnoreNamedScreen
                    && bundle == null) {
                    listener?.screenLaunched(getName(fragment))
                }
            }
        }
    }

    private fun getName(screen: NamedScreen): String {
        return if (screen.screenName.isNotEmpty()) {
            screen.screenName
        } else {
            parseName(screen.javaClass.simpleName)
        }
    }

    private fun parseName(className: String): String {
        val withoutSuffix = when {
            className.endsWith(SUFFIX_ACTIVITY) -> className.replace(
                SUFFIX_ACTIVITY, "")
            className.endsWith(SUFFIX_DIALOG) -> className.replace(
                SUFFIX_DIALOG, "")
            className.endsWith(SUFFIX_FRAGMENT) -> className.replace(
                SUFFIX_FRAGMENT, "")
            else -> className
        }

        // there's probably a util function but i can't find it:D
        val regex = "([a-z])([A-Z]+)"
        val replacement = "$1_$2"
        return withoutSuffix
            .replace(regex.toRegex(), replacement)
            .toLowerCase()
    }

    interface Listener {
        fun screenLaunched(name: String)
    }

    class DefaultListener : Listener {
        override fun screenLaunched(name: String) {
            Analytics.log("screen launched: $name")
        }
    }

    private companion object {
        private const val SUFFIX_ACTIVITY = "Activity"
        private const val SUFFIX_DIALOG = "Dialog"
        private const val SUFFIX_FRAGMENT = "Fragment"
    }
}