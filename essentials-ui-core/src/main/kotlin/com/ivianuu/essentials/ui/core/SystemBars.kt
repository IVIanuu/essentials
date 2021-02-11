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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpRect
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.ivianuu.essentials.ui.AppTheme
import com.ivianuu.essentials.ui.UiDecoratorBinding
import com.ivianuu.essentials.ui.UiDecoratorConfig
import com.ivianuu.essentials.ui.common.compositionActivity
import com.ivianuu.essentials.util.setFlag
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.GivenFun
import com.ivianuu.injekt.common.keyOf

@Composable
fun overlaySystemBarBgColor(color: Color) =
    if (color.isLight) Color.White.copy(alpha = 0.4f) else Color.Black.copy(alpha = 0.2f)

@Composable
fun Modifier.systemBarStyle(
    bgColor: Color = overlaySystemBarBgColor(MaterialTheme.colors.surface),
    lightIcons: Boolean = LocalContentColor.current.isDark,
    elevation: Dp = 0.dp,
): Modifier = composed {
    val systemBarManager = LocalSystemBarManager.current
    var globalBounds by rememberState<Rect?> { null }
    val density = LocalDensity.current

    DisposableEffect(systemBarManager, globalBounds, density, bgColor, lightIcons, elevation) {
        val dpBounds = with(density) {
            DpRect(
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

    onGloballyPositioned { globalBounds = it.boundsInWindow() }
}

@UiDecoratorBinding
@GivenFun
@Composable
fun ProvideSystemBarManager(content: @Composable () -> Unit) {
    val systemBarManager = remember { SystemBarManager() }
    systemBarManager.updateSystemBars()
    Providers(
        LocalSystemBarManager provides systemBarManager,
        content = content
    )
}

@Given
fun RootSystemBarsStyleConfig() = UiDecoratorConfig<RootSystemBarsStyle>(
    dependencies = setOf(keyOf<AppTheme>(), keyOf<ProvideSystemBarManager>())
)

@UiDecoratorBinding
@GivenFun
@Composable
fun RootSystemBarsStyle(content: @Composable () -> Unit) {
    Surface {
        Box(
            modifier = Modifier.fillMaxSize()
                .systemBarStyle()
        ) {
            content()
        }
    }
}

private val LocalSystemBarManager = staticCompositionLocalOf<SystemBarManager>()

private data class SystemBarStyle(
    val barColor: Color,
    val lightIcons: Boolean,
    val bounds: DpRect,
    val elevation: Dp,
)

private class SystemBarManager {

    val styles = mutableStateListOf<SystemBarStyle>()

    @Composable
    fun updateSystemBars() {
        val activity = compositionActivity
        DisposableEffect(activity) {
            WindowCompat.setDecorFitsSystemWindows(activity.window, false)
            if (Build.VERSION.SDK_INT >= 29) {
                activity.window.isStatusBarContrastEnforced = false
                activity.window.isNavigationBarContrastEnforced = false
            }
            onDispose { }
        }

        val layoutDirection = LocalLayoutDirection.current
        val windowInsets = LocalInsets.current
        val density = LocalDensity.current
        val screenHeight = with(density) {
            activity.window.decorView.height.toDp()
        }
        val screenWidth = with(density) {
            activity.window.decorView.width.toDp()
        }

        val statusBarHitPoint = remember(windowInsets) {
            DpOffset(
                windowInsets.calculateStartPadding(layoutDirection),
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

        DisposableEffect(activity, statusBarStyle?.barColor) {
            activity.window.statusBarColor =
                (
                        statusBarStyle?.barColor ?: Color.Black
                            .copy(alpha = 0.2f)
                        ).toArgb()
            onDispose {  }
        }

        DisposableEffect(activity, statusBarStyle?.lightIcons) {
            activity.window.decorView.systemUiVisibility =
                activity.window.decorView.systemUiVisibility.setFlag(
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR,
                    statusBarStyle?.lightIcons ?: false
                )
            onDispose { }
        }

        if (Build.VERSION.SDK_INT >= 26) {
            val navBarHitPoint = remember(windowInsets, screenWidth, screenHeight) {
                when {
                    windowInsets.calculateBottomPadding() != 0.dp -> {
                        DpOffset(
                            windowInsets.calculateLeftPadding(layoutDirection),
                            screenHeight - windowInsets.calculateBottomPadding()
                        )
                    }
                    windowInsets.calculateLeftPadding(layoutDirection) != 0.dp -> {
                        DpOffset(
                            0.dp,
                            screenHeight
                        )
                    }
                    windowInsets.calculateRightPadding(layoutDirection) != 0.dp -> {
                        DpOffset(
                            screenWidth - windowInsets.calculateRightPadding(layoutDirection),
                            screenHeight
                        )
                    }
                    else -> DpOffset(Int.MAX_VALUE.dp, Int.MAX_VALUE.dp)
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

            DisposableEffect(activity, navBarStyle?.barColor) {
                activity.window.navigationBarColor =
                    (
                            navBarStyle?.barColor ?: Color.Black
                                .copy(alpha = 0.2f)
                            ).toArgb()
                onDispose { }
            }

            DisposableEffect(activity, navBarStyle?.lightIcons) {
                activity.window.decorView.systemUiVisibility =
                    activity.window.decorView.systemUiVisibility.setFlag(
                        View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR,
                        navBarStyle?.lightIcons ?: false
                    )
                onDispose { }
            }
        } else {
            DisposableEffect(activity) {
                activity.window.navigationBarColor = Color.Black
                    .copy(alpha = 0.4f)
                    .toArgb()
                onDispose { }
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
