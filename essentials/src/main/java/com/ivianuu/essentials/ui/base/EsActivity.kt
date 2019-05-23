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
import com.ivianuu.essentials.ui.common.BackHandler
import com.ivianuu.essentials.ui.mvrx.MvRxView
import com.ivianuu.essentials.ui.traveler.EsFragmentNavigator
import com.ivianuu.essentials.ui.traveler.key.keyModule
import com.ivianuu.essentials.util.ext.unsafeLazy
import com.ivianuu.injekt.InjektTrait
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.android.activityComponent
import com.ivianuu.injekt.inject
import com.ivianuu.traveler.Navigator
import com.ivianuu.traveler.Router
import com.ivianuu.traveler.android.AppNavigator
import com.ivianuu.traveler.android.setNavigator
import com.ivianuu.traveler.common.ResultNavigator
import com.ivianuu.traveler.common.compositeNavigatorOf
import com.ivianuu.traveler.setRoot

/**
 * Base activity
 */
abstract class EsActivity : AppCompatActivity(), InjektTrait, MvRxView {

    override val component by unsafeLazy {
        activityComponent(
            modules = listOf(keyModule(intent.extras, false)) + modules()
        )
    }

    val router by inject<Router>()

    protected open val layoutRes get() = 0

    open val containerId
        get() = android.R.id.content

    open val startKey: Any?
        get() = null

    val fragmentNavigator by unsafeLazy {
        EsFragmentNavigator(supportFragmentManager, containerId)
    }

    protected open val navigator: Navigator by unsafeLazy {
        val navigators = mutableListOf<ResultNavigator>().apply {
            addAll(navigators())
            add(fragmentNavigator)
            add(AppNavigator(this@EsActivity))
        }

        compositeNavigatorOf(navigators)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        fragmentNavigator.restoreState(savedInstanceState)

        super.onCreate(savedInstanceState)

        if (layoutRes != 0) {
            setContentView(layoutRes)
        }

        if (savedInstanceState == null) {
            startKey?.let { router.setRoot(it) }
        }
    }

    override fun onStart() {
        super.onStart()
        invalidate()
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        router.setNavigator(this, navigator)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        fragmentNavigator.saveState(outState)
    }

    override fun onBackPressed() {
        val topFragment = fragmentNavigator.backStack.lastOrNull()
            ?.fragmentTag?.let { supportFragmentManager.findFragmentByTag(it) }

        if (topFragment is BackHandler && topFragment.handleBack()) {
            return
        }

        if (!fragmentNavigator.handleBack()) {
            super.onBackPressed()
        }
    }

    override fun invalidate() {
    }

    protected open fun navigators(): List<ResultNavigator> = emptyList()

    protected open fun modules(): List<Module> = emptyList()

}