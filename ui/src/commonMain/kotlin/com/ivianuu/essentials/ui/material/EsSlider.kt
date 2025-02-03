/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.material

import androidx.compose.foundation.interaction.*
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.*
import com.ivianuu.essentials.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.*
import kotlin.math.*
import kotlin.time.*
import kotlin.time.Duration.Companion.seconds

@Composable fun <T : Comparable<T>> EsSlider(
  value: T,
  modifier: Modifier = Modifier,
  onValueChange: ((T) -> Unit)? = null,
  onValueChangeFinished: ((T) -> Unit)? = null,
  stepPolicy: StepPolicy<T> = NoStepsStepPolicy,
  enabled: Boolean = true,
  interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
  colors: SliderColors = SliderDefaults.colors(),
  lerper: Lerper<T> = inject,
  valueRange: @DefaultSliderRange ClosedRange<T> = inject
) {
  fun T.toFloat() = lerper.unlerp(valueRange.start, valueRange.endInclusive, this)
  fun Float.toValue() = lerper.lerp(valueRange.start, valueRange.endInclusive, this)

  var internalValue: Float? by remember { mutableStateOf(null) }
  var internalValueEraseJob: Job? by remember { mutableStateOf(null) }

  val scope = rememberCoroutineScope()
  androidx.compose.material3.Slider(
    value = internalValue ?: value.toFloat(),
    onValueChange = { newInternalValue ->
      internalValue = newInternalValue
      internalValueEraseJob?.cancel()
      onValueChange?.invoke(stepPolicy.stepValue(newInternalValue.toValue(), valueRange))
    },
    modifier = modifier,
    enabled = enabled,
    valueRange = 0f..1f,
    steps = remember(stepPolicy, valueRange) { stepPolicy(valueRange) },
    onValueChangeFinished = {
      if (internalValue == null) return@Slider

      onValueChangeFinished?.invoke(stepPolicy.stepValue(internalValue!!.toValue(), valueRange))

      internalValueEraseJob?.cancel()
      internalValueEraseJob = scope.launch {
        delay(1.seconds)
        internalValue = null
      }
    },
    interactionSource = interactionSource,
    colors = colors
  )
}

@Tag @Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS, AnnotationTarget.CONSTRUCTOR)
annotation class DefaultSliderRange {
  @Provide companion object {
    @Provide val double: @DefaultSliderRange ClosedRange<Double> = 0.0..1.0
    @Provide val float: @DefaultSliderRange ClosedRange<Float> = 0f..1f
    @Provide val int: @DefaultSliderRange ClosedRange<Int> = 0..100
    @Provide val long: @DefaultSliderRange ClosedRange<Long> = 0L..100L
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

fun <T : Comparable<T>> StepPolicy<T>.stepValue(
  value: T,
  valueRange: @DefaultSliderRange ClosedRange<T> = inject,
  lerper: Lerper<T> = inject
): T {
  val steps = this@stepValue(valueRange)
  val stepFractions = (if (steps == 0) emptyList()
  else List(steps + 2) { it.toFloat() / (steps + 1) })

  val valueFraction = lerper.unlerp(valueRange.start, valueRange.endInclusive, value)

  val steppedFraction = stepFractions
    .minByOrNull { (it - valueFraction).absoluteValue }
    ?: valueFraction

  return lerper.lerp(valueRange.start, valueRange.endInclusive, steppedFraction)
}
