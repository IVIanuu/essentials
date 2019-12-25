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

package com.ivianuu.essentials.ui.material

import androidx.compose.Ambient
import androidx.compose.Composable
import androidx.compose.Immutable
import androidx.compose.ambient
import androidx.compose.remember
import androidx.ui.core.Alignment
import androidx.ui.core.CurrentTextStyleProvider
import androidx.ui.core.Dp
import androidx.ui.core.dp
import androidx.ui.graphics.Color
import androidx.ui.layout.Container
import androidx.ui.layout.EdgeInsets
import androidx.ui.layout.LayoutExpanded
import androidx.ui.layout.LayoutExpandedWidth
import androidx.ui.layout.LayoutWidth
import androidx.ui.layout.Spacer
import androidx.ui.material.MaterialTheme
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.core.Unstable
import com.ivianuu.essentials.ui.layout.CrossAxisAlignment
import com.ivianuu.essentials.ui.layout.MainAxisAlignment
import com.ivianuu.essentials.ui.layout.Row
import com.ivianuu.essentials.ui.layout.SpacingRow
import com.ivianuu.essentials.ui.layout.WithModifier
import com.ivianuu.essentials.ui.navigation.navigator
import com.ivianuu.essentials.ui.navigation.route

@Immutable
data class TopAppBarStyle(
    val color: Color,
    val elevation: Dp = DefaultAppBarElevation,
    val centerTitle: Boolean = false
)

@Composable
fun DefaultTopAppBarStyle(color: Color = MaterialTheme.colors().primary) = TopAppBarStyle(
    color = color
)

val TopAppBarStyleAmbient = Ambient.of<TopAppBarStyle?>()

@Composable
fun TopAppBar(title: String) {
    TopAppBar(title = { Text(title) })
}

@Unstable
@Composable
fun TopAppBar(
    style: TopAppBarStyle = ambient(TopAppBarStyleAmbient) ?: DefaultTopAppBarStyle(),
    title: (@Composable() () -> Unit)? = null,
    leading: (@Composable() () -> Unit)? = autoTopAppBarLeadingIcon(),
    actions: (@Composable() () -> Unit)? = null
) {
    Surface(color = style.color) {
        Container(
            height = AppBarHeight,
            modifier = LayoutExpandedWidth,
            padding = EdgeInsets(left = 16.dp, right = 16.dp)
        ) {
            Row(
                modifier = LayoutExpanded,
                mainAxisAlignment = MainAxisAlignment.SpaceBetween,
                crossAxisAlignment = CrossAxisAlignment.Center
            ) {
                if (leading != null) {
                    Container(
                        modifier = LayoutInflexible,
                        alignment = Alignment.CenterLeft
                    ) {
                        IconStyleAmbient.Provider(IconStyle()) {
                            leading()
                        }
                    }
                }

                if (title != null) {
                    if (leading != null) Spacer(LayoutWidth(16.dp))

                    WithModifier(
                        modifier = LayoutFlexible(1f)
                    ) {
                        CurrentTextStyleProvider(MaterialTheme.typography().h6) {
                            title()
                        }
                    }

                    if (actions != null) Spacer(LayoutWidth(16.dp))
                }

                if (actions != null) {
                    Container(alignment = Alignment.CenterRight) {
                        SpacingRow(
                            spacing = 8.dp,
                            modifier = LayoutInflexible
                        ) {
                            IconStyleAmbient.Provider(IconStyle()) {
                                actions()
                            }
                        }
                    }
                }
            }
        }
    }
}

private val AppBarHeight = 56.dp
private val DefaultAppBarElevation = 8.dp

@Composable
private fun autoTopAppBarLeadingIcon(): (@Composable() () -> Unit)? {
    val scaffold = scaffold
    val navigator = navigator
    val route = route
    val canGoBack = remember { navigator.backStack.indexOf(route) > 0 }
    return when {
        scaffold.hasDrawer -> {
            { DrawerButton() }
        }
        canGoBack -> {
            { BackButton() }
        }
        else -> null
    }
}
