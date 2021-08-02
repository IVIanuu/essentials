/*
 * Copyright 2021 Manuel Wrage
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

package com.ivianuu.essentials.ui.prefs

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.time.*
import com.ivianuu.essentials.ui.common.*
import com.ivianuu.essentials.ui.material.*
import kotlinx.coroutines.*
import kotlin.time.*

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
  androidx.compose.foundation.layout.Box(modifier = modifier) {
    ListItem(
      modifier = Modifier.align(Alignment.BottomCenter)
        .padding(bottom = 32.dp),
      title = title,
      subtitle = subtitle,
      leading = leading,
      onClick = {}
    )

    androidx.compose.foundation.layout.Row(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(
          start = 12.dp, // make the slider pretty
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
            valueText(fromFloat(internalValue))
          }
        }
      }
    }
  }
}
