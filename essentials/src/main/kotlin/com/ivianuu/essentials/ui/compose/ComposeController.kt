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

package com.ivianuu.essentials.ui.compose

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.Composable
import androidx.compose.memo
import androidx.compose.unaryPlus
import androidx.ui.core.ambientDensity
import androidx.ui.core.setContent
import androidx.ui.foundation.isSystemInDarkTheme
import androidx.ui.material.MaterialTheme
import com.ivianuu.director.requireActivity
import com.ivianuu.essentials.ui.base.EsController
import com.ivianuu.essentials.ui.compose.common.MultiAmbientProvider
import com.ivianuu.essentials.ui.compose.common.with
import com.ivianuu.essentials.ui.compose.core.ActivityAmbient
import com.ivianuu.essentials.ui.compose.core.AndroidComposeViewContainer
import com.ivianuu.essentials.ui.compose.core.ControllerAmbient
import com.ivianuu.essentials.ui.compose.core.MediaQuery
import com.ivianuu.essentials.ui.compose.core.MediaQueryProvider
import com.ivianuu.essentials.ui.compose.core.RouteAmbient
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.coroutines.collect
import com.ivianuu.essentials.ui.compose.injekt.ComponentAmbient
import com.ivianuu.essentials.ui.compose.injekt.MaterialThemeProvider
import com.ivianuu.essentials.ui.compose.injekt.inject

/**
 * Controller which uses compose to display it's ui
 */
abstract class ComposeController : EsController() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup) =
        AndroidComposeViewContainer(requireActivity()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            onViewCreated(this)
        }

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)
        view as AndroidComposeViewContainer
        view.setContent { ComposeWithAmbients(view) }
    }

    override fun onDestroyView(view: View) {
        super.onDestroyView(view)
        view as AndroidComposeViewContainer
        // todo use disposeComposition once fixed
        view.setContent { }
    }

    // todo move this to somewhere else
    @Composable
    protected open fun ComposeWithAmbients(view: AndroidComposeViewContainer) =
        composable("ComposeWithAmbients") {
        MultiAmbientProvider(
            ActivityAmbient with requireActivity(),
            RouteAmbient with route!!,
            ControllerAmbient with this,
            ComponentAmbient with component
        ) {
            val viewportMetrics =
                +collect(
                    +memo { AndroidComposeViewContainer.ViewportMetrics() },
                    view.viewportMetrics
                )

            val mediaQuery = MediaQuery(
                size = viewportMetrics.size,
                viewPadding = viewportMetrics.viewPadding,
                viewInsets = viewportMetrics.viewInsets,
                density = +ambientDensity(),
                darkMode = +isSystemInDarkTheme()
            )

            MediaQueryProvider(value = mediaQuery) {
                val materialThemeProvider = +inject<MaterialThemeProvider>()
                MaterialTheme(
                    colors = +materialThemeProvider.colors,
                    typography = +materialThemeProvider.typography
                ) {
                    composable("content") {
                        content()
                    }
                }
            }
        }
    }

    @Composable
    protected abstract fun content()
}