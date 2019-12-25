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
import androidx.ui.core.CurrentTextStyleProvider
import androidx.ui.core.Dp
import androidx.ui.core.IntPx
import androidx.ui.core.Layout
import androidx.ui.core.ParentData
import androidx.ui.core.dp
import androidx.ui.core.looseMin
import androidx.ui.graphics.Color
import androidx.ui.layout.Container
import androidx.ui.layout.EdgeInsets
import androidx.ui.layout.LayoutExpandedWidth
import androidx.ui.material.MaterialTheme
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.core.Unstable
import com.ivianuu.essentials.ui.layout.Column
import com.ivianuu.essentials.ui.layout.CrossAxisAlignment
import com.ivianuu.essentials.ui.layout.MainAxisAlignment
import com.ivianuu.essentials.ui.layout.SpacingRow
import com.ivianuu.essentials.ui.navigation.navigator
import com.ivianuu.essentials.ui.navigation.route

@Immutable
data class TopAppBarStyle(
    val color: Color,
    val elevation: Dp = DefaultAppBarElevation,
    val centerTitle: Boolean = false
)

@Composable
fun DefaultTopAppBarStyle(
    color: Color = MaterialTheme.colors().primary,
    elevation: Dp = DefaultAppBarElevation,
    centerTitle: Boolean = false
) = TopAppBarStyle(
    color = color,
    elevation = elevation,
    centerTitle = centerTitle
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
            TopAppBarLayout(
                centerTitle = style.centerTitle,
                leading = leading?.let {
                    {
                        IconStyleAmbient.Provider(IconStyle()) {
                            leading()
                        }
                    }
                },
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
                            IconStyleAmbient.Provider(IconStyle()) {
                                actions()
                            }
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun TopAppBarLayout(
    centerTitle: Boolean,
    leading: @Composable() (() -> Unit)?,
    title: @Composable() (() -> Unit)?,
    actions: @Composable() (() -> Unit)?
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
    }) { measureables, constraints ->
        val leadingMeasureable = measureables.singleOrNull { it.parentData == TopAppBarSlot.Leading }
        val titleMeasureable = measureables.singleOrNull { it.parentData == TopAppBarSlot.Title }
        val actionsMeasureable = measureables.singleOrNull { it.parentData == TopAppBarSlot.Actions }

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
                x = IntPx.Zero,
                y = height / 2 - leadingPlaceable.height / 2
            )

            if (titlePlaceable != null) {
                val titleX = if (centerTitle) {
                    (width / 2) - titlePlaceable.width / 2
                } else if (leadingPlaceable != null ) {
                    56.dp.toIntPx()
                } else {
                    IntPx.Zero
                }

                d { "title x $titleX" }

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
