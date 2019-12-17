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

package com.ivianuu.essentials.legacy.ui.base

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.lifecycleScope
import com.ivianuu.director.Router
import com.ivianuu.director.router
import com.ivianuu.essentials.legacy.ui.navigation.Navigator
import com.ivianuu.essentials.legacy.ui.navigation.director.ControllerRenderer
import com.ivianuu.essentials.legacy.ui.navigation.director.ControllerRoute
import com.ivianuu.essentials.ui.base.EsActivity
import com.ivianuu.essentials.util.coroutineScope
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.get
import com.ivianuu.injekt.inject
import com.ivianuu.scopes.android.onPause
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 * Base activity
 */
abstract class EsLegacyActivity : EsActivity() {

    override fun modules() = listOf(EsActivityModule(this))

    open val navigator: Navigator by inject()
    private val controllerRenderer: ControllerRenderer by inject()

    open val startRoute: ControllerRoute?
        get() = null

    private val onBackPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            navigator.pop()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // force router init
        get<Router>()

        if (navigator.backStack.isEmpty()) {
            startRoute?.let { navigator.push(it) }
        }

        onBackPressedDispatcher.addCallback(onBackPressedCallback)

        navigator.flow
            .onEach { onBackPressedCallback.isEnabled = it.size > 1 }
            .launchIn(lifecycleScope)
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        onPause.coroutineScope.launch {
            controllerRenderer.render(navigator)
        }
    }
}

private fun EsActivityModule(activity: EsActivity) = Module {
    single { activity.router(activity.containerId) }
}
