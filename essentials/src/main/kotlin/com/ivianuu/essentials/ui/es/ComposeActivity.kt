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

package com.ivianuu.essentials.ui.es

import android.os.Bundle
import android.view.ViewGroup
import androidx.compose.Composable
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.ui.core.CoroutineContextAmbient
import androidx.ui.core.ambientDensity
import androidx.ui.core.setContent
import androidx.ui.foundation.isSystemInDarkTheme
import com.ivianuu.essentials.ui.base.EsActivity
import com.ivianuu.essentials.ui.base.EsViewModel
import com.ivianuu.essentials.ui.common.MultiAmbientProvider
import com.ivianuu.essentials.ui.common.with
import com.ivianuu.essentials.ui.core.AndroidComposeViewContainer
import com.ivianuu.essentials.ui.core.MediaQuery
import com.ivianuu.essentials.ui.core.MediaQueryProvider
import com.ivianuu.essentials.ui.injekt.ComponentAmbient
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.NavigatorState
import com.ivianuu.essentials.ui.navigation.Route
import com.ivianuu.essentials.util.getViewModel
import com.ivianuu.essentials.util.unsafeLazy
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.get

abstract class ComposeActivity : EsActivity() {

    override fun retainedModules(): List<Module> =
        listOf(RouteCompatModule(this, startRoute))

    protected open val startRoute: Route? = null

    private val composeContentView by unsafeLazy {
        AndroidComposeViewContainer(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container = findViewById<ViewGroup>(containerId)
        container.addView(composeContentView)
        composeContentView.setContent {
            ComposeWithAmbients(composeContentView) {
                wrapContent {
                    content()
                }
            }
        }
    }

    override fun onDestroy() {
        // todo use disposeComposition once fixed
        composeContentView.setContent { }
        super.onDestroy()
    }

    // todo move this to somewhere else
    @Composable
    protected open fun ComposeWithAmbients(
        view: AndroidComposeViewContainer,
        children: @Composable() () -> Unit
    ) {
        MultiAmbientProvider(
            ActivityAmbient with this,
            ComponentAmbient with component,
            CoroutineContextAmbient with lifecycleScope.coroutineContext
        ) {
            val viewportMetrics = view.viewportMetrics
            val density = ambientDensity()
            val isDarkTheme = isSystemInDarkTheme()

            val mediaQuery = MediaQuery(
                size = viewportMetrics.size,
                viewPadding = viewportMetrics.viewPadding,
                viewInsets = viewportMetrics.viewInsets,
                density = density,
                darkMode = isDarkTheme
            )

            MediaQueryProvider(value = mediaQuery, children = children)
        }
    }

    @Composable
    protected open fun wrapContent(content: @Composable() () -> Unit) {
        content()
    }

    @Composable
    protected open fun content() {
        Navigator(state = get())
    }
}

private fun RouteCompatModule(
    activity: ComposeActivity,
    startRoute: Route?
) = Module {
    single {
        NavigatorState(
            coroutineScope = activity.getViewModel { CoroutineScopeViewModel() }.viewModelScope,
            startRoute = startRoute
        )
    }
}

// todo remove
private class CoroutineScopeViewModel : EsViewModel()