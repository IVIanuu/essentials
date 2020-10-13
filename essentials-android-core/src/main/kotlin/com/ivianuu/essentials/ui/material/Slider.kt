package com.ivianuu.essentials.ui.material

import androidx.compose.foundation.InteractionState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SliderConstants
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlin.time.Duration

typealias StepPolicy<T> = (ClosedRange<T>) -> Int

val NoStepsStepPolicy: StepPolicy<*> = { 0 }

fun <T : Comparable<T>> fixedStepPolicy(steps: Int): StepPolicy<T> = { steps }

fun incrementingStepPolicy(incValue: Int): StepPolicy<Int> = { valueRange ->
    ((valueRange.endInclusive - valueRange.start) / incValue) - 1
}

fun incrementingStepPolicy(incValue: Float): StepPolicy<Float> = { valueRange ->
    (((valueRange.endInclusive - valueRange.start) / incValue) - 1).toInt()
}

fun incrementingStepPolicy(incValue: Double): StepPolicy<Double> = { valueRange ->
    (((valueRange.endInclusive - valueRange.start) / incValue) - 1).toInt()
}

fun incrementingStepPolicy(incValue: Long): StepPolicy<Long> = { valueRange ->
    (((valueRange.endInclusive - valueRange.start) / incValue) - 1).toInt()
}

fun incrementingStepPolicy(incValue: Duration): StepPolicy<Duration> = { valueRange ->
    (((valueRange.endInclusive - valueRange.start) / incValue) - 1).toInt()
}

@Composable
fun Slider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    stepPolicy: StepPolicy<Float> = NoStepsStepPolicy,
    onValueChangeEnd: () -> Unit = {},
    interactionState: InteractionState = remember { InteractionState() },
    thumbColor: Color = MaterialTheme.colors.primary,
    activeTrackColor: Color = MaterialTheme.colors.primary,
    inactiveTrackColor: Color = activeTrackColor.copy(alpha = SliderConstants.InactiveTrackColorAlpha),
    activeTickColor: Color = MaterialTheme.colors.onPrimary.copy(alpha = SliderConstants.TickColorAlpha),
    inactiveTickColor: Color = activeTrackColor.copy(alpha = SliderConstants.TickColorAlpha),
) {
    // todo tmp workaround because slider is broken after the first onValueChangeEnd
    key(value) {
        androidx.compose.material.Slider(
            value,
            onValueChange,
            modifier,
            valueRange,
            remember(valueRange) { stepPolicy(valueRange) },
            onValueChangeEnd,
            interactionState,
            thumbColor,
            activeTrackColor,
            inactiveTrackColor,
            activeTickColor,
            inactiveTickColor
        )
    }
}
