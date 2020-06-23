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
import androidx.compose.Composable
import androidx.compose.Immutable
import androidx.compose.Providers
import androidx.compose.Stable
import androidx.compose.StructurallyEqual
import androidx.compose.frames.modelListOf
import androidx.compose.getValue
import androidx.compose.onCommit
import androidx.compose.remember
import androidx.compose.setValue
import androidx.compose.state
import androidx.compose.staticAmbientOf
import androidx.ui.core.ConfigurationAmbient
import androidx.ui.core.DensityAmbient
import androidx.ui.core.Modifier
import androidx.ui.core.composed
import androidx.ui.core.globalBounds
import androidx.ui.core.onPositioned
import androidx.ui.graphics.Color
import androidx.ui.graphics.toArgb
import androidx.ui.unit.Bounds
import androidx.ui.unit.Position
import androidx.ui.unit.PxBounds
import androidx.ui.unit.dp
import com.ivianuu.essentials.ui.common.compositionActivity
import com.ivianuu.essentials.util.addFlag
import com.ivianuu.essentials.util.setFlag

fun Modifier.systemBarOverlayStyle(light: Boolean): Modifier = composed {
    val systemBarManager = SystemBarManagerAmbient.current
    var globalBounds by state<PxBounds?>(StructurallyEqual) { null }
    val density = DensityAmbient.current

    onCommit(systemBarManager, globalBounds, density, light) {
        val dpBounds = with(density) {
            Bounds(
                left = globalBounds?.left?.toInt()?.toDp() ?: 0.dp,
                top = globalBounds?.top?.toInt()?.toDp() ?: 0.dp,
                right = globalBounds?.right?.toInt()?.toDp() ?: 0.dp,
                bottom = globalBounds?.bottom?.toInt()?.toDp() ?: 0.dp
            )
        }

        val style = SystemBarStyle(dpBounds, light)
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
    val bounds: Bounds,
    val light: Boolean
)

@Stable
private class SystemBarManager {

    val styles = modelListOf<SystemBarStyle>()

    @Composable
    fun updateSystemBars() {
        val activity = compositionActivity
        onCommit(activity) {
            activity.window.statusBarColor = Color.Black.copy(alpha = 0.2f).toArgb()
            activity.window.navigationBarColor = Color.Black.copy(alpha = 0.2f).toArgb()

            activity.window.addFlags(
                WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
            )

            activity.window.decorView.systemUiVisibility =
                activity.window.decorView.systemUiVisibility.addFlag(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                )

            activity.window.isNavigationBarContrastEnforced = false
            activity.window.isStatusBarContrastEnforced = false
        }

        val windowInsets = InsetsAmbient.current

        val screenWidth = ConfigurationAmbient.current.screenWidthDp.dp
        val screenHeight = ConfigurationAmbient.current.screenHeightDp.dp

        val statusBarHitPoint = remember(windowInsets, screenWidth) {
            Position(
                screenWidth / 2,
                windowInsets.systemBars.top / 2
            )
        }

        val lightStatusBar = remember(statusBarHitPoint, styles.toList()) {
            styles.lastOrNull {
                statusBarHitPoint.x >= it.bounds.left &&
                        statusBarHitPoint.y >= it.bounds.top &&
                        statusBarHitPoint.x <= it.bounds.right &&
                        statusBarHitPoint.y <= it.bounds.bottom
            }?.light ?: false
        }

        onCommit(activity, lightStatusBar) {
            activity.window.decorView.systemUiVisibility =
                activity.window.decorView.systemUiVisibility.setFlag(
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR,
                    lightStatusBar
                )
        }

        if (Build.VERSION.SDK_INT >= 26) {
            val navBarHitPoint = remember(windowInsets, screenWidth) {
                Position(
                    screenWidth / 2,
                    ((screenHeight - windowInsets.systemBars.bottom) + screenHeight) / 2
                )
            }

            val lightNavBar = remember(navBarHitPoint, styles.toList()) {
                styles.lastOrNull {
                    navBarHitPoint.x >= it.bounds.left &&
                            navBarHitPoint.y >= it.bounds.top &&
                            navBarHitPoint.x <= it.bounds.right &&
                            navBarHitPoint.y <= it.bounds.bottom
                }?.light ?: false
            }

            onCommit(activity, lightNavBar) {
                activity.window.decorView.systemUiVisibility =
                    activity.window.decorView.systemUiVisibility.setFlag(
                        View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR,
                        lightNavBar
                    )
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
