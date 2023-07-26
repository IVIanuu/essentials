/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.prefs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.LocalMinimumInteractiveComponentEnforcement
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
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.Lerper
import com.ivianuu.essentials.ui.material.DefaultSliderRange
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.NoStepsStepPolicy
import com.ivianuu.essentials.ui.material.Slider
import com.ivianuu.essentials.ui.material.StepPolicy
import com.ivianuu.essentials.ui.material.stepValue
import com.ivianuu.injekt.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
  valueText: @Composable ((T) -> Unit)? = null,
  @Inject lerper: Lerper<T>,
  @Inject valueRange: @DefaultSliderRange ClosedRange<T>,
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
