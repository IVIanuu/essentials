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
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.ivianuu.essentials.injection.Injectable
import com.ivianuu.essentials.injection.KtHasSupportFragmentInjector
import com.ivianuu.essentials.injection.KtHasViewInjector
import com.ivianuu.essentials.ui.common.back.BackHandler
import com.ivianuu.essentials.ui.traveler.navigator.KeyFragmentAppNavigator
import com.ivianuu.essentials.ui.traveler.setupRouter
import com.ivianuu.essentials.util.ext.unsafeLazy
import com.ivianuu.essentials.util.screenlogger.NamedScreen
import com.ivianuu.traveler.Navigator
import com.ivianuu.traveler.Router
import dagger.android.DispatchingAndroidInjector
import javax.inject.Inject

/**
 * Base activity
 */
abstract class BaseActivity : AppCompatActivity(), KtHasSupportFragmentInjector,
    KtHasViewInjector, Injectable, NamedScreen {

    @Inject lateinit var backHandler: BackHandler

    @Inject override lateinit var supportFragmentInjector: DispatchingAndroidInjector<Fragment>
    @Inject override lateinit var viewInjector: DispatchingAndroidInjector<View>

    protected open val layoutRes = -1

    open val fragmentContainer = android.R.id.content

    protected open val navigator: Navigator by unsafeLazy {
        KeyFragmentAppNavigator(
            this,
            supportFragmentManager,
            fragmentContainer
        )
    }

    lateinit var router: Router

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        router = setupRouter(navigator, fragmentContainer)

        if (layoutRes != -1) setContentView(layoutRes)
    }

    override fun onBackPressed() {
        if (!backHandler.handleBack(this)) {
            super.onBackPressed()
        }
    }
}