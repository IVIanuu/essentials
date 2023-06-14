/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.material

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.compose.getValue
import com.ivianuu.essentials.compose.refOf
import com.ivianuu.essentials.compose.setValue
import com.ivianuu.essentials.time.toDuration
import com.ivianuu.essentials.time.toLong
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
    thumbColor = MaterialTheme.colorScheme.secondary,
    activeTrackColor = MaterialTheme.colorScheme.secondary,
  ),
  @Inject converter: SliderValueConverter<T>,
  @Inject valueRange: @DefaultSliderRange ClosedRange<T>,
) = with(converter) {
  var internalValue by remember(value) { mutableStateOf(value.toFloat()) }

  var valueChangeJob: Job? by remember { refOf(null) }
  val scope = rememberCoroutineScope()
  androidx.compose.material3.Slider(
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
        onValueChangeFinished(stepPolicy.stepValue(internalValue.toValue(), valueRange))
      }
    },
    colors,
    interactionSource
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

interface SliderValueConverter<T : Comparable<T>> {
  fun T.toFloat(): Float
  fun Float.toValue(): T

  companion object {
    @Provide val duration = object : SliderValueConverter<Duration> {
      override fun Duration.toFloat() = toLong().toFloat()
      override fun Float.toValue() = toLong().toDuration()
    }

    @Provide val double = object : SliderValueConverter<Double> {
      override fun Double.toFloat() = toFloat()
      override fun Float.toValue() = toDouble()
    }

    @Provide val dp = object : SliderValueConverter<Dp> {
      override fun Dp.toFloat() = value
      override fun Float.toValue() = dp
    }

    @Provide val float = object : SliderValueConverter<Float> {
      override fun Float.toFloat() = this
      override fun Float.toValue() = this
    }

    @Provide val int = object : SliderValueConverter<Int> {
      override fun Int.toFloat() = toFloat()
      override fun Float.toValue() = toInt()
    }

    @Provide val long = object : SliderValueConverter<Long> {
      override fun Long.toFloat() = toFloat()
      override fun Float.toValue() = toLong()
    }
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
  @Inject valueRange: @DefaultSliderRange ClosedRange<T>,
  @Inject converter: SliderValueConverter<T>
): T = with(converter) {
  val steps = this@stepValue(valueRange)
  val stepFractions = (if (steps == 0) emptyList()
  else List(steps + 2) { it.toFloat() / (steps + 1) })
  val stepValues = stepFractions
    .map {
      valueRange.start.toFloat() +
          ((valueRange.endInclusive.toFloat() - valueRange.start.toFloat()) * it)
    }

  val steppedValue = stepValues
    .minByOrNull { (it - value.toFloat()).absoluteValue }
    ?: value.toFloat()

  return steppedValue.toValue()
}
