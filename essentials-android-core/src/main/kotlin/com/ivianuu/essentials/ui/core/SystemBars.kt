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

package com.ivianuu.essentials.ui.core

import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.state
import androidx.compose.runtime.staticAmbientOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.onPositioned
import androidx.compose.foundation.contentColor
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.onPreCommit
import androidx.compose.ui.layout.globalBounds
import androidx.compose.ui.platform.DensityAmbient
import androidx.compose.ui.unit.Bounds
import androidx.compose.ui.unit.Position
import androidx.compose.ui.unit.PxBounds
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.ui.common.compositionActivity
import com.ivianuu.essentials.util.addFlag
import com.ivianuu.essentials.util.isDark
import com.ivianuu.essentials.util.isLight
import com.ivianuu.essentials.util.setFlag

@Composable
fun overlaySystemBarBgColor(color: Color) =
    if (color.isLight) Color.White.copy(alpha = 0.4f) else Color.Black.copy(alpha = 0.2f)

@Composable
fun Modifier.systemBarStyle(
    bgColor: Color = overlaySystemBarBgColor(MaterialTheme.colors.surface),
    lightIcons: Boolean = contentColor().isDark
): Modifier = composed {
    val systemBarManager = SystemBarManagerAmbient.current
    var globalBounds by state<PxBounds?> { null }
    val density = DensityAmbient.current

    onPreCommit(systemBarManager, globalBounds, density, bgColor, lightIcons) {
        val dpBounds = with(density) {
            Bounds(
                left = globalBounds?.left?.toInt()?.toDp() ?: 0.dp,
                top = globalBounds?.top?.toInt()?.toDp() ?: 0.dp,
                right = globalBounds?.right?.toInt()?.toDp() ?: 0.dp,
                bottom = globalBounds?.bottom?.toInt()?.toDp() ?: 0.dp
            )
        }

        val style = SystemBarStyle(bgColor, lightIcons, dpBounds)
        systemBarManager.registerStyle(style)
        onDispose { systemBarManager.unregisterStyle(style) }
    }

    onPositioned { globalBounds = it.globalBounds }
}

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
    val bounds: Bounds
)

@Stable
private class SystemBarManager {

    val styles = mutableStateListOf<SystemBarStyle>()

    @Composable
    fun updateSystemBars() {
        val activity = compositionActivity
        onPreCommit(activity) {
            activity.window.addFlags(
                WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
            )

            activity.window.decorView.systemUiVisibility =
                activity.window.decorView.systemUiVisibility.addFlag(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                )

            if (Build.VERSION.SDK_INT >= 29) {
                activity.window.isNavigationBarContrastEnforced = false
                activity.window.isStatusBarContrastEnforced = false
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
                windowInsets.systemBars.start,
                windowInsets.systemBars.top
            )
        }

        val statusBarStyle = remember(statusBarHitPoint, styles.toList()) {
            styles.lastOrNull {
                statusBarHitPoint.x >= it.bounds.left &&
                        statusBarHitPoint.y >= it.bounds.top &&
                        statusBarHitPoint.x <= it.bounds.right &&
                        statusBarHitPoint.y <= it.bounds.bottom
            }
        }

        onPreCommit(activity, statusBarStyle?.barColor) {
            activity.window.statusBarColor =
                (statusBarStyle?.barColor ?: Color.Black
                    .copy(alpha = 0.2f)).toArgb()
        }

        onPreCommit(activity, statusBarStyle?.lightIcons) {
            activity.window.decorView.systemUiVisibility =
                activity.window.decorView.systemUiVisibility.setFlag(
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR,
                    statusBarStyle?.lightIcons ?: false
                )
        }

        if (Build.VERSION.SDK_INT >= 26) {
            val navBarHitPoint = remember(windowInsets, screenWidth, screenHeight) {
                when {
                    windowInsets.systemBars.bottom != 0.dp -> {
                        Position(
                            windowInsets.systemBars.start,
                            screenHeight - windowInsets.systemBars.bottom
                        )
                    }
                    windowInsets.systemBars.start != 0.dp -> {
                        Position(
                            0.dp,
                            screenHeight
                        )
                    }
                    windowInsets.systemBars.end != 0.dp -> {
                        Position(
                            screenWidth - windowInsets.systemBars.end,
                            screenHeight
                        )
                    }
                    else -> Position(Int.MAX_VALUE.dp, Int.MAX_VALUE.dp)
                }
            }

            val navBarStyle = remember(navBarHitPoint, styles.toList()) {
                styles.lastOrNull {
                    navBarHitPoint.x >= it.bounds.left &&
                            navBarHitPoint.y >= it.bounds.top &&
                            navBarHitPoint.x <= it.bounds.right &&
                            navBarHitPoint.y <= it.bounds.bottom
                }
            }

            onPreCommit(activity, navBarStyle?.barColor) {
                activity.window.navigationBarColor =
                    (navBarStyle?.barColor ?: Color.Black
                        .copy(alpha = 0.2f)).toArgb()
            }

            onPreCommit(activity, navBarStyle?.lightIcons) {
                activity.window.decorView.systemUiVisibility =
                    activity.window.decorView.systemUiVisibility.setFlag(
                        View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR,
                        navBarStyle?.lightIcons ?: false
                    )
            }
        } else {
            onPreCommit(activity) {
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
