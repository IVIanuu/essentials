/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.material

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SliderColors
import androidx.compose.material.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.ivianuu.essentials.Lerper
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

context(Lerper<T>, DefaultSliderRange<T>) @Composable fun <T : Comparable<T>> Slider(
  value: T,
  modifier: Modifier = Modifier,
  onValueChange: ((T) -> Unit)? = null,
  onValueChangeFinished: ((T) -> Unit)? = null,
  stepPolicy: StepPolicy<T> = NoStepsStepPolicy,
  enabled: Boolean = true,
  interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
  colors: SliderColors = SliderDefaults.colors(
    thumbColor = MaterialTheme.colors.secondary,
    activeTrackColor = MaterialTheme.colors.secondary,
    activeTickColor = Color.Transparent,
    inactiveTickColor = Color.Transparent,
    disabledActiveTickColor = Color.Transparent,
    disabledInactiveTickColor = Color.Transparent
  ),
  valueRange: ClosedRange<T> = defaultSliderRange
) {
  fun T.toFloat() = unlerp(defaultSliderRange.start, defaultSliderRange.endInclusive, this)
  fun Float.toValue() = lerp(defaultSliderRange.start, defaultSliderRange.endInclusive, this)

  var internalValue: Float? by remember { mutableStateOf(null) }
  var internalValueEraseJob: Job? by remember { mutableStateOf(null) }

  val scope = rememberCoroutineScope()
  androidx.compose.material.Slider(
    internalValue ?: value.toFloat(),
    { newInternalValue ->
      internalValue = newInternalValue
      internalValueEraseJob?.cancel()
      onValueChange?.invoke(stepPolicy.stepValue(newInternalValue.toValue(), valueRange))
    },
    modifier,
    enabled,
    0f..1f,
    remember(stepPolicy, defaultSliderRange) { stepPolicy(defaultSliderRange) },
    {
      if (internalValue == null) return@Slider

      onValueChangeFinished?.invoke(stepPolicy.stepValue(internalValue!!.toValue(), valueRange))

      internalValueEraseJob?.cancel()
      internalValueEraseJob = scope.launch {
        delay(1.seconds)
        internalValue = null
      }
    },
    interactionSource,
    colors
  )
}

@JvmInline
value class DefaultSliderRange<T : Comparable<T>>(val defaultSliderRange: ClosedRange<T>) {
  @Provide companion object {
    @Provide val double get() = DefaultSliderRange(0.0..1.0)
    @Provide val float get() = DefaultSliderRange(0f..1f)
    @Provide val int get() = DefaultSliderRange(0..100)
    @Provide val long get() = DefaultSliderRange(0L..100L)
  }
}

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

context(Lerper<T>)
fun <T : Comparable<T>> StepPolicy<T>.stepValue(value: T, valueRange: ClosedRange<T>): T {
  val steps = this@stepValue(valueRange)
  val stepFractions = (if (steps == 0) emptyList()
  else List(steps + 2) { it.toFloat() / (steps + 1) })

  val valueFraction = unlerp(valueRange.start, valueRange.endInclusive, value)

  val steppedFraction = stepFractions
    .minByOrNull { (it - valueFraction).absoluteValue }
    ?: valueFraction

  return lerp(valueRange.start, valueRange.endInclusive, steppedFraction)
}
