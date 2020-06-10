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

package com.ivianuu.essentials.ui.popup

import androidx.animation.FloatPropKey
import androidx.animation.LinearOutSlowInEasing
import androidx.animation.transitionDefinition
import androidx.compose.Composable
import androidx.compose.onActive
import androidx.compose.remember
import androidx.ui.core.ConfigurationAmbient
import androidx.ui.core.Layout
import androidx.ui.core.Modifier
import androidx.ui.core.TransformOrigin
import androidx.ui.core.gesture.tapGestureFilter
import androidx.ui.foundation.Box
import androidx.ui.unit.IntPx
import androidx.ui.unit.IntPxBounds
import androidx.ui.unit.center
import androidx.ui.unit.dp
import androidx.ui.unit.ipx
import com.ivianuu.essentials.ui.animatable.AnimatableElementsAmbient
import com.ivianuu.essentials.ui.animatable.animatableElement
import com.ivianuu.essentials.ui.animatedstack.StackTransition
import com.ivianuu.essentials.ui.common.transition
import com.ivianuu.essentials.ui.common.untrackedState
import com.ivianuu.essentials.ui.navigation.NavigatorAmbient
import com.ivianuu.essentials.ui.navigation.Route

fun PopupRoute(
    position: IntPxBounds,
    onCancel: (() -> Unit)? = null,
    popup: @Composable () -> Unit
) = Route(
    opaque = true,
    enterTransition = PopupStackAnimation(position),
    exitTransition = PopupStackAnimation(position)
) {
    val navigator = NavigatorAmbient.current

    val configuration = ConfigurationAmbient.current
    val initialConfiguration = remember { configuration }
    if (configuration !== initialConfiguration) {
        navigator.popTop()
    }

    val dismissed = untrackedState { false }

    val dismiss: (Boolean) -> Unit = { cancelled ->
        if (!dismissed.value) {
            dismissed.value = true
            navigator.popTop()
            if (cancelled) onCancel?.invoke()
        }
    }

    PopupLayout(
        position = position,
        modifier = Modifier.tapGestureFilter(
            onTap = { dismiss(true) }
        )
    ) {
        Box(
            modifier = Modifier.tapGestureFilter(onTap = {})
                .animatableElement(PopupTag),
            children = popup
        )
    }
}

private val PopupTag = Any()

@Composable
private fun PopupLayout(
    position: IntPxBounds,
    modifier: Modifier,
    children: @Composable () -> Unit
) {
    Layout(children = children, modifier = modifier) { measureables, constraints, _ ->
        val childConstraints = constraints.copy(
            minWidth = 0.ipx,
            minHeight = 0.ipx
        )

        val placeable = measureables.single().measure(childConstraints)

        var y = position.top
        var x: IntPx

        // Find the ideal horizontal position.
        if ((position.left + position.right / 2) < constraints.maxWidth / 2) {
            x = position.left
        } else if (position.left < position.right) {
            x = position.right - placeable.width
        } else {
            x = position.right - placeable.width // todo based on ltr/rtl
        }

        x = x.coerceIn(8.dp.toIntPx(), constraints.maxWidth - placeable.width - 8.dp.toIntPx())
        y = y.coerceIn(8.dp.toIntPx(), constraints.maxHeight - placeable.height - 8.dp.toIntPx())

        layout(constraints.maxWidth, constraints.maxHeight) {
            placeable.place(x = x, y = y)
        }
    }
}

private fun PopupStackAnimation(
    position: IntPxBounds
): StackTransition = { context ->
    val popupElement = AnimatableElementsAmbient.current.getElement(PopupTag).value

    if (context.isPush) onActive { context.addTo() }

    val transitionState = transition(
        definition = PopupTransitionDefinition,
        initState = !context.isPush,
        toState = context.isPush,
        onStateChangeFinished = {
            if (!context.isPush) context.removeFrom()
            context.onComplete()
        }
    )

    val transformOrigin = if (context.containerBounds != null) {
        TransformOrigin(
            pivotFractionX = if (position.left.value.toFloat() < context.containerBounds.center().x / 2) 0f else 1f,
            pivotFractionY = if (position.top.value.toFloat() < context.containerBounds.center().y / 2) 0f else 1f
        )
    } else TransformOrigin.Center

    popupElement.drawLayerModifier.alpha = transitionState[Alpha]
    popupElement.drawLayerModifier.scaleX = transitionState[ScaleX]
    popupElement.drawLayerModifier.scaleY = transitionState[ScaleY]
    popupElement.drawLayerModifier.transformOrigin = transformOrigin
}

private val ScaleY = FloatPropKey()
private val ScaleX = FloatPropKey()
private val Alpha = FloatPropKey()

private val PopupTransitionDefinition = transitionDefinition {
    state(false) {
        this[ScaleX] = 0f
        this[ScaleY] = 0f
        this[Alpha] = 0f
    }
    state(true) {
        this[ScaleX] = 1f
        this[ScaleY] = 1f
        this[Alpha] = 1f
    }
    transition(false, true) {
        ScaleX using tween {
            duration = 120
            easing = LinearOutSlowInEasing
        }
        ScaleY using tween {
            duration = 180
            easing = LinearOutSlowInEasing
            delay = 60
        }
        Alpha using tween {
            duration = 30
        }
    }
    transition(true, false) {
        ScaleX using tween {
            duration = 1
            delay = 120 - 1
        }
        ScaleY using tween {
            duration = 1
            delay = 120 - 1
        }
        Alpha using tween {
            duration = 120
        }
    }
}
