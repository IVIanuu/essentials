/*
 * Copyright 2020 Manuel Wrage
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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.ui.LocalUiComponent
import com.ivianuu.essentials.ui.UiComponent
import com.ivianuu.essentials.ui.common.BackButton
import com.ivianuu.essentials.ui.core.InsetsPadding
import com.ivianuu.essentials.ui.core.isLight
import com.ivianuu.essentials.ui.core.overlaySystemBarBgColor
import com.ivianuu.essentials.ui.core.systemBarStyle
import com.ivianuu.essentials.ui.navigation.NavigationState
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.component.ComponentElementBinding
import com.ivianuu.injekt.component.get
import kotlinx.coroutines.flow.StateFlow

enum class TopAppBarStyle {
    Primary, Surface
}

val LocalTopAppBarStyle = compositionLocalOf { TopAppBarStyle.Primary }

@Composable
fun TopAppBar(
    modifier: Modifier = Modifier,
    title: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = autoTopAppBarLeadingIcon(),
    actions: @Composable (() -> Unit)? = null,
    backgroundColor: Color = when (LocalTopAppBarStyle.current) {
        TopAppBarStyle.Primary -> MaterialTheme.colors.primary
        TopAppBarStyle.Surface -> MaterialTheme.colors.surface
    },
    contentColor: Color = guessingContentColorFor(backgroundColor),
    elevation: Dp = DefaultAppBarElevation,
) {
    TopAppBar(
        modifier = modifier,
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        elevation = elevation
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
                    content = title
                )
            }
        }

        if (actions != null) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                actions()
            }
        }
    }
}

@Composable
fun TopAppBar(
    modifier: Modifier = Modifier,
    backgroundColor: Color = when (LocalTopAppBarStyle.current) {
        TopAppBarStyle.Primary -> MaterialTheme.colors.primary
        TopAppBarStyle.Surface -> MaterialTheme.colors.surface
    },
    contentColor: Color = guessingContentColorFor(backgroundColor),
    elevation: Dp = DefaultAppBarElevation,
    applySystemBarStyle: Boolean = true,
    content: @Composable RowScope.() -> Unit,
) {
    Surface(
        color = backgroundColor,
        contentColor = contentColor,
        elevation = elevation,
        modifier = (
                if (applySystemBarStyle) Modifier.systemBarStyle(
                    bgColor = overlaySystemBarBgColor(backgroundColor),
                    lightIcons = backgroundColor.isLight,
                    elevation = elevation
                ) else Modifier
                ).then(modifier)
    ) {
        InsetsPadding(start = false, end = false, bottom = false) {
            Row(
                modifier = Modifier.preferredHeight(DefaultAppBarHeight)
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                content = content
            )
        }
    }
}

private val DefaultAppBarHeight = 56.dp
private val DefaultAppBarElevation = 4.dp

@Composable
private fun autoTopAppBarLeadingIcon(): @Composable (() -> Unit)? {
    val component = LocalUiComponent.current.get<AutoTopAppBarComponent>()
    val canGoBack = remember {
        component.navigationState.value.backStack.size > 1
    }
    return when {
        canGoBack -> {
            { BackButton() }
        }
        else -> null
    }
}

@ComponentElementBinding<UiComponent>
@Given
class AutoTopAppBarComponent(
    @Given val navigationState: StateFlow<NavigationState>
)
