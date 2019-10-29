/*
 * Copyright 2019 Manuel Wrage
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
import com.ivianuu.director.router
import com.ivianuu.essentials.ui.mvrx.MvRxView
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.director.ControllerRenderer
import com.ivianuu.essentials.ui.navigation.director.ControllerRoute
import com.ivianuu.essentials.util.coroutineScope
import com.ivianuu.essentials.util.unsafeLazy
import com.ivianuu.injekt.InjektTrait
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.android.activityComponent
import com.ivianuu.injekt.factory
import com.ivianuu.injekt.inject
import com.ivianuu.injekt.module
import com.ivianuu.scopes.android.onPause
import kotlinx.coroutines.launch

private fun esActivityModule(esActivity: EsActivity) = module {
    factory { esActivity.router(esActivity.containerId) }
}

/**
 * Base activity
 */
abstract class EsActivity : AppCompatActivity(), InjektTrait, MvRxView {

    override val component by unsafeLazy {
        activityComponent {
            modules(esActivityModule(this@EsActivity))
            modules(this@EsActivity.modules())
        }
    }

    var handleBack = true

    val navigator: Navigator by inject()
    private val controllerRenderer: ControllerRenderer by inject()

    protected open val layoutRes: Int get() = 0

    open val containerId: Int
        get() = android.R.id.content

    open val startRoute: ControllerRoute?
        get() = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (layoutRes != 0) {
            setContentView(layoutRes)
        }

        if (navigator.backStack.isEmpty()) {
            startRoute?.let { navigator.push(it) }
        }
    }

    override fun onStart() {
        super.onStart()
        invalidate()
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        onPause.coroutineScope.launch {
            controllerRenderer.render()
        }
    }

    override fun onBackPressed() {
        if (handleBack && navigator.backStack.size > 1) {
            navigator.pop()
        } else {
            super.onBackPressed()
        }
    }

    override fun invalidate() {
    }

    protected open fun modules(): List<Module> = emptyList()

}