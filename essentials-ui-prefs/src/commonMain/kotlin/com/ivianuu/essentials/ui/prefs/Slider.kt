/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.prefs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.time.toDuration
import com.ivianuu.essentials.time.toLong
import com.ivianuu.essentials.ui.common.getValue
import com.ivianuu.essentials.ui.common.refOf
import com.ivianuu.essentials.ui.common.setValue
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.NoStepsStepPolicy
import com.ivianuu.essentials.ui.material.Slider
import com.ivianuu.essentials.ui.material.StepPolicy
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Tag
import com.ivianuu.injekt.inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.time.Duration

context(SliderValueConverter<T>) @Composable fun <T : Comparable<T>> SliderListItem(
  value: T,
  onValueChange: (T) -> Unit,
  title: (@Composable () -> Unit)? = null,
  subtitle: (@Composable () -> Unit)? = null,
  leading: (@Composable () -> Unit)? = null,
  stepPolicy: StepPolicy<T> = NoStepsStepPolicy,
  valueText: @Composable ((T) -> Unit)? = null,
  modifier: Modifier = Modifier,
  @Inject valueRange: @DefaultSliderRange ClosedRange<T>,
) {
  Box(modifier = modifier) {
    ListItem(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(bottom = 20.dp),
      title = title,
      subtitle = subtitle,
      leading = leading
    )

    Row(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(
          // align the slider with the content
          start = if (leading != null) 52.dp else 12.dp,
          end = 16.dp
        ),
      verticalAlignment = Alignment.CenterVertically
    ) {
      var internalValue by remember(value) { mutableStateOf(value.toFloat()) }

      val floatRange = remember(inject<SliderValueConverter<T>>(), valueRange) {
        valueRange.start.toFloat()..valueRange.endInclusive.toFloat()
      }

      var valueChangeJob: Job? by remember { refOf(null) }
      val scope = rememberCoroutineScope()
      Slider(
        value = internalValue,
        onValueChange = { newValue ->
          internalValue = newValue
          valueChangeJob?.cancel()
          valueChangeJob = scope.launch {
            delay(200)
            if (newValue != value)
              onValueChange(newValue.toValue())

            delay(800)
            internalValue = value.toFloat()
          }
        },
        valueRange = floatRange,
        stepPolicy = remember(stepPolicy) {
          { valueRange ->
            stepPolicy(valueRange.start.toValue()..valueRange.endInclusive.toValue())
          }
        },
        modifier = Modifier.weight(1f)
      )

      if (valueText != null) {
        Box(
          modifier = Modifier.widthIn(min = 72.dp),
          contentAlignment = Alignment.CenterEnd
        ) {
          CompositionLocalProvider(
            LocalTextStyle provides MaterialTheme.typography.body2
          ) {
            val steppedValue = remember(valueRange, stepPolicy, internalValue) {
              val steps = stepPolicy(valueRange)
              val stepFractions = (if (steps == 0) emptyList()
              else List(steps + 2) { it.toFloat() / (steps + 1) })
              val stepValues = stepFractions
                .map {
                  valueRange.start.toFloat() +
                      ((valueRange.endInclusive.toFloat() - valueRange.start.toFloat()) * it)
                }

              val steppedValue = stepValues
                .minByOrNull { (it - internalValue).absoluteValue }
                ?: internalValue

              steppedValue.toValue()
            }

            valueText(steppedValue)
          }
        }
      }
    }
  }
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
