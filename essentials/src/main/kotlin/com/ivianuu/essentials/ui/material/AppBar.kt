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
import androidx.compose.staticAmbientOf
import androidx.ui.core.CurrentTextStyleProvider
import androidx.ui.core.Layout
import androidx.ui.core.ParentData
import androidx.ui.graphics.Color
import androidx.ui.layout.Container
import androidx.ui.layout.EdgeInsets
import androidx.ui.layout.LayoutWidth
import androidx.ui.material.MaterialTheme
import androidx.ui.unit.Dp
import androidx.ui.unit.dp
import androidx.ui.unit.ipx
import com.ivianuu.essentials.ui.common.SafeArea
import com.ivianuu.essentials.ui.core.ProvideSystemBarStyle
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.core.ambientSystemBarStyle
import com.ivianuu.essentials.ui.core.currentOrNull
import com.ivianuu.essentials.ui.core.looseMin
import com.ivianuu.essentials.ui.layout.Column
import com.ivianuu.essentials.ui.layout.CrossAxisAlignment
import com.ivianuu.essentials.ui.layout.MainAxisAlignment
import com.ivianuu.essentials.ui.layout.SpacingRow
import com.ivianuu.essentials.ui.navigation.NavigatorAmbient
import com.ivianuu.essentials.ui.navigation.RouteAmbient
import com.ivianuu.essentials.util.isLight

@Immutable
data class AppBarStyle(
    val color: Color,
    val contentColor: Color,
    val elevation: Dp,
    val centerTitle: Boolean
)

@Composable
fun AppBarStyle(
    color: Color,
    elevation: Dp = DefaultAppBarElevation,
    centerTitle: Boolean = false
) = AppBarStyle(
    color = color,
    contentColor = guessingContentColorFor(color),
    elevation = elevation,
    centerTitle = centerTitle
)

@Composable
fun PrimaryAppBarStyle(
    elevation: Dp = DefaultAppBarElevation,
    centerTitle: Boolean = false
) = with(MaterialTheme.colors()) {
    AppBarStyle(color = primary, contentColor = onPrimary, elevation = elevation, centerTitle = centerTitle)
}

@Composable
fun SurfaceAppBarStyle(
    elevation: Dp = 0.dp,
    centerTitle: Boolean = false
) = with(MaterialTheme.colors()) {
    AppBarStyle(color = surface, contentColor = onSurface, elevation = elevation, centerTitle = centerTitle)
}

val AppBarStyleAmbient =
    staticAmbientOf<AppBarStyle?> { null }

@Composable
fun TopAppBar(title: String) {
    TopAppBar(title = { Text(title) })
}

@Composable
fun TopAppBar(
    style: AppBarStyle = AppBarStyleAmbient.current ?: PrimaryAppBarStyle(),
    title: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = autoTopAppBarLeadingIcon(),
    actions: @Composable (() -> Unit)? = null,
    primary: Boolean = true
) {
    ProvideSystemBarStyle(
        value = ambientSystemBarStyle().let {
            if (primary) it.copy(lightStatusBar = style.color.isLight) else it
        },
    ) {
        Surface(
            color = style.color,
            elevation = style.elevation,
        ) {
            SafeArea(
                top = primary,
                left = false,
                right = false,
                bottom = false
            ) {
                Container(
                    height = AppBarHeight,
                    modifier = LayoutWidth.Fill,
                    padding = EdgeInsets(left = 16.dp, right = 16.dp)
                ) {
                    TopAppBarLayout(
                        centerTitle = style.centerTitle,
                        leading = leading,
                        title = title?.let {
                            {
                                Column(
                                    mainAxisAlignment = MainAxisAlignment.Start,
                                    crossAxisAlignment = CrossAxisAlignment.Start
                                ) {
                                    CurrentTextStyleProvider(MaterialTheme.typography().h6) {
                                        title()
                                    }
                                }
                            }
                        },
                        actions = actions?.let {
                            {
                                SpacingRow(spacing = 8.dp) {
                                    actions()
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TopAppBarLayout(
    centerTitle: Boolean,
    leading: @Composable (() -> Unit)?,
    title: @Composable (() -> Unit)?,
    actions: @Composable (() -> Unit)?
) {
    Layout(children = {
        if (leading != null) {
            ParentData(data = TopAppBarSlot.Leading) {
                leading()
            }
        }

        if (title != null) {
            ParentData(data = TopAppBarSlot.Title) {
                title()
            }
        }

        if (actions != null) {
            ParentData(data = TopAppBarSlot.Actions) {
                actions()
            }
        }
    }) { measurables, constraints ->
        val leadingMeasureable = measurables.singleOrNull { it.parentData == TopAppBarSlot.Leading }
        val titleMeasureable = measurables.singleOrNull { it.parentData == TopAppBarSlot.Title }
        val actionsMeasureable = measurables.singleOrNull { it.parentData == TopAppBarSlot.Actions }

        var childConstraints = constraints.looseMin()

        val leadingPlaceable = leadingMeasureable?.measure(childConstraints)
        if (leadingPlaceable != null) {
            childConstraints = childConstraints.copy(maxWidth = childConstraints.maxWidth - leadingPlaceable.width)
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
                val titleX = if (centerTitle) {
                    (width / 2) - titlePlaceable.width / 2
                } else if (leadingPlaceable != null) {
                    56.dp.toIntPx()
                } else {
                    0.ipx
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

private val AppBarHeight = 56.dp
private val DefaultAppBarElevation = 8.dp

@Composable
private fun autoTopAppBarLeadingIcon(): @Composable (() -> Unit)? {
    val scaffold = ScaffoldAmbient.currentOrNull
    val navigator = NavigatorAmbient.currentOrNull
    val route = RouteAmbient.currentOrNull
    val canGoBack = navigator?.backStack?.indexOf(route)?.let { it > 0 }
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
