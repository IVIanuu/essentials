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

import android.app.Activity
import android.os.Build
import android.view.View
import androidx.compose.Ambient
import androidx.compose.Composable
import androidx.compose.Immutable
import androidx.compose.ambient
import androidx.compose.onPreCommit
import androidx.ui.graphics.Color
import androidx.ui.graphics.toArgb
import com.ivianuu.essentials.util.isLight
import com.ivianuu.essentials.util.setFlag

@Immutable
data class SystemBarStyle(
    val statusBarColor: Color = Color.Black,
    val lightStatusBar: Boolean = statusBarColor.isLight,
    val drawBehindStatusBar: Boolean = false,
    val navigationBarColor: Color = Color.Black,
    val lightNavigationBar: Boolean = navigationBarColor.isLight,
    val drawBehindNavBar: Boolean = false
)

@Composable
fun ProvideCurrentSystemBarStyle(
    style: SystemBarStyle,
    children: @Composable() () -> Unit
) {
    val systemBarManager = ambient(SystemBarManagerAmbient)
    onPreCommit(style) {
        systemBarManager.registerStyle(style)
        onDispose { systemBarManager.unregisterStyle(style) }
    }
    SystemBarStyleAmbient.Provider(value = style, children = children)
}

@Composable
fun ambientSystemBarStyle(): SystemBarStyle =
    ambient(SystemBarStyleAmbient)

private val SystemBarStyleAmbient = Ambient.of { SystemBarStyle() }

internal class SystemBarManager(private val activity: Activity) {

    private val styles = mutableListOf<SystemBarStyle>()
    private var appliedStyle: SystemBarStyle? = null

    fun registerStyle(style: SystemBarStyle) {
        styles += style
        update()
    }

    fun unregisterStyle(style: SystemBarStyle) {
        styles -= style
        update()
    }

    private fun update() {
        val style = styles.lastOrNull() ?: SystemBarStyle()
        if (appliedStyle == style) return
        appliedStyle = style
        val window = activity.window
        val decorView = window.decorView
        window.statusBarColor = style.statusBarColor.toArgb()
        decorView.systemUiVisibility =
            decorView.systemUiVisibility.setFlag(
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR,
                style.drawBehindStatusBar
            )

        decorView.systemUiVisibility =
            decorView.systemUiVisibility.setFlag(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN, style.lightStatusBar
            )

        window.navigationBarColor = style.navigationBarColor.toArgb()
        if (Build.VERSION.SDK_INT >= 26) {
            decorView.systemUiVisibility =
                decorView.systemUiVisibility.setFlag(
                    View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR,
                    style.lightNavigationBar
                )
        }

        decorView.systemUiVisibility =
            decorView.systemUiVisibility.setFlag(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION, style.drawBehindNavBar
            )
    }
}

internal val SystemBarManagerAmbient = Ambient.of<SystemBarManager>()
