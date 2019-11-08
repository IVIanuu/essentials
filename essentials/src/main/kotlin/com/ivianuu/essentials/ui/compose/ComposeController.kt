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
import android.widget.FrameLayout
import androidx.compose.Composable
import androidx.compose.unaryPlus
import androidx.core.view.children
import androidx.ui.core.AndroidComposeView
import androidx.ui.core.setContent
import androidx.ui.material.MaterialTheme
import com.ivianuu.director.requireActivity
import com.ivianuu.essentials.ui.base.EsController
import com.ivianuu.essentials.ui.compose.core.ActivityAmbient
import com.ivianuu.essentials.ui.compose.core.ControllerAmbient
import com.ivianuu.essentials.ui.compose.core.InsetsManager
import com.ivianuu.essentials.ui.compose.core.InsetsManagerAmbient
import com.ivianuu.essentials.ui.compose.core.RouteAmbient
import com.ivianuu.essentials.ui.compose.injekt.ComponentAmbient
import com.ivianuu.essentials.ui.compose.injekt.MaterialThemeProvider
import com.ivianuu.essentials.ui.compose.injekt.inject
import com.ivianuu.essentials.util.cast

/**
 * Controller which uses compose to display it's ui
 */
abstract class ComposeController : EsController() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup) =
        FrameLayout(requireActivity()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            onViewCreated(this)
        }

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)
        view.cast<ViewGroup>().setContent {
            val composeView = view.cast<ViewGroup>().children.first() as AndroidComposeView
            composeView.fitsSystemWindows = true
            composeWithAmbients(composeView)
        }
    }

    override fun onDestroyView(view: View) {
        super.onDestroyView(view)
        // todo use disposeComposition once fixed
        view.cast<ViewGroup>().setContent { }
    }

    @Composable
    protected open fun composeWithAmbients(view: AndroidComposeView) {
        ActivityAmbient.Provider(value = requireActivity()) {
            val insetsManager = InsetsManager(view)
            InsetsManagerAmbient.Provider(value = insetsManager) {
                RouteAmbient.Provider(value = route!!) {
                    ControllerAmbient.Provider(value = this) {
                        ComponentAmbient.Provider(value = component) {
                            val materialThemeProvider = +inject<MaterialThemeProvider>()
                            MaterialTheme(
                                colors = +materialThemeProvider.colors,
                                typography = +materialThemeProvider.typography
                            ) {
                                compose()
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    protected abstract fun compose()
}