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

import androidx.compose.Composable
import androidx.compose.Immutable
import androidx.compose.Providers
import androidx.compose.remember
import androidx.compose.staticAmbientOf
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.ProvideTextStyle
import androidx.ui.graphics.Color
import androidx.ui.layout.Arrangement
import androidx.ui.layout.Column
import androidx.ui.layout.Row
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.padding
import androidx.ui.layout.preferredHeight
import androidx.ui.material.MaterialTheme
import androidx.ui.unit.Dp
import androidx.ui.unit.dp
import com.ivianuu.essentials.ui.common.BackButton
import com.ivianuu.essentials.ui.common.DrawerButton
import com.ivianuu.essentials.ui.common.SafeArea
import com.ivianuu.essentials.ui.core.DefaultTextComposableStyle
import com.ivianuu.essentials.ui.core.ProvideSystemBarStyle
import com.ivianuu.essentials.ui.core.TextComposableStyleAmbient
import com.ivianuu.essentials.ui.core.ambientSystemBarStyle
import com.ivianuu.essentials.ui.core.currentOrElse
import com.ivianuu.essentials.ui.core.currentOrNull
import com.ivianuu.essentials.ui.navigation.NavigatorAmbient
import com.ivianuu.essentials.ui.navigation.RouteAmbient
import com.ivianuu.essentials.util.isLight

@Immutable
data class AppBarStyle(
    val backgroundColor: Color,
    val contentColor: Color,
    val elevation: Dp,
    val modifier: Modifier = Modifier
)

@Composable
fun DefaultAppBarStyle(
    backgroundColor: Color,
    contentColor: Color = guessingContentColorFor(backgroundColor),
    elevation: Dp = DefaultAppBarElevation,
    modifier: Modifier = Modifier
) = AppBarStyle(
    backgroundColor = backgroundColor,
    contentColor = contentColor,
    elevation = elevation,
    modifier = modifier
)

@Composable
fun PrimaryAppBarStyle(
    elevation: Dp = DefaultAppBarElevation,
    modifier: Modifier = Modifier
) = with(MaterialTheme.colors) {
    DefaultAppBarStyle(
        backgroundColor = primary,
        contentColor = onPrimary,
        elevation = elevation,
        modifier = modifier
    )
}

@Composable
fun SurfaceAppBarStyle(
    elevation: Dp = 0.dp,
    modifier: Modifier = Modifier
) = with(MaterialTheme.colors) {
    DefaultAppBarStyle(
        backgroundColor = surface,
        contentColor = onSurface,
        elevation = elevation,
        modifier = modifier
    )
}

val AppBarStyleAmbient = staticAmbientOf<AppBarStyle>()

@Composable
fun TopAppBar(
    modifier: Modifier = Modifier,
    style: AppBarStyle = AppBarStyleAmbient.currentOrElse { PrimaryAppBarStyle() },
    title: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = autoTopAppBarLeadingIcon(),
    actions: @Composable (() -> Unit)? = null,
    primary: Boolean = true // todo rename param
) {
    ProvideSystemBarStyle(
        value = ambientSystemBarStyle().let {
            if (primary) it.copy(lightStatusBar = style.backgroundColor.isLight) else it
        }
    ) {
        Surface(
            color = style.backgroundColor,
            elevation = style.elevation
        ) {
            SafeArea(
                top = primary,
                left = false,
                right = false,
                bottom = false
            ) {
                Row(
                    modifier = Modifier.preferredHeight(DefaultAppBarHeight)
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp)
                        .plus(style.modifier)
                        .plus(modifier),
                    verticalGravity = Alignment.CenterVertically
                ) {
                    leading?.invoke()
                    if (title != null) {
                        val startPadding = if (leading != null) 16.dp else 0.dp
                        val endPadding = if (actions != null) 16.dp else 0.dp
                        Column(
                            modifier = Modifier
                                .padding(start = startPadding, end = endPadding)
                                .weight(1f),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Providers(
                                TextComposableStyleAmbient provides DefaultTextComposableStyle(
                                    maxLines = 1
                                )
                            ) {
                                ProvideTextStyle(
                                    MaterialTheme.typography.h6,
                                    children = title
                                )
                            }
                        }
                    }

                    if (actions != null) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalGravity = Alignment.CenterVertically
                        ) {
                            actions()
                        }
                    }
                }
            }
        }
    }
}

private val DefaultAppBarHeight = 56.dp
private val DefaultAppBarElevation = 8.dp

@Composable
private fun autoTopAppBarLeadingIcon(): @Composable (() -> Unit)? {
    val scaffold = ScaffoldAmbient.currentOrNull
    val navigator = NavigatorAmbient.currentOrNull
    val route = RouteAmbient.currentOrNull
    val canGoBack = remember { navigator?.backStack?.indexOf(route)?.let { it > 0 } }
    return when {
        scaffold?.hasDrawer ?: false -> {
            { DrawerButton() }
        }
        canGoBack ?: false -> {
            { BackButton() }
        }
        else -> null
    }
}
