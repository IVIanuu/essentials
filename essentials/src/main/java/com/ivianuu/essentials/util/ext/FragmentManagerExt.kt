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

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.View

// todo findFragmentBy**<SomeFragment>, requireFragmentBy**<SomeFragment>

fun FragmentManager.doOnFragmentPreAttached(
    recursive: Boolean,
    action: (fm: FragmentManager, f: Fragment, context: Context) -> Unit
) =
    registerFragmentLifecycleCallbacks(recursive, onFragmentPreAttached = action)

fun FragmentManager.doOnFragmentAttached(
    recursive: Boolean,
    action: (fm: FragmentManager, f: Fragment, context: Context) -> Unit
) =
    registerFragmentLifecycleCallbacks(recursive, onFragmentAttached = action)

fun FragmentManager.doOnFragmentPreCreated(
    recursive: Boolean,
    action: (fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) -> Unit
) =
    registerFragmentLifecycleCallbacks(recursive, onFragmentPreCreated = action)

fun FragmentManager.doOnFragmentCreated(
    recursive: Boolean,
    action: (fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) -> Unit
) =
    registerFragmentLifecycleCallbacks(recursive, onFragmentCreated = action)

fun FragmentManager.doOnFragmentActivityCreated(
    recursive: Boolean,
    action: (fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) -> Unit
) =
    registerFragmentLifecycleCallbacks(recursive, onFragmentActivityCreated = action)

fun FragmentManager.doOnFragmentViewCreated(
    recursive: Boolean,
    action: (fm: FragmentManager, f: Fragment, view: View, savedInstanceState: Bundle?) -> Unit
) =
    registerFragmentLifecycleCallbacks(recursive, onFragmentViewCreated = action)

fun FragmentManager.doOnFragmentStarted(
    recursive: Boolean,
    action: (fm: FragmentManager, f: Fragment) -> Unit
) =
    registerFragmentLifecycleCallbacks(recursive, onFragmentStarted = action)

fun FragmentManager.doOnFragmentResumed(
    recursive: Boolean,
    action: (fm: FragmentManager, f: Fragment) -> Unit
) =
    registerFragmentLifecycleCallbacks(recursive, onFragmentResumed = action)

fun FragmentManager.doOnFragmentPaused(
    recursive: Boolean,
    action: (fm: FragmentManager, f: Fragment) -> Unit
) =
    registerFragmentLifecycleCallbacks(recursive, onFragmentPaused = action)

fun FragmentManager.doOnFragmentStopped(
    recursive: Boolean,
    action: (fm: FragmentManager, f: Fragment) -> Unit
) =
    registerFragmentLifecycleCallbacks(recursive, onFragmentStopped = action)

fun FragmentManager.doOnFragmentSaveInstanceState(
    recursive: Boolean,
    action: (fm: FragmentManager, f: Fragment, outState: Bundle) -> Unit
) =
    registerFragmentLifecycleCallbacks(recursive, onFragmentSaveInstanceState = action)

fun FragmentManager.doOnFragmentViewDestroyed(
    recursive: Boolean,
    action: (fm: FragmentManager, f: Fragment) -> Unit
) =
    registerFragmentLifecycleCallbacks(recursive, onFragmentViewDestroyed = action)

fun FragmentManager.doOnFragmentDestroyed(
    recursive: Boolean,
    action: (fm: FragmentManager, f: Fragment) -> Unit
) =
    registerFragmentLifecycleCallbacks(recursive, onFragmentDestroyed = action)

fun FragmentManager.doOnFragmentDetached(
    recursive: Boolean,
    action: (fm: FragmentManager, f: Fragment) -> Unit
) =
    registerFragmentLifecycleCallbacks(recursive, onFragmentDetached = action)

fun FragmentManager.registerFragmentLifecycleCallbacks(
    recursive: Boolean,
    onFragmentPreAttached: ((fm: FragmentManager, f: Fragment, context: Context) -> Unit)? = null,
    onFragmentAttached: ((fm: FragmentManager, f: Fragment, context: Context) -> Unit)? = null,
    onFragmentPreCreated: ((fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) -> Unit)? = null,
    onFragmentCreated: ((fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) -> Unit)? = null,
    onFragmentActivityCreated: ((fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) -> Unit)? = null,
    onFragmentViewCreated: ((fm: FragmentManager, f: Fragment, view: View, savedInstanceState: Bundle?) -> Unit)? = null,
    onFragmentStarted: ((fm: FragmentManager, f: Fragment) -> Unit)? = null,
    onFragmentResumed: ((fm: FragmentManager, f: Fragment) -> Unit)? = null,
    onFragmentPaused: ((fm: FragmentManager, f: Fragment) -> Unit)? = null,
    onFragmentStopped: ((fm: FragmentManager, f: Fragment) -> Unit)? = null,
    onFragmentSaveInstanceState: ((fm: FragmentManager, f: Fragment, outState: Bundle) -> Unit)? = null,
    onFragmentViewDestroyed: ((fm: FragmentManager, f: Fragment) -> Unit)? = null,
    onFragmentDestroyed: ((fm: FragmentManager, f: Fragment) -> Unit)? = null,
    onFragmentDetached: ((fm: FragmentManager, f: Fragment) -> Unit)? = null
): FragmentManager.FragmentLifecycleCallbacks {
    val callbacks = object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentPreAttached(fm: FragmentManager, f: Fragment, context: Context) {
            onFragmentPreAttached?.invoke(fm, f, context)
        }

        override fun onFragmentAttached(fm: FragmentManager, f: Fragment, context: Context) {
            onFragmentAttached?.invoke(fm, f, context)
        }

        override fun onFragmentPreCreated(
            fm: FragmentManager,
            f: Fragment,
            savedInstanceState: Bundle?
        ) {
            onFragmentPreCreated?.invoke(fm, f, savedInstanceState)
        }

        override fun onFragmentCreated(
            fm: FragmentManager,
            f: Fragment,
            savedInstanceState: Bundle?
        ) {
            onFragmentCreated?.invoke(fm, f, savedInstanceState)
        }

        override fun onFragmentActivityCreated(
            fm: FragmentManager,
            f: Fragment,
            savedInstanceState: Bundle?
        ) {
            onFragmentActivityCreated?.invoke(fm, f, savedInstanceState)
        }

        override fun onFragmentViewCreated(
            fm: FragmentManager,
            f: Fragment,
            v: View,
            savedInstanceState: Bundle?
        ) {
            onFragmentViewCreated?.invoke(fm, f, v, savedInstanceState)
        }

        override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
            onFragmentStarted?.invoke(fm, f)
        }

        override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
            onFragmentResumed?.invoke(fm, f)
        }

        override fun onFragmentPaused(fm: FragmentManager, f: Fragment) {
            onFragmentPaused?.invoke(fm, f)
        }

        override fun onFragmentStopped(fm: FragmentManager, f: Fragment) {
            onFragmentStopped?.invoke(fm, f)
        }

        override fun onFragmentSaveInstanceState(
            fm: FragmentManager,
            f: Fragment,
            outState: Bundle
        ) {
            onFragmentSaveInstanceState?.invoke(fm, f, outState)
        }

        override fun onFragmentViewDestroyed(fm: FragmentManager, f: Fragment) {
            onFragmentViewDestroyed?.invoke(fm, f)
        }

        override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
            onFragmentDestroyed?.invoke(fm, f)
        }

        override fun onFragmentDetached(fm: FragmentManager, f: Fragment) {
            onFragmentDetached?.invoke(fm, f)
        }
    }
    registerFragmentLifecycleCallbacks(callbacks, recursive)
    return callbacks
}