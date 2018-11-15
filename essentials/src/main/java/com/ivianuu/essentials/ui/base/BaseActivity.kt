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
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ivianuu.contributor.view.HasViewInjector
import com.ivianuu.director.Controller
import com.ivianuu.director.attachRouter
import com.ivianuu.director.contributor.HasControllerInjector
import com.ivianuu.director.traveler.ControllerNavigator
import com.ivianuu.essentials.R
import com.ivianuu.essentials.ui.mvrx.MvRxView
import com.ivianuu.essentials.ui.traveler.navigator.AddFragmentPlugin
import com.ivianuu.essentials.util.ViewModelFactoryHolder
import com.ivianuu.essentials.util.asMainCoroutineScope
import com.ivianuu.essentials.util.ext.unsafeLazy
import com.ivianuu.scopes.archlifecycle.onDestroy
import com.ivianuu.traveler.Navigator
import com.ivianuu.traveler.NavigatorHolder
import com.ivianuu.traveler.Router
import com.ivianuu.traveler.android.AppNavigator
import com.ivianuu.traveler.common.ResultNavigator
import com.ivianuu.traveler.common.compositeNavigatorOf
import com.ivianuu.traveler.fragment.FragmentNavigator
import com.ivianuu.traveler.lifecycle.setNavigator
import com.ivianuu.traveler.setRoot
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

/**
 * Base activity
 */
abstract class BaseActivity : AppCompatActivity(), OnBackPressedCallback,
    HasControllerInjector,
    HasSupportFragmentInjector,
    HasViewInjector,
    MvRxView, ViewModelFactoryHolder {

    @Inject lateinit var navigatorHolder: NavigatorHolder
    @Inject lateinit var travelerRouter: Router

    @Inject lateinit var controllerInjector: DispatchingAndroidInjector<Controller>
    @Inject lateinit var supportFragmentInjector: DispatchingAndroidInjector<Fragment>
    @Inject lateinit var viewInjector: DispatchingAndroidInjector<View>

    @Inject override lateinit var viewModelFactory: ViewModelProvider.Factory

    open val useDirector = false

    val coroutineScope = onDestroy.asMainCoroutineScope()

    protected open val layoutRes get() = R.layout.activity_default

    open val containerId
        get() = R.id.container

    open val startKey: Any?
        get() = null

    var router: com.ivianuu.director.Router? = null

    protected open val navigator: Navigator by unsafeLazy {
        val navigators = mutableListOf<ResultNavigator>()
        navigators.addAll(navigators())
        if (useDirector) {
            navigators.add(ControllerNavigator(router!!))
        } else {
            navigators.add(FragmentNavigator(containerId))
        }
        navigators.add(AppNavigator(this))
        navigators.add(AddFragmentPlugin(supportFragmentManager))
        compositeNavigatorOf(navigators)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        addOnBackPressedCallback(this)

        setContentView(layoutRes)

        if (useDirector) {
            router = attachRouter(findViewById(containerId), savedInstanceState)
        }

        if (savedInstanceState == null) {
            startKey?.let { travelerRouter.setRoot(it) }
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        navigatorHolder.setNavigator(this, navigator)
    }

    override fun invalidate() {
    }

    override fun handleOnBackPressed() = if (useDirector) {
        router!!.handleBack()
    } else {
        false
    }

    override fun controllerInjector(): AndroidInjector<Controller> = controllerInjector
    override fun supportFragmentInjector(): AndroidInjector<Fragment> = supportFragmentInjector
    override fun viewInjector(): AndroidInjector<View> = viewInjector

    protected open fun navigators() = emptyList<ResultNavigator>()
}