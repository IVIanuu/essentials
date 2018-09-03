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

import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.ivianuu.compass.CompassFragmentAppNavigator
import com.ivianuu.essentials.injection.Injectable
import com.ivianuu.essentials.ui.common.BackListener
import com.ivianuu.essentials.ui.traveler.RouterHolder
import com.ivianuu.essentials.util.ViewModelFactoryHolder
import com.ivianuu.essentials.util.ext.unsafeLazy
import com.ivianuu.essentials.util.screenlogger.IdentifiableScreen
import com.ivianuu.traveler.Navigator
import com.ivianuu.traveler.NavigatorHolder
import com.ivianuu.traveler.Router
import com.ivianuu.traveler.setNavigator
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * Base activity
 */
abstract class BaseActivity : AppCompatActivity(), HasSupportFragmentInjector, Injectable,
    IdentifiableScreen, RouterHolder, ViewModelFactoryHolder {

    @Inject lateinit var navigatorHolder: NavigatorHolder
    @Inject override lateinit var router: Router

    @Inject lateinit var supportFragmentInjector: DispatchingAndroidInjector<Fragment>

    @Inject override lateinit var viewModelFactory: ViewModelProvider.Factory

    open val startDestination: Any? = null

    protected open val layoutRes = -1

    open val fragmentContainer = android.R.id.content

    protected open val navigator: Navigator by unsafeLazy {
        CompassFragmentAppNavigator(
            this,
            supportFragmentManager,
            fragmentContainer
        )
    }

    protected val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        navigatorHolder.setNavigator(this, navigator)

        if (layoutRes != -1) setContentView(layoutRes)

        if (savedInstanceState == null) {
            startDestination?.let { router.newRoot(it) }
        }
    }

    override fun onDestroy() {
        disposables.clear()
        super.onDestroy()
    }

    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(fragmentContainer)
        if (currentFragment is BackListener && currentFragment.handleBack()) return
        super.onBackPressed()
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = supportFragmentInjector
}