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
import androidx.compose.ambientOf
import androidx.compose.remember
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
import androidx.ui.material.contentColorFor
import androidx.ui.material.primarySurface
import androidx.ui.unit.Dp
import androidx.ui.unit.dp
import com.ivianuu.essentials.ui.common.BackButton
import com.ivianuu.essentials.ui.common.DrawerButton
import com.ivianuu.essentials.ui.common.SafeArea
import com.ivianuu.essentials.ui.core.ProvideSystemBarStyle
import com.ivianuu.essentials.ui.core.ambientSystemBarStyle
import com.ivianuu.essentials.ui.core.currentOrNull
import com.ivianuu.essentials.ui.navigation.NavigatorAmbient
import com.ivianuu.essentials.util.isLight

enum class TopAppBarStyle {
    Primary, Surface
}

val TopAppBarStyleAmbient = ambientOf { TopAppBarStyle.Primary }

@Composable
fun TopAppBar(
    modifier: Modifier = Modifier,
    backgroundColor: Color = when (TopAppBarStyleAmbient.current) {
        TopAppBarStyle.Primary -> MaterialTheme.colors.primary
        TopAppBarStyle.Surface -> MaterialTheme.colors.surface
    },
    contentColor: Color = contentColorFor(backgroundColor),
    elevation: Dp = DefaultAppBarElevation,
    title: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = autoTopAppBarLeadingIcon(),
    actions: @Composable (() -> Unit)? = null,
    primary: Boolean = true // todo rename param
) {
    ProvideSystemBarStyle(
        value = ambientSystemBarStyle().let {
            if (primary) it.copy(lightStatusBar = backgroundColor.isLight) else it
        }
    ) {
        Surface(
            color = backgroundColor,
            contentColor = contentColor,
            elevation = elevation
        ) {
            SafeArea(
                top = primary,
                start = false,
                end = false,
                bottom = false
            ) {
                Row(
                    modifier = Modifier.preferredHeight(DefaultAppBarHeight)
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp)
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
                            ProvideTextStyle(
                                MaterialTheme.typography.h6,
                                children = title
                            )
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
private val DefaultAppBarElevation = 4.dp

@Composable
private fun autoTopAppBarLeadingIcon(): @Composable (() -> Unit)? {
    val navigator = NavigatorAmbient.currentOrNull
    val canGoBack = remember {
        (navigator?.backStack?.size ?: 0) > 1 ||
                (navigator?.popsLastRoute ?: false && navigator?.backStack?.isNotEmpty() ?: false)
    }
    return when {
        ScaffoldAmbient.currentOrNull?.hasDrawer ?: false -> {
            { DrawerButton() }
        }
        canGoBack -> {
            { BackButton() }
        }
        else -> null
    }
}
