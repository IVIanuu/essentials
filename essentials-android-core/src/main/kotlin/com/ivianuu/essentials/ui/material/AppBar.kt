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
import androidx.ui.core.Layout
import androidx.ui.core.Modifier
import androidx.ui.core.tag
import androidx.ui.foundation.Box
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
import androidx.ui.unit.ipx
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
    val centerTitle: Boolean,
    val modifier: Modifier = Modifier
)

@Composable
fun DefaultAppBarStyle(
    backgroundColor: Color,
    contentColor: Color = guessingContentColorFor(backgroundColor),
    elevation: Dp = DefaultAppBarElevation,
    centerTitle: Boolean = false,
    modifier: Modifier = Modifier
) = AppBarStyle(
    backgroundColor = backgroundColor,
    contentColor = contentColor,
    elevation = elevation,
    centerTitle = centerTitle,
    modifier = modifier
)

@Composable
fun PrimaryAppBarStyle(
    elevation: Dp = DefaultAppBarElevation,
    centerTitle: Boolean = false,
    modifier: Modifier = Modifier
) = with(MaterialTheme.colors) {
    DefaultAppBarStyle(
        backgroundColor = primary,
        contentColor = onPrimary,
        elevation = elevation,
        centerTitle = centerTitle,
        modifier = modifier
    )
}

@Composable
fun SurfaceAppBarStyle(
    elevation: Dp = 0.dp,
    centerTitle: Boolean = false,
    modifier: Modifier = Modifier
) = with(MaterialTheme.colors) {
    DefaultAppBarStyle(
        backgroundColor = surface,
        contentColor = onSurface,
        elevation = elevation,
        centerTitle = centerTitle,
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
                TopAppBarLayout(
                    modifier = Modifier.preferredHeight(DefaultAppBarHeight)
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp)
                        .plus(style.modifier)
                        .plus(modifier),
                    centerTitle = style.centerTitle,
                    leading = leading,
                    title = title?.let {
                        {
                            Column {
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
                    },
                    actions = actions?.let {
                        {
                            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                                actions()
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun TopAppBarLayout(
    modifier: Modifier,
    centerTitle: Boolean,
    leading: @Composable (() -> Unit)?,
    title: @Composable (() -> Unit)?,
    actions: @Composable (() -> Unit)?
) {
    Layout(modifier = modifier, children = {
        if (leading != null) {
            Box(modifier = Modifier.tag(TopAppBarSlot.Leading)) {
                leading()
            }
        }

        if (title != null) {
            Box(modifier = Modifier.tag(TopAppBarSlot.Title)) {
                title()
            }
        }

        if (actions != null) {
            Box(modifier = Modifier.tag(TopAppBarSlot.Actions)) {
                actions()
            }
        }
    }) { measurables, constraints, _ ->
        val leadingMeasureable = measurables.singleOrNull { it.tag == TopAppBarSlot.Leading }
        val titleMeasureable = measurables.singleOrNull { it.tag == TopAppBarSlot.Title }
        val actionsMeasureable = measurables.singleOrNull { it.tag == TopAppBarSlot.Actions }

        var childConstraints = constraints.copy(minWidth = 0.ipx, minHeight = 0.ipx)

        val leadingPlaceable = leadingMeasureable?.measure(childConstraints)
        if (leadingPlaceable != null) {
            childConstraints =
                childConstraints.copy(maxWidth = childConstraints.maxWidth - leadingPlaceable.width)
        }

        val actionsPlaceable = actionsMeasureable?.measure(childConstraints)
        if (actionsPlaceable != null) {
            childConstraints = childConstraints.copy(maxWidth = childConstraints.maxWidth - actionsPlaceable.width)
        }

        if (leadingPlaceable != null) {
            childConstraints = childConstraints.copy(maxWidth = childConstraints.maxWidth - 56.dp.toIntPx())
        }
        if (actionsPlaceable != null) {
            childConstraints = childConstraints.copy(maxWidth = childConstraints.maxWidth - 16.dp.toIntPx())
        }

        val titlePlaceable = titleMeasureable?.measure(childConstraints)

        val width = constraints.maxWidth
        val height = constraints.maxHeight

        layout(width, height) {
            leadingPlaceable?.place(
                x = 0.ipx,
                y = height / 2 - leadingPlaceable.height / 2
            )

            if (titlePlaceable != null) {
                val titleX = when {
                    centerTitle -> (width / 2) - titlePlaceable.width / 2
                    leadingPlaceable != null -> 56.dp.toIntPx()
                    else -> 0.ipx
                }

                titlePlaceable.place(
                    x = titleX,
                    y = height / 2 - titlePlaceable.height / 2
                )
            }

            actionsPlaceable?.place(
                x = width - actionsPlaceable.width,
                y = height / 2 - actionsPlaceable.height / 2
            )
        }
    }
}

private enum class TopAppBarSlot {
    Leading, Title, Actions
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
