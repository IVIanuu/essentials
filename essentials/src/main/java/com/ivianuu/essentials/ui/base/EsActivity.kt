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
import com.ivianuu.director.Router
import com.ivianuu.director.attachRouter
import com.ivianuu.director.handleBack
import com.ivianuu.director.traveler.ControllerNavigator
import com.ivianuu.essentials.R
import com.ivianuu.essentials.util.asMainCoroutineScope
import com.ivianuu.essentials.util.ext.unsafeLazy
import com.ivianuu.injectors.CompositeInjectors
import com.ivianuu.injectors.HasInjectors
import com.ivianuu.injectors.android.inject
import com.ivianuu.scopes.archlifecycle.onDestroy
import com.ivianuu.traveler.Navigator
import com.ivianuu.traveler.NavigatorHolder
import com.ivianuu.traveler.android.AppNavigator
import com.ivianuu.traveler.common.ResultNavigator
import com.ivianuu.traveler.common.compositeNavigatorOf
import com.ivianuu.traveler.setRoot
import javax.inject.Inject

/**
 * Base activity
 */
abstract class EsActivity : AppCompatActivity(), HasInjectors {

    @Inject override lateinit var injectors: CompositeInjectors

    @Inject lateinit var navigatorHolder: NavigatorHolder
    @Inject lateinit var travelerRouter: com.ivianuu.traveler.Router

    val coroutineScope = onDestroy.asMainCoroutineScope()

    protected open val layoutRes get() = R.layout.activity_default

    open val containerId
        get() = R.id.container

    open val startKey: Any?
        get() = null

    lateinit var router: Router

    protected open val navigator: Navigator by unsafeLazy {
        val navigators = mutableListOf<ResultNavigator>()
        navigators.addAll(navigators())
        navigators.add(ControllerNavigator(router))
        navigators.add(AppNavigator(this))
        compositeNavigatorOf(navigators)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        inject()
        super.onCreate(savedInstanceState)

        setContentView(layoutRes)

        router = attachRouter(findViewById(containerId), savedInstanceState)
        navigatorHolder.setNavigator(navigator)

        if (savedInstanceState == null) {
            startKey?.let { travelerRouter.setRoot(it) }
        }
    }

    override fun onBackPressed() {
        if (!router.handleBack()) {
            super.onBackPressed()
        }
    }

    protected open fun navigators() = emptyList<ResultNavigator>()
}