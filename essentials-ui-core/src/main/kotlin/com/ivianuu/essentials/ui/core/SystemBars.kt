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

package com.ivianuu.essentials.ui.core

import android.os.Build
import android.view.View
import androidx.compose.foundation.AmbientContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.onCommit
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticAmbientOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.globalBounds
import androidx.compose.ui.onGloballyPositioned
import androidx.compose.ui.platform.DensityAmbient
import androidx.compose.ui.unit.Bounds
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Position
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.ivianuu.essentials.ui.UiDecorator
import com.ivianuu.essentials.ui.UiDecoratorBinding
import com.ivianuu.essentials.ui.common.compositionActivity
import com.ivianuu.essentials.util.setFlag
import com.ivianuu.injekt.FunBinding

@Composable
fun overlaySystemBarBgColor(color: Color) =
    if (color.isLight) Color.White.copy(alpha = 0.4f) else Color.Black.copy(alpha = 0.2f)

@Composable
fun Modifier.systemBarStyle(
    bgColor: Color = overlaySystemBarBgColor(MaterialTheme.colors.surface),
    lightIcons: Boolean = AmbientContentColor.current.isDark,
    elevation: Dp = 0.dp,
): Modifier = composed {
    val systemBarManager = SystemBarManagerAmbient.current
    var globalBounds by rememberState<Rect?> { null }
    val density = DensityAmbient.current

    onCommit(systemBarManager, globalBounds, density, bgColor, lightIcons, elevation) {
        val dpBounds = with(density) {
            Bounds(
                left = globalBounds?.left?.toInt()?.toDp() ?: 0.dp,
                top = globalBounds?.top?.toInt()?.toDp() ?: 0.dp,
                right = globalBounds?.right?.toInt()?.toDp() ?: 0.dp,
                bottom = globalBounds?.bottom?.toInt()?.toDp() ?: 0.dp
            )
        }

        val style = SystemBarStyle(bgColor, lightIcons, dpBounds, elevation)
        systemBarManager.registerStyle(style)
        onDispose { systemBarManager.unregisterStyle(style) }
    }

    onGloballyPositioned { globalBounds = it.globalBounds }
}

typealias ProvideSystemBarManager = UiDecorator

@UiDecoratorBinding
@FunBinding
@Composable
fun ProvideSystemBarManager(children: @Composable () -> Unit) {
    val systemBarManager = remember { SystemBarManager() }
    systemBarManager.updateSystemBars()
    Providers(
        SystemBarManagerAmbient provides systemBarManager,
        children = children
    )
}

private val SystemBarManagerAmbient = staticAmbientOf<SystemBarManager>()

@Immutable
data class SystemBarStyle(
    val barColor: Color,
    val lightIcons: Boolean,
    val bounds: Bounds,
    val elevation: Dp,
)

@Stable
private class SystemBarManager {

    val styles = mutableStateListOf<SystemBarStyle>()

    @Composable
    fun updateSystemBars() {
        val activity = compositionActivity
        onCommit(activity) {
            WindowCompat.setDecorFitsSystemWindows(activity.window, false)
            if (Build.VERSION.SDK_INT >= 29) {
                activity.window.isStatusBarContrastEnforced = false
                activity.window.isNavigationBarContrastEnforced = false
            }
        }

        val windowInsets = InsetsAmbient.current
        val density = DensityAmbient.current
        val screenHeight = with(density) {
            activity.window.decorView.height.toDp()
        }
        val screenWidth = with(density) {
            activity.window.decorView.width.toDp()
        }

        val statusBarHitPoint = remember(windowInsets) {
            Position(
                windowInsets.start,
                0.dp
            )
        }

        val statusBarStyle = remember(statusBarHitPoint, styles.toList()) {
            styles
                .sortedBy { it.elevation }
                .lastOrNull {
                    statusBarHitPoint.x >= it.bounds.left &&
                            statusBarHitPoint.y >= it.bounds.top &&
                            statusBarHitPoint.x <= it.bounds.right &&
                            statusBarHitPoint.y <= it.bounds.bottom
                }
        }

        onCommit(activity, statusBarStyle?.barColor) {
            activity.window.statusBarColor =
                (
                        statusBarStyle?.barColor ?: Color.Black
                            .copy(alpha = 0.2f)
                        ).toArgb()
        }

        onCommit(activity, statusBarStyle?.lightIcons) {
            activity.window.decorView.systemUiVisibility =
                activity.window.decorView.systemUiVisibility.setFlag(
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR,
                    statusBarStyle?.lightIcons ?: false
                )
        }

        if (Build.VERSION.SDK_INT >= 26) {
            val navBarHitPoint = remember(windowInsets, screenWidth, screenHeight) {
                when {
                    windowInsets.bottom != 0.dp -> {
                        Position(
                            windowInsets.start,
                            screenHeight - windowInsets.bottom
                        )
                    }
                    windowInsets.start != 0.dp -> {
                        Position(
                            0.dp,
                            screenHeight
                        )
                    }
                    windowInsets.end != 0.dp -> {
                        Position(
                            screenWidth - windowInsets.end,
                            screenHeight
                        )
                    }
                    else -> Position(Int.MAX_VALUE.dp, Int.MAX_VALUE.dp)
                }
            }

            val navBarStyle = remember(navBarHitPoint, styles.toList()) {
                styles
                    .sortedBy { it.elevation }
                    .lastOrNull {
                        navBarHitPoint.x >= it.bounds.left &&
                                navBarHitPoint.y >= it.bounds.top &&
                                navBarHitPoint.x <= it.bounds.right &&
                                navBarHitPoint.y <= it.bounds.bottom
                    }
            }

            onCommit(activity, navBarStyle?.barColor) {
                activity.window.navigationBarColor =
                    (
                            navBarStyle?.barColor ?: Color.Black
                                .copy(alpha = 0.2f)
                            ).toArgb()
            }

            onCommit(activity, navBarStyle?.lightIcons) {
                activity.window.decorView.systemUiVisibility =
                    activity.window.decorView.systemUiVisibility.setFlag(
                        View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR,
                        navBarStyle?.lightIcons ?: false
                    )
            }
        } else {
            onCommit(activity) {
                activity.window.navigationBarColor = Color.Black
                    .copy(alpha = 0.2f)
                    .toArgb()
            }
        }
    }

    fun registerStyle(style: SystemBarStyle) {
        styles += style
    }

    fun unregisterStyle(style: SystemBarStyle) {
        styles -= style
    }
}
