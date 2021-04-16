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

import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.ui.animatedstack.animation.*
import com.ivianuu.essentials.ui.common.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*

class PopupKey(
    val position: Rect,
    val onCancel: (() -> Unit)?,
    val content: @Composable() () -> Unit,
) : Key<Nothing>

@Given
fun popupUi(
    @Given key: PopupKey,
    @Given navigator: Navigator,
): KeyUi<PopupKey> = {
    val configuration = LocalConfiguration.current
    val initialConfiguration = remember { configuration }
    if (configuration !== initialConfiguration) {
        navigator.pop(key)
    }

    var dismissed by remember { refOf(false) }

    val dismiss: (Boolean) -> Unit = { cancelled ->
        if (!dismissed) {
            dismissed = true
            navigator.pop(key)
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
        ) {
            key.content()
        }
    }
}

@Given
val popupKeyOptionsFactory: KeyUiOptionsFactory<PopupKey> = {
    KeyUiOptions(
        opaque = true,
        enterTransition = FadeStackTransition(),
        exitTransition = FadeStackTransition()
    )
}

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
            x = (position.right - placeable.width).toInt()
        }

        x = x.coerceIn(8.dp.roundToPx(), constraints.maxWidth - placeable.width - 8.dp.roundToPx())
        y = y.coerceIn(8.dp.roundToPx(), constraints.maxHeight - placeable.height - 8.dp.roundToPx())

        layout(constraints.maxWidth, constraints.maxHeight) {
            placeable.place(x = x, y = y)
        }
    }
}
