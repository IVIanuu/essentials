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
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ivianuu.compass.android.CompassAppNavigator
import com.ivianuu.compass.fragment.CompassFragmentNavigator
import com.ivianuu.essentials.ui.common.BackListener
import com.ivianuu.essentials.ui.mvrx.MvRxView
import com.ivianuu.essentials.ui.traveler.navigator.AddFragmentPlugin
import com.ivianuu.essentials.util.ViewModelFactoryHolder
import com.ivianuu.essentials.util.asMainCoroutineScope
import com.ivianuu.essentials.util.ext.unsafeLazy
import com.ivianuu.scopes.archlifecycle.onDestroy
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
import javax.inject.Inject

/**
 * Base activity
 */
abstract class BaseActivity : AppCompatActivity(), HasSupportFragmentInjector,
    MvRxView, ViewModelFactoryHolder {

    @Inject lateinit var navigatorHolder: NavigatorHolder
    @Inject lateinit var router: Router

    @Inject lateinit var supportFragmentInjector: DispatchingAndroidInjector<Fragment>

    @Inject override lateinit var viewModelFactory: ViewModelProvider.Factory

    val coroutineScope = onDestroy.asMainCoroutineScope()

    protected open val layoutRes = -1

    open val fragmentContainer = android.R.id.content
    open val startDestination: Any? = null

    protected open val navigator: Navigator by unsafeLazy {
        val navigators = mutableListOf<ResultNavigator>()
        navigators.addAll(navigators())
        navigators.add(CompassFragmentNavigator(fragmentContainer))
        navigators.add(CompassAppNavigator(this))
        navigators.add(AddFragmentPlugin(supportFragmentManager))
        compositeNavigatorOf(navigators)
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
        supportFragmentManager.fragments.filterIsInstance<BackListener>().forEach {
            if (it.handleBack()) {
                return
            }
        }

        super.onBackPressed()
    }

    override fun invalidate() {
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = supportFragmentInjector

    protected open fun navigators() = emptyList<ResultNavigator>()
}