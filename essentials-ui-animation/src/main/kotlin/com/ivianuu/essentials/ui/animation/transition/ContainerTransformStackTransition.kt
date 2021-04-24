package com.ivianuu.essentials.ui.animation.transition

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.ui.animation.*
import com.ivianuu.essentials.ui.animation.util.*
import com.ivianuu.essentials.ui.core.*
import kotlinx.coroutines.*
import kotlin.time.*

fun ContainerTransformStackTransition(closedKey: Any, openedKey: Any): StackTransition = {
    val fromModifier = fromElementModifier(if (isPush) closedKey else openedKey)!!
    val toModifier = toElementModifier(if (isPush) openedKey else closedKey)!!

    toModifier.value = Modifier.alpha(0f)

    attachTo()

    val (fromBounds, toBounds) = parTupled(
        { fromModifier.awaitLayoutCoordinates().boundsInWindow() },
        { toModifier.awaitLayoutCoordinates().boundsInWindow() }
    )

    val startPosition = fromBounds.topLeft
    val endPosition = toBounds.topLeft

    val startWidth = fromBounds.width
    val startHeight = fromBounds.height
    val endWidth = toBounds.width
    val endHeight = toBounds.height

    var currentScrimAlpha by mutableStateOf(0f)
    var currentPosition by mutableStateOf(startPosition)
    var currentWidth by mutableStateOf(startWidth)
    var currentHeight by mutableStateOf(startHeight)
    var currentColor by mutableStateOf(Color.Transparent)
    var currentBorderWidth by mutableStateOf(0.dp)
    var currentBorderColor by mutableStateOf(Color.Transparent)
    var currentElevation by mutableStateOf(0.dp)
    var currentFromAlpha by mutableStateOf(1f)
    var currentToAlpha by mutableStateOf(0f)

    val fromProps = fromElement(if (isPush) closedKey else openedKey)!![ContainerTransformPropsKey]!!
    val toProps = toElement(if (isPush) openedKey else closedKey)!![ContainerTransformPropsKey]!!

    val laidOut = CompletableDeferred<Unit>()
    overlay {
        Box(
            modifier = Modifier.fillMaxSize()
                .background(Color.Black.copy(alpha = 0.54f * currentScrimAlpha))
        ) {
            Surface(
                modifier = Modifier
                    .onGloballyPositioned {
                        launch { laidOut.complete(Unit) }
                    }
                    .composed {
                        with(LocalDensity.current) {
                            size(currentWidth.toDp(), currentHeight.toDp())
                                .offset(currentPosition.x.toDp(), currentPosition.y.toDp())
                        }
                    },
                color = currentColor,
                border = if (currentBorderWidth > 0.dp) BorderStroke(currentBorderWidth, currentBorderColor) else null,
                elevation = currentElevation
            ) {
                withCompositionContext(fromProps.compositionContext) {
                    FittedBox(fromBounds.width.toInt(), fromBounds.height.toInt(), currentFromAlpha)  {
                        fromProps.content()
                    }
                }
                withCompositionContext(toProps.compositionContext) {
                    FittedBox(toBounds.width.toInt(), toBounds.height.toInt(), currentToAlpha)  {
                        toProps.content()
                    }
                }
            }
        }
    }
    laidOut.await()
    fromModifier.value = Modifier.alpha(0f)

    animate(defaultAnimationSpec(if (isPush) 300.milliseconds else 250.milliseconds)) { value ->
        currentScrimAlpha = if (isPush) {
            interval(0f, 0.3f, value)
        } else {
            lerp(1f, 0f, FastOutSlowInEasing.transform(value))
        }
        currentPosition = lerp(startPosition, endPosition, FastOutSlowInEasing.transform(value))
        currentWidth = lerp(startWidth, endWidth, FastOutSlowInEasing.transform(value))
        currentHeight = lerp(startHeight, endHeight, FastOutSlowInEasing.transform(value))
        currentColor = lerp(fromProps.color, toProps.color, value)
        currentBorderWidth = lerp(fromProps.borderWidth, toProps.borderWidth, value)
        currentBorderColor = lerp(fromProps.borderColor, toProps.borderColor, value)
        currentElevation = lerp(fromProps.elevation, toProps.elevation, value)
        currentToAlpha = interval(0.2f, 0.4f, value)
        if (!isPush) {
            currentFromAlpha = lerp(1f, 0f, interval(0.15f, 0.45f, value))
        }
    }
}

@Composable
private fun FittedBox(
    width: Int,
    height: Int,
    alpha: Float,
    content: @Composable () -> Unit,
) {
    Layout(content, Modifier.alpha(alpha)) { measurables, constraints ->
        val placeables = measurables.map {
            it.measure(Constraints.fixed(width, height))
        }
        layout(constraints.maxWidth, constraints.maxHeight) {
            placeables.forEach { it.place(0, 0) }
        }
    }
}

val ContainerTransformPropsKey = AnimationElementPropKey<ContainerTransformProps>()

class ContainerTransformProps(
    compositionContext: CompositionContext,
    content: @Composable () -> Unit,
    color: Color,
    borderWidth: Dp,
    borderColor: Color,
    elevation: Dp
) {
    var compositionContext by mutableStateOf(compositionContext)
    var content by mutableStateOf(content)
    var color by mutableStateOf(color)
    var borderWidth by mutableStateOf(borderWidth)
    var borderColor by mutableStateOf(borderColor)
    var elevation by mutableStateOf(elevation)
}

@Composable
fun ContainerTransformElement(
    key: Any,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.surface,
    borderWidth: Dp = 0.dp,
    borderColor: Color = Color.Transparent,
    elevation: Dp = 0.dp,
    content: @Composable () -> Unit = {}
) {
    val compositionContext = rememberCompositionContext()
    val props = remember { ContainerTransformProps(compositionContext, content, color, borderWidth, borderColor, elevation) }
    props.compositionContext = compositionContext
    props.content = content
    props.color = color
    props.borderWidth = borderWidth
    props.borderColor = borderColor
    props.elevation = elevation
    Surface(
        Modifier
            .animationElement(key, ContainerTransformPropsKey to props)
            .then(modifier),
        color = color,
        border = if (borderWidth > 0.dp) BorderStroke(borderWidth, borderColor) else null,
        elevation = elevation
    ) {
        content()
    }
}
