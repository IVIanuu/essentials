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
import com.ivianuu.essentials.Lerper
import com.ivianuu.essentials.compose.getValue
import com.ivianuu.essentials.compose.refOf
import com.ivianuu.essentials.compose.setValue
import com.ivianuu.essentials.unlerp
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Tag
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.time.Duration

@Composable fun <T : Comparable<T>> Slider(
  value: T,
  onValueChange: ((T) -> Unit)? = null,
  onValueChangeFinished: ((T) -> Unit)? = null,
  stepPolicy: StepPolicy<T> = NoStepsStepPolicy,
  valueRestoreDuration: Duration = Duration.INFINITE,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
  colors: SliderColors = SliderDefaults.colors(
    thumbColor = MaterialTheme.colors.secondary,
    activeTrackColor = MaterialTheme.colors.secondary,
  ),
  @Inject lerper: Lerper<T>,
  @Inject valueRange: @DefaultSliderRange ClosedRange<T>,
) {
  fun T.toFloat() = lerper.unlerp(valueRange.start, valueRange.endInclusive, this)
  fun Float.toValue() = lerper.lerp(valueRange.start, valueRange.endInclusive, this)

  var internalValue by remember(value) { mutableStateOf(value.toFloat()) }

  var valueChangeJob: Job? by remember { refOf(null) }
  val scope = rememberCoroutineScope()
  androidx.compose.material.Slider(
    internalValue,
    { newInternalValue ->
      internalValue = newInternalValue
      val newValue = internalValue.toValue()
      if (newValue != value)
        onValueChange?.invoke(newValue)

      valueChangeJob?.cancel()
      if (valueRestoreDuration < Duration.INFINITE) {
        valueChangeJob = scope.launch {
          delay(valueRestoreDuration)
          internalValue = value.toFloat()
        }
      }
    },
    modifier,
    enabled,
    remember(valueRange) { valueRange.start.toFloat()..valueRange.endInclusive.toFloat() },
    remember(stepPolicy, valueRange) { stepPolicy(valueRange) },
    onValueChangeFinished?.let {
      {
        onValueChangeFinished(internalValue.toValue())
      }
    },
    interactionSource,
    colors
  )
}

@Tag annotation class DefaultSliderRange {
  companion object {
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
