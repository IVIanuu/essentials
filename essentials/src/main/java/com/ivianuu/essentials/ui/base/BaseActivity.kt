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
import com.ivianuu.compass.android.CompassAppNavigator
import com.ivianuu.compass.director.CompassControllerNavigator
import com.ivianuu.compass.fragment.CompassFragmentNavigator
import com.ivianuu.contributor.director.HasControllerInjector
import com.ivianuu.contributor.view.HasViewInjector
import com.ivianuu.director.Controller
import com.ivianuu.director.attachRouter
import com.ivianuu.essentials.R
import com.ivianuu.essentials.injection.Injectable
import com.ivianuu.essentials.ui.common.BackListener
import com.ivianuu.essentials.ui.mvrx.MvRxView
import com.ivianuu.essentials.ui.traveler.RouterHolder
import com.ivianuu.essentials.ui.traveler.navigator.AddFragmentPlugin
import com.ivianuu.essentials.util.ext.unsafeLazy
import com.ivianuu.essentials.util.lifecycle.LifecycleJob
import com.ivianuu.essentials.util.lifecycle.LifecycleOwner2
import com.ivianuu.essentials.util.screenlogger.IdentifiableScreen
import com.ivianuu.essentials.util.viewmodel.ViewModelFactoryHolder
import com.ivianuu.rxlifecycle.RxLifecycleOwner
import com.ivianuu.traveler.Navigator
import com.ivianuu.traveler.NavigatorHolder
import com.ivianuu.traveler.Router
import com.ivianuu.traveler.common.ResultNavigator
import com.ivianuu.traveler.common.compositeNavigatorOf
import com.ivianuu.traveler.lifecycle.setNavigator
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
abstract class BaseActivity : AppCompatActivity(), CoroutineScope, HasControllerInjector,
    HasSupportFragmentInjector, HasViewInjector, Injectable, IdentifiableScreen, LifecycleOwner2,
    MvRxView, RouterHolder, RxLifecycleOwner, ViewModelFactoryHolder {

    @Inject lateinit var navigatorHolder: NavigatorHolder
    @Inject lateinit var travelerRouter: Router

    @Inject lateinit var controllerInjector: DispatchingAndroidInjector<Controller>
    @Inject lateinit var supportFragmentInjector: DispatchingAndroidInjector<Fragment>
    @Inject lateinit var viewInjector: DispatchingAndroidInjector<View>

    @Inject override lateinit var viewModelFactory: ViewModelProvider.Factory

    override val providedRouter: Router
        get() = travelerRouter

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    val job = LifecycleJob(this)

    protected open val layoutRes = -1

    open val fragmentContainer = R.id.container
    open val startDestination: Any? = null

    open val useDirector = false

    protected open val navigator: Navigator by unsafeLazy {
        val navigators = mutableListOf<ResultNavigator>()
        navigators.addAll(navigators())
        if (useDirector) {
            navigators.add(CompassControllerNavigator(router!!))
        } else {
            navigators.add(CompassFragmentNavigator(fragmentContainer))
        }

        navigators.add(CompassAppNavigator(this))
        navigators.add(AddFragmentPlugin(supportFragmentManager))
        compositeNavigatorOf(navigators)
    }

    private var router: com.ivianuu.director.Router? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        setContentView(if (layoutRes != -1) layoutRes else R.layout.activity_default)

        if (useDirector) {
            router = attachRouter(findViewById(fragmentContainer), savedInstanceState)
        }

        if (savedInstanceState == null) {
            startDestination?.let { travelerRouter.setRoot(it) }
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        navigatorHolder.setNavigator(this, navigator)
    }

    override fun onBackPressed() {
        if (useDirector) {
            if (!router!!.handleBack()) {
                super.onBackPressed()
            }
        } else {
            val currentFragment = supportFragmentManager.findFragmentById(fragmentContainer)
            if (currentFragment is BackListener && currentFragment.handleBack()) return
            super.onBackPressed()
        }
    }

    override fun invalidate() {
    }

    override fun controllerInjector(): AndroidInjector<Controller> = controllerInjector

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = supportFragmentInjector

    override fun viewInjector(): AndroidInjector<View> = viewInjector

    protected open fun navigators() = emptyList<ResultNavigator>()
}