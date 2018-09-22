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

package com.ivianuu.essentials.ui.base

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ivianuu.compass.fragment.CompassFragmentAppNavigatorPlugin
import com.ivianuu.essentials.injection.Injectable
import com.ivianuu.essentials.injection.view.HasViewInjector
import com.ivianuu.essentials.ui.common.BackListener
import com.ivianuu.essentials.ui.mvrx.MvRxView
import com.ivianuu.essentials.ui.traveler.RouterHolder
import com.ivianuu.essentials.util.ext.unsafeLazy
import com.ivianuu.essentials.util.lifecycle.LifecycleJob
import com.ivianuu.essentials.util.lifecycle.LifecycleOwner2
import com.ivianuu.essentials.util.screenlogger.IdentifiableScreen
import com.ivianuu.essentials.util.viewmodel.ViewModelFactoryHolder
import com.ivianuu.rxlifecycle.RxLifecycleOwner
import com.ivianuu.traveler.Navigator
import com.ivianuu.traveler.NavigatorHolder
import com.ivianuu.traveler.Router
import com.ivianuu.traveler.lifecycle.setNavigator
import com.ivianuu.traveler.plugin.NavigatorPlugin
import com.ivianuu.traveler.plugin.pluginNavigatorOf
import com.ivianuu.traveler.setRoot
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.android.Main
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

/**
 * Base activity
 */
abstract class BaseActivity : AppCompatActivity(), CoroutineScope, HasSupportFragmentInjector,
    HasViewInjector, Injectable, IdentifiableScreen, LifecycleOwner2, MvRxView, RouterHolder,
    RxLifecycleOwner, ViewModelFactoryHolder {

    @Inject lateinit var navigatorHolder: NavigatorHolder
    @Inject override lateinit var router: Router

    @Inject lateinit var supportFragmentInjector: DispatchingAndroidInjector<Fragment>
    @Inject lateinit var viewInjector: DispatchingAndroidInjector<View>

    @Inject override lateinit var viewModelFactory: ViewModelProvider.Factory

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    val job = LifecycleJob(this)

    protected open val layoutRes = -1

    open val fragmentContainer = android.R.id.content
    open val startDestination: Any? = null

    protected open val navigator: Navigator by unsafeLazy {
        val plugins = mutableListOf<NavigatorPlugin>()
        plugins.addAll(navigatorPlugins())
        plugins.add(CompassFragmentAppNavigatorPlugin(fragmentContainer))
        pluginNavigatorOf(plugins)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        if (layoutRes != -1) {
            setContentView(layoutRes)
        }

        if (savedInstanceState == null) {
            startDestination?.let { router.setRoot(it) }
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        navigatorHolder.setNavigator(this, navigator)
    }

    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(fragmentContainer)
        if (currentFragment is BackListener && currentFragment.handleBack()) return
        super.onBackPressed()
    }

    override fun invalidate() {
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = supportFragmentInjector

    override fun viewInjector(): AndroidInjector<View> = viewInjector

    protected open fun navigatorPlugins() = emptyList<NavigatorPlugin>()
}