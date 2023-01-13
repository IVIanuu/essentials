/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.material

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SliderColors
import androidx.compose.material.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.time.toDuration
import com.ivianuu.essentials.time.toLong
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Tag
import kotlin.time.Duration

@Composable fun <T : Comparable<T>> Slider(
  value: T,
  onValueChange: (T) -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  stepPolicy: StepPolicy<T> = NoStepsStepPolicy,
  onValueChangeFinished: (() -> Unit)? = null,
  interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
  colors: SliderColors = SliderDefaults.colors(
    thumbColor = MaterialTheme.colors.secondary,
    activeTrackColor = MaterialTheme.colors.secondary,
  ),
  @Inject converter: SliderValueConverter<T>,
  @Inject valueRange: @DefaultSliderRange ClosedRange<T>,
) = with(converter) {
  androidx.compose.material.Slider(
    value.toFloat(),
    { onValueChange(it.toValue()) },
    modifier,
    enabled,
    remember(valueRange) { valueRange.start.toFloat()..valueRange.endInclusive.toFloat() },
    remember(valueRange) { stepPolicy(valueRange) },
    onValueChangeFinished,
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
