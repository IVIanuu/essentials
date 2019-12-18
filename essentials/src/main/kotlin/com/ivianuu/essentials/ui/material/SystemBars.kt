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

import android.os.Build
import android.view.View
import androidx.compose.Ambient
import androidx.compose.Composable
import androidx.compose.Immutable
import androidx.compose.ambient
import androidx.ui.graphics.Color
import androidx.ui.graphics.toArgb
import com.ivianuu.essentials.ui.core.ActivityAmbient
import com.ivianuu.essentials.util.isLight
import com.ivianuu.essentials.util.setFlag

@Immutable
data class SystemBarConfig(
    val statusBarColor: Color = Color.Black,
    val lightStatusBar: Boolean = statusBarColor.isLight,
    val drawBehindStatusBar: Boolean = false,
    val navigationBarColor: Color = Color.Black,
    val lightNavigationBar: Boolean = navigationBarColor.isLight,
    val drawBehindNavBar: Boolean = false
)

@Composable
fun SystemBarTheme(
    config: SystemBarConfig = SystemBarConfig(),
    children: @Composable() () -> Unit
) {
    applySystemBarConfig(config)
    SystemBarConfigAmbient.Provider(value = config, children = children)
}

@Composable
private fun applySystemBarConfig(config: SystemBarConfig) {
    val activity = ambient(ActivityAmbient)
    val window = activity.window
    val decorView = window.decorView
    window.statusBarColor = config.statusBarColor.toArgb()
    decorView.systemUiVisibility =
        decorView.systemUiVisibility.setFlag(
            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR,
            config.drawBehindStatusBar
        )

    decorView.systemUiVisibility =
        decorView.systemUiVisibility.setFlag(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN, config.lightStatusBar
        )

    window.navigationBarColor = config.navigationBarColor.toArgb()
    if (Build.VERSION.SDK_INT >= 26) {
        decorView.systemUiVisibility =
            decorView.systemUiVisibility.setFlag(
                View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR,
                config.lightNavigationBar
            )
    }

    decorView.systemUiVisibility =
        decorView.systemUiVisibility.setFlag(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION, config.drawBehindNavBar
        )
}

private val SystemBarConfigAmbient = Ambient.of<SystemBarConfig>()