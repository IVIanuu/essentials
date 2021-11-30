/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.time.Duration

@Composable fun DoubleSliderListItem(
  value: Double,
  onValueChange: (Double) -> Unit,
  title: @Composable (() -> Unit)? = null,
  subtitle: @Composable (() -> Unit)? = null,
  leading: @Composable (() -> Unit)? = null,
  valueText: @Composable ((Double) -> Unit)? = null,
  valueRange: ClosedRange<Double> = 0.0..1.0,
  stepPolicy: StepPolicy<Double> = NoStepsStepPolicy,
  modifier: Modifier = Modifier,
) {
  BaseSliderListItem(
    value = value,
    onValueChange = onValueChange,
    modifier = modifier,
    toFloat = { it.toFloat() },
    fromFloat = { it.toDouble() },
    title = title,
    subtitle = subtitle,
    leading = leading,
    valueRange = valueRange,
    stepPolicy = stepPolicy,
    valueText = valueText
  )
}

@Composable fun FloatSliderListItem(
  value: Float,
  onValueChange: (Float) -> Unit,
  title: @Composable (() -> Unit)? = null,
  subtitle: @Composable (() -> Unit)? = null,
  leading: @Composable (() -> Unit)? = null,
  valueText: @Composable ((Float) -> Unit)? = null,
  valueRange: ClosedRange<Float> = 0f..1f,
  stepPolicy: StepPolicy<Float> = NoStepsStepPolicy,
  modifier: Modifier = Modifier,
) {
  BaseSliderListItem(
    value = value,
    onValueChange = onValueChange,
    modifier = modifier,
    toFloat = { it },
    fromFloat = { it },
    title = title,
    subtitle = subtitle,
    leading = leading,
    valueRange = valueRange,
    stepPolicy = stepPolicy,
    valueText = valueText
  )
}

@Composable fun IntSliderListItem(
  value: Int,
  onValueChange: (Int) -> Unit,
  title: @Composable (() -> Unit)? = null,
  subtitle: @Composable (() -> Unit)? = null,
  leading: @Composable (() -> Unit)? = null,
  valueText: @Composable ((Int) -> Unit)? = null,
  valueRange: IntRange = 0..100,
  stepPolicy: StepPolicy<Int> = NoStepsStepPolicy,
  modifier: Modifier = Modifier,
) {
  BaseSliderListItem(
    value = value,
    onValueChange = onValueChange,
    modifier = modifier,
    toFloat = { it.toFloat() },
    fromFloat = { it.toInt() },
    title = title,
    subtitle = subtitle,
    leading = leading,
    valueText = valueText,
    valueRange = valueRange,
    stepPolicy = stepPolicy
  )
}

@Composable fun LongSliderListItem(
  value: Long,
  onValueChange: (Long) -> Unit,
  title: @Composable (() -> Unit)? = null,
  subtitle: @Composable (() -> Unit)? = null,
  leading: @Composable (() -> Unit)? = null,
  valueText: @Composable ((Long) -> Unit)? = null,
  valueRange: LongRange = 0L..100L,
  stepPolicy: StepPolicy<Long> = NoStepsStepPolicy,
  modifier: Modifier = Modifier,
) {
  BaseSliderListItem(
    value = value,
    onValueChange = onValueChange,
    modifier = modifier,
    toFloat = { it.toFloat() },
    fromFloat = { it.toLong() },
    title = title,
    subtitle = subtitle,
    leading = leading,
    valueText = valueText,
    valueRange = valueRange,
    stepPolicy = stepPolicy
  )
}

@Composable fun DpSliderListItem(
  value: Dp,
  onValueChange: (Dp) -> Unit,
  title: @Composable (() -> Unit)? = null,
  subtitle: @Composable (() -> Unit)? = null,
  leading: @Composable (() -> Unit)? = null,
  valueText: @Composable ((Dp) -> Unit)? = null,
  valueRange: ClosedRange<Dp> = 0.dp..1.dp,
  stepPolicy: StepPolicy<Dp> = NoStepsStepPolicy,
  modifier: Modifier = Modifier,
) {
  BaseSliderListItem(
    value = value,
    onValueChange = onValueChange,
    modifier = modifier,
    toFloat = { it.value },
    fromFloat = { it.dp },
    title = title,
    subtitle = subtitle,
    leading = leading,
    valueText = valueText,
    valueRange = valueRange,
    stepPolicy = stepPolicy
  )
}

@Composable fun DurationSliderListItem(
  value: Duration,
  onValueChange: (Duration) -> Unit,
  title: @Composable (() -> Unit)? = null,
  subtitle: @Composable (() -> Unit)? = null,
  leading: @Composable (() -> Unit)? = null,
  valueText: @Composable ((Duration) -> Unit)? = null,
  valueRange: ClosedRange<Duration>,
  stepPolicy: StepPolicy<Duration> = NoStepsStepPolicy,
  modifier: Modifier = Modifier,
) {
  BaseSliderListItem(
    value = value,
    onValueChange = onValueChange,
    modifier = modifier,
    toFloat = { it.toLong().toFloat() },
    fromFloat = { it.toLong().toDuration() },
    title = title,
    subtitle = subtitle,
    leading = leading,
    valueRange = valueRange,
    stepPolicy = stepPolicy,
    valueText = valueText
  )
}

@Composable fun <T : Comparable<T>> BaseSliderListItem(
  value: T,
  onValueChange: (T) -> Unit,
  toFloat: (T) -> Float,
  fromFloat: (Float) -> T,
  title: @Composable (() -> Unit)? = null,
  subtitle: @Composable (() -> Unit)? = null,
  leading: @Composable (() -> Unit)? = null,
  valueRange: ClosedRange<T>,
  stepPolicy: StepPolicy<T> = NoStepsStepPolicy,
  valueText: @Composable ((T) -> Unit)? = null,
  modifier: Modifier = Modifier,
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
      var internalValue by remember(value) { mutableStateOf(toFloat(value)) }

      val floatRange = remember(toFloat, valueRange) {
        toFloat(valueRange.start)..toFloat(valueRange.endInclusive)
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
              onValueChange(fromFloat(newValue))

            delay(800)
            internalValue = toFloat(value)
          }
        },
        valueRange = floatRange,
        stepPolicy = remember(stepPolicy) {
          { valueRange ->
            stepPolicy(fromFloat(valueRange.start)..fromFloat(valueRange.endInclusive))
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
                  toFloat(valueRange.start) +
                      ((toFloat(valueRange.endInclusive) - toFloat(valueRange.start)) * it)
                }

              val steppedValue = stepValues
                .minByOrNull { (it - internalValue).absoluteValue }
                ?: internalValue

              fromFloat(steppedValue)
            }

            valueText(steppedValue)
          }
        }
      }
    }
  }
}
