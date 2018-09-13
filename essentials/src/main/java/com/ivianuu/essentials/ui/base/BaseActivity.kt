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
import com.ivianuu.compass.CompassFragmentAppNavigator
import com.ivianuu.essentials.injection.Injectable
import com.ivianuu.essentials.injection.view.HasViewInjector
import com.ivianuu.essentials.ui.common.BackListener
import com.ivianuu.essentials.ui.mvrx.MvRxView
import com.ivianuu.essentials.ui.traveler.RouterHolder
import com.ivianuu.essentials.util.ext.unsafeLazy
import com.ivianuu.essentials.util.lifecycle.LifecycleCoroutineScope
import com.ivianuu.essentials.util.lifecycle.LifecycleOwner2
import com.ivianuu.essentials.util.screenlogger.IdentifiableScreen
import com.ivianuu.essentials.util.viewmodel.ViewModelFactoryHolder
import com.ivianuu.traveler.Navigator
import com.ivianuu.traveler.NavigatorHolder
import com.ivianuu.traveler.Router
import com.ivianuu.traveler.extension.setNavigator
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.coroutines.Job
import javax.inject.Inject

/**
 * Base activity
 */
abstract class BaseActivity : AppCompatActivity(), HasSupportFragmentInjector, HasViewInjector,
    Injectable, IdentifiableScreen, LifecycleCoroutineScope, LifecycleOwner2, MvRxView,
    RouterHolder,
    ViewModelFactoryHolder {

    @Inject lateinit var navigatorHolder: NavigatorHolder
    @Inject override lateinit var router: Router

    @Inject lateinit var supportFragmentInjector: DispatchingAndroidInjector<Fragment>
    @Inject lateinit var viewInjector: DispatchingAndroidInjector<View>

    @Inject override lateinit var viewModelFactory: ViewModelProvider.Factory

    override val job = Job()

    protected open val layoutRes = -1

    open val fragmentContainer = android.R.id.content
    open val startDestination: Any? = null

    protected open val navigator: Navigator by unsafeLazy {
        CompassFragmentAppNavigator(
            this,
            supportFragmentManager,
            fragmentContainer
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        initCoroutineScope()

        navigatorHolder.setNavigator(this, navigator)

        if (layoutRes != -1) setContentView(layoutRes)

        if (savedInstanceState == null) {
            startDestination?.let { router.newRootScreen(it) }
        }
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
}