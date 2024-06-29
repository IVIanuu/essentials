/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.prefs

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.*
import kotlin.time.Duration.Companion.seconds

@Composable fun <T : Comparable<T>> SliderListItem(
  value: T,
  modifier: Modifier = Modifier,
  onValueChange: ((T) -> Unit)? = null,
  onValueChangeFinished: ((T) -> Unit)? = null,
  stepPolicy: StepPolicy<T> = NoStepsStepPolicy,
  title: (@Composable () -> Unit)? = null,
  subtitle: (@Composable () -> Unit)? = null,
  leading: (@Composable () -> Unit)? = null,
  valueText: @Composable ((T) -> Unit)? = { Text(it.toString()) },
  lerper: Lerper<T> = inject,
  valueRange: @DefaultSliderRange ClosedRange<T> = inject,
) {
  var internalValue: T? by remember { mutableStateOf(null) }
  var internalValueEraseJob: Job? by remember { mutableStateOf(null) }

  val minHeight = if (subtitle != null) {
    if (leading == null) 80.dp else 88.dp
  } else {
    if (leading == null) 64.dp else 72.dp
  }

  ListItem(
    modifier = modifier.heightIn(minHeight),
    title = title,
    subtitle = {
      subtitle?.invoke()

      val scope = rememberCoroutineScope()
      CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
        Slider(
          modifier = Modifier
            .requiredHeight(24.dp)
            .padding(top = 4.dp),
          value = internalValue ?: value,
          onValueChange = { newValue ->
            internalValue = newValue
            internalValueEraseJob?.cancel()
            onValueChange?.invoke(stepPolicy.stepValue(internalValue!!, valueRange))
          },
          onValueChangeFinished = { newValue ->
            onValueChangeFinished?.invoke(stepPolicy.stepValue(newValue, valueRange))
            internalValueEraseJob?.cancel()
            internalValueEraseJob = scope.launch {
              delay(1.seconds)
              internalValue = null
            }
          },
          stepPolicy = stepPolicy,
          valueRange = valueRange
        )
      }
    },
    leading = leading,
    trailing = valueText?.let {
      {
        Box(
          modifier = Modifier.widthIn(min = 56.dp),
          contentAlignment = Alignment.TopEnd
        ) {
          CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.body2) {
            valueText(stepPolicy.stepValue(internalValue ?: value, valueRange))
          }
        }
      }
    }
  )
}

@Composable fun <T> SliderListItem(
  value: T,
  values: List<T>,
  modifier: Modifier = Modifier,
  onValueChange: ((T) -> Unit)? = null,
  onValueChangeFinished: ((T) -> Unit)? = null,
  title: (@Composable () -> Unit)? = null,
  subtitle: (@Composable () -> Unit)? = null,
  leading: (@Composable () -> Unit)? = null,
  valueText: @Composable ((T) -> Unit)? = { Text(it.toString()) }
) {
  SliderListItem(
    value = values.indexOf(value),
    modifier = modifier,
    valueRange = 0..values.lastIndex,
    onValueChange = onValueChange?.let { { onValueChange(values[it]) } },
    onValueChangeFinished = onValueChangeFinished?.let { { onValueChangeFinished(values[it]) } },
    title = title,
    subtitle = subtitle,
    leading = leading,
    valueText = valueText?.let { { valueText(values[it]) } }
  )
}
