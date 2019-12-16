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

package com.ivianuu.essentials.ui.compose.es

import android.os.Bundle
import android.view.ViewGroup
import androidx.compose.Composable
import androidx.lifecycle.viewModelScope
import androidx.ui.core.ambientDensity
import androidx.ui.core.setContent
import androidx.ui.foundation.isSystemInDarkTheme
import androidx.ui.material.MaterialTheme
import com.ivianuu.essentials.ui.base.EsActivity
import com.ivianuu.essentials.ui.base.EsViewModel
import com.ivianuu.essentials.ui.compose.common.MultiAmbientProvider
import com.ivianuu.essentials.ui.compose.common.OverlayState
import com.ivianuu.essentials.ui.compose.common.with
import com.ivianuu.essentials.ui.compose.core.AndroidComposeViewContainer
import com.ivianuu.essentials.ui.compose.core.MediaQuery
import com.ivianuu.essentials.ui.compose.core.MediaQueryProvider
import com.ivianuu.essentials.ui.compose.injekt.ComponentAmbient
import com.ivianuu.essentials.ui.compose.injekt.MaterialThemeProvider
import com.ivianuu.essentials.ui.compose.injekt.inject
import com.ivianuu.essentials.ui.compose.navigation.Navigator
import com.ivianuu.essentials.ui.compose.navigation.NavigatorState
import com.ivianuu.essentials.ui.compose.navigation.Route
import com.ivianuu.essentials.util.getViewModel
import com.ivianuu.essentials.util.unsafeLazy
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.get

private fun ComposeActivityModule(startRoute: Route) = Module {
    single {
        NavigatorState(
            overlayState = OverlayState(),
            coroutineScope = get<ComposeActivity>().getViewModel { CoroutineScopeViewModel() }.viewModelScope,
            startRoute = startRoute,
            handleBack = true
        )
    }
}

// todo remove
private class CoroutineScopeViewModel : EsViewModel()

abstract class ComposeActivity : EsActivity() {

    override fun retainedModules(): List<Module> = listOf(ComposeActivityModule(startRoute))

    protected abstract val startRoute: Route

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
        setContentView(composeContentView)
        composeContentView.setContent { ComposeWithAmbients(composeContentView) }
    }

    override fun onDestroy() {
        // todo use disposeComposition once fixed
        composeContentView.setContent { }
        super.onDestroy()
    }

    // todo move this to somewhere else
    @Composable
    protected open fun ComposeWithAmbients(view: AndroidComposeViewContainer) {
        MultiAmbientProvider(
            ActivityAmbient with this,
            ComponentAmbient with component
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

            MediaQueryProvider(value = mediaQuery) {
                val materialThemeProvider = inject<MaterialThemeProvider>()
                MaterialTheme(
                    colors = materialThemeProvider.colors(),
                    typography = materialThemeProvider.typography()
                ) {
                    content()
                }
            }
        }
    }

    @Composable
    protected open fun content() {
        Navigator(startRoute = startRoute)
    }
}
