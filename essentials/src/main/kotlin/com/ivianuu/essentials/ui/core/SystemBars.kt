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

import android.app.Activity
import android.os.Build
import android.view.View
import androidx.compose.Ambient
import androidx.compose.Composable
import androidx.compose.Immutable
import androidx.compose.ambient
import androidx.compose.frames.modelListOf
import androidx.compose.onPreCommit
import androidx.compose.remember
import androidx.ui.core.Modifier
import androidx.ui.core.dp
import androidx.ui.graphics.Color
import androidx.ui.graphics.toArgb
import androidx.ui.layout.LayoutExpandedHeight
import androidx.ui.layout.LayoutExpandedWidth
import androidx.ui.layout.LayoutGravity
import androidx.ui.layout.LayoutHeight
import androidx.ui.layout.LayoutWidth
import androidx.ui.layout.Stack
import com.ivianuu.essentials.ui.common.SafeArea
import com.ivianuu.essentials.ui.common.framed
import com.ivianuu.essentials.ui.layout.WithModifier
import com.ivianuu.essentials.ui.material.Surface
import com.ivianuu.essentials.util.addFlag
import com.ivianuu.essentials.util.isLight
import com.ivianuu.essentials.util.setFlag

@Immutable
data class SystemBarStyle(
    val statusBarColor: Color = Color.Black,
    val lightStatusBar: Boolean = statusBarColor.isLight,
    val navigationBarColor: Color = Color.Black,
    val lightNavigationBar: Boolean = navigationBarColor.isLight
)

@Composable
fun ProvideCurrentSystemBarStyle(
    value: SystemBarStyle,
    children: @Composable() () -> Unit
) {
    val systemBarManager = ambient(SystemBarManagerAmbient)
    onPreCommit(value) {
        systemBarManager.registerStyle(value)
        onDispose { systemBarManager.unregisterStyle(value) }
    }
    SystemBarStyleAmbient.Provider(
        value = value,
        children = children
    )
}

@Composable
fun StatusBar(color: Color) {
    SafeArea(
        top = false,
        bottom = false
    ) {
        Surface(
            modifier = LayoutExpandedWidth + LayoutHeight(ambientWindowInsets().viewPadding.top),
            color = color
        ) { }
    }
}

@Composable
fun NavigationBar(color: Color) {
    val windowInsets = ambientWindowInsets()
    val viewPadding = windowInsets.viewPadding
    val modifier = when {
        viewPadding.bottom > 0.dp -> LayoutExpandedWidth + LayoutHeight(viewPadding.bottom)
        viewPadding.left > 0.dp -> LayoutWidth(viewPadding.left) + LayoutExpandedHeight
        viewPadding.right > 0.dp -> LayoutWidth(viewPadding.right) + LayoutExpandedHeight
        else -> Modifier.None
    }
    Surface(
        modifier = modifier,
        color = color
    ) { }
}

@Composable
fun ambientSystemBarStyle(): SystemBarStyle =
    ambient(SystemBarStyleAmbient)

@Composable
fun SystemBarManager(children: @Composable() () -> Unit) {
    val activity = ambient(ActivityAmbient)
    val systemBarManager = remember { SystemBarManager(activity) }
    SystemBarManagerAmbient.Provider(value = systemBarManager) {
        Stack {
            WithModifier(
                modifier = LayoutGravity.TopLeft,
                children = children
            )

            WithModifier(modifier = LayoutGravity.TopLeft) {
                StatusBar(color = systemBarManager.currentStyle.statusBarColor)
            }

            val viewPadding = ambientWindowInsets().viewPadding
            val navBarGravity = when {
                viewPadding.bottom > 0.dp -> LayoutGravity.BottomLeft
                viewPadding.left > 0.dp -> LayoutGravity.TopLeft
                viewPadding.right > 0.dp -> LayoutGravity.TopRight
                else -> Modifier.None
            }

            WithModifier(modifier = navBarGravity) {
                NavigationBar(color = systemBarManager.currentStyle.navigationBarColor)
            }
        }
    }
}

private val SystemBarStyleAmbient = Ambient.of { SystemBarStyle() }

internal class SystemBarManager(private val activity: Activity) {

    private val styles = modelListOf<SystemBarStyle>()
    var currentStyle: SystemBarStyle by framed(SystemBarStyle())

    init {
        activity.window.statusBarColor = Color.Black.copy(alpha = 0.2f).toArgb()
        activity.window.navigationBarColor = Color.Transparent.toArgb()
        activity.window.decorView.systemUiVisibility =
            activity.window.decorView.systemUiVisibility.addFlag(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            )
    }

    fun registerStyle(style: SystemBarStyle) {
        styles += style
        update()
    }

    fun unregisterStyle(style: SystemBarStyle) {
        styles -= style
        update()
    }

    private fun update() {
        currentStyle = styles.lastOrNull() ?: SystemBarStyle()
        activity.window.decorView.systemUiVisibility =
            activity.window.decorView.systemUiVisibility.setFlag(
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR,
                currentStyle.lightStatusBar
            )
        if (Build.VERSION.SDK_INT >= 26) {
            activity.window.decorView.systemUiVisibility =
                activity.window.decorView.systemUiVisibility.setFlag(
                    View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR,
                    currentStyle.lightNavigationBar
                )
        }
    }
}

internal val SystemBarManagerAmbient = Ambient.of<SystemBarManager>()
