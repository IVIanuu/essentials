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

package com.ivianuu.essentials.ui.popup

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.store.Collector
import com.ivianuu.essentials.ui.animatable.animatable
import com.ivianuu.essentials.ui.animatedstack.animation.FadeStackTransition
import com.ivianuu.essentials.ui.common.getValue
import com.ivianuu.essentials.ui.common.refOf
import com.ivianuu.essentials.ui.common.setValue
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyModule
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.navigation.KeyUiOptions
import com.ivianuu.essentials.ui.navigation.KeyUiOptionsFactory
import com.ivianuu.essentials.ui.navigation.NavigationAction
import com.ivianuu.essentials.ui.navigation.NavigationAction.Pop
import com.ivianuu.injekt.Given
data class PopupKey(
    val position: Rect,
    val onCancel: (() -> Unit)?,
    val content: @Composable() () -> Unit,
) : Key<Nothing>

@Given
val popupKeyModule = KeyModule<PopupKey>()

@Given
fun popupUi(
    @Given key: PopupKey,
    @Given navigator: Collector<NavigationAction>,
): KeyUi<PopupKey> = {
    val configuration = LocalConfiguration.current
    val initialConfiguration = remember { configuration }
    if (configuration !== initialConfiguration) {
        navigator(Pop(key))
    }

    var dismissed by remember { refOf(false) }

    val dismiss: (Boolean) -> Unit = { cancelled ->
        if (!dismissed) {
            dismissed = true
            navigator(Pop(key))
            if (cancelled) key.onCancel?.invoke()
        }
    }

    PopupLayout(
        position = key.position,
        modifier = Modifier.pointerInput(true) {
            detectTapGestures { dismiss(true) }
        }
    ) {
        Box(
            modifier = Modifier
                .pointerInput(true) {
                    detectTapGestures {
                    }
                }
                .animatable(PopupTag)
        ) {
            key.content()
        }
    }
}

@Given
fun popupKeyOptionsFactory(): KeyUiOptionsFactory<PopupKey> = {
    KeyUiOptions(
        opaque = true,
        enterTransition = FadeStackTransition(),
        exitTransition = FadeStackTransition()
    )
}

private val PopupTag = Any()

@Composable
private fun PopupLayout(
    position: Rect,
    modifier: Modifier,
    content: @Composable () -> Unit,
) {
    Layout(content = content, modifier = modifier) { measureables, constraints ->
        val childConstraints = constraints.copy(
            minWidth = 0,
            minHeight = 0
        )

        val placeable = measureables.single().measure(childConstraints)

        var y = position.top.toInt()
        var x: Int

        // Find the ideal horizontal position.
        if ((position.left + position.right / 2) < constraints.maxWidth / 2) {
            x = position.left.toInt()
        } else if (position.left < position.right) {
            x = (position.right - placeable.width).toInt()
        } else {
            x = (position.right - placeable.width).toInt() // todo based on ltr/rtl
        }

        x = x.coerceIn(8.dp.roundToPx(), constraints.maxWidth - placeable.width - 8.dp.roundToPx())
        y = y.coerceIn(8.dp.roundToPx(), constraints.maxHeight - placeable.height - 8.dp.roundToPx())

        layout(constraints.maxWidth, constraints.maxHeight) {
            placeable.place(x = x, y = y)
        }
    }
}
