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

@file:Suppress("NOTHING_TO_INLINE") // Aliases to other public API.

package com.ivianuu.essentials.util.ext

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.ViewModelStoreOwner
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.app.containerId
import android.support.v7.preference.Preference
import android.support.v7.widget.Toolbar
import android.view.View
import com.ivianuu.compass.CompassFragmentAppNavigator
import com.ivianuu.compass.CompassFragmentNavigator
import com.ivianuu.essentials.ui.traveler.TravelerStore
import com.ivianuu.essentials.ui.traveler.navigator.CompassFragmentSwapperNavigator
import com.ivianuu.essentials.ui.traveler.navigator.FragmentSwapperNavigator.HideStrategy
import com.ivianuu.traveler.Navigator
import com.ivianuu.traveler.Router
import com.ivianuu.traveler.lifecycleobserver.NavigatorLifecycleObserver

val Fragment.localRouter: Router
    get() {
        val parent = parentFragment
        return parent?.getRouter(containerId, false)
                ?: requireActivity().getRouter(containerId, false)
                ?: throw IllegalStateException("not attached")
    }

val Fragment.rootRouter: Router
    get() {
        val parent = parentFragment
        return parent?.rootRouter
                ?: requireActivity().getRouter(containerId, false)
                ?: throw IllegalStateException("not attached")
    }

inline fun ViewModelStoreOwner.getTravelerStore() =
    getViewModel<TravelerStore>()

inline fun ViewModelStoreOwner.getTraveler(containerId: Int) =
    getTravelerStore().getTraveler(containerId)

inline fun ViewModelStoreOwner.getTraveler(containerId: Int, createIfNeeded: Boolean) =
        getTravelerStore().getTraveler(containerId, createIfNeeded)

inline fun ViewModelStoreOwner.getNavigatorHolder(containerId: Int) =
    getTraveler(containerId).navigatorHolder

inline fun ViewModelStoreOwner.getNavigatorHolder(containerId: Int, createIfNeeded: Boolean) =
        getTraveler(containerId, createIfNeeded)?.navigatorHolder

inline fun ViewModelStoreOwner.getRouter(containerId: Int) =
    getTraveler(containerId).router

inline fun ViewModelStoreOwner.getRouter(containerId: Int, createIfNeeded: Boolean) =
        getTraveler(containerId, createIfNeeded)?.router

inline fun <T> T.setupRouter(
    navigator: Navigator,
    containerId: Int
): Router where T : ViewModelStoreOwner, T : LifecycleOwner {
    val traveler = getTraveler(containerId)
    NavigatorLifecycleObserver.start(this, navigator, traveler.navigatorHolder)
    return traveler.router
}

inline fun <T> T.removeRouter(containerId: Int) where T : ViewModelStoreOwner, T : LifecycleOwner {
    getTravelerStore().removeTraveler(containerId)
}

inline fun FragmentActivity.setupCompassFragmentRouter(containerId: Int, crossinline exit: () -> Unit = {}) =
    setupCompassFragmentRouter(supportFragmentManager, containerId, exit)

inline fun Fragment.setupCompassFragmentRouter(containerId: Int, crossinline exit: () -> Unit = {}) =
    setupCompassFragmentRouter(childFragmentManager, containerId, exit)

inline fun <T> T.setupCompassFragmentRouter(
    fm: FragmentManager,
    containerId: Int,
    crossinline exit: () -> Unit = {}
): Router where T : ViewModelStoreOwner, T : LifecycleOwner {
    val navigator = object : CompassFragmentNavigator(fm, containerId) {
        override fun exit() {
            exit.invoke()
        }
    }

    return setupRouter(navigator, containerId)
}

inline fun FragmentActivity.setupKeyFragmentAppRouter(containerId: Int) =
    setupKeyFragmentAppRouter(this, supportFragmentManager, containerId)

inline fun Fragment.setupKeyFragmentAppRouter(containerId: Int) =
    setupKeyFragmentAppRouter(requireActivity(), childFragmentManager, containerId)

inline fun <T> T.setupKeyFragmentAppRouter(
    activity: FragmentActivity, fm: FragmentManager, containerId: Int
): Router where T : ViewModelStoreOwner, T : LifecycleOwner {
    val navigator = CompassFragmentAppNavigator(activity, fm, containerId)
    return setupRouter(navigator, containerId)
}

inline fun FragmentActivity.setupKeyFragmentSwapperRouter(
    containerId: Int,
    hideStrategy: HideStrategy = HideStrategy.DETACH,
    swapOnReselection: Boolean = true
) = setupKeyFragmentSwapperRouter(
    supportFragmentManager, containerId,
    hideStrategy, swapOnReselection
)

inline fun Fragment.setupKeyFragmentSwapperRouter(
    containerId: Int,
    hideStrategy: HideStrategy = HideStrategy.DETACH,
    swapOnReselection: Boolean = true
) = setupKeyFragmentSwapperRouter(
    childFragmentManager, containerId,
    hideStrategy, swapOnReselection
)

inline fun <T> T.setupKeyFragmentSwapperRouter(
    fm: FragmentManager,
    containerId: Int,
    hideStrategy: HideStrategy = HideStrategy.DETACH,
    swapOnReselection: Boolean = true
): Router where T : ViewModelStoreOwner, T : LifecycleOwner {
    val navigator = CompassFragmentSwapperNavigator(
        fm,
        containerId, hideStrategy, swapOnReselection
    )
    return setupRouter(navigator, containerId)
}

inline fun Toolbar.exitOnNavigationClick(router: Router) {
    setNavigationOnClickListener { router.exit() }
}

inline fun View.navigateOnClick(router: Router, crossinline key: () -> Any) {
    setOnClickListener { router.navigateTo(key()) }
}

inline fun View.navigateOnClick(router: Router, key: Any) {
    setOnClickListener { router.navigateTo(key) }
}

inline fun Preference.navigateOnClick(router: Router, crossinline key: () -> Any) {
    setOnPreferenceClickListener {
        router.navigateTo(key())
        true
    }
}

inline fun Preference.navigateOnClick(router: Router, key: Any) {
    setOnPreferenceClickListener {
        router.navigateTo(key)
        true
    }
}