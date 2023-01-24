/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.prefs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import com.ivianuu.essentials.compose.getValue
import com.ivianuu.essentials.compose.refOf
import com.ivianuu.essentials.compose.setValue
import com.ivianuu.essentials.ui.material.DefaultSliderRange
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.NoStepsStepPolicy
import com.ivianuu.essentials.ui.material.Slider
import com.ivianuu.essentials.ui.material.SliderValueConverter
import com.ivianuu.essentials.ui.material.StepPolicy
import com.ivianuu.essentials.ui.material.stepValue
import com.ivianuu.injekt.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable fun <T : Comparable<T>> SliderListItem(
  value: T,
  onValueChange: (T) -> Unit,
  stepPolicy: StepPolicy<T> = NoStepsStepPolicy,
  title: (@Composable () -> Unit)? = null,
  subtitle: (@Composable () -> Unit)? = null,
  leading: (@Composable () -> Unit)? = null,
  valueText: @Composable ((T) -> Unit)? = null,
  modifier: Modifier = Modifier,
  contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
  textPadding: PaddingValues = PaddingValues(horizontal = 16.dp),
  singleLine: Boolean = false,
  @Inject converter: SliderValueConverter<T>,
  @Inject valueRange: @DefaultSliderRange ClosedRange<T>,
) = with(converter) {
  var internalValue by remember(value) { mutableStateOf(value.toFloat()) }

  val textPadding = PaddingValues(
    start = max(textPadding.calculateStartPadding(LocalLayoutDirection.current) - 4.dp, 0.dp),
    top = textPadding.calculateTopPadding(),
    bottom = textPadding.calculateBottomPadding(),
    end = textPadding.calculateEndPadding(LocalLayoutDirection.current)
  )

  ListItem(
    modifier = modifier,
    title = title?.let {
      {
        Box(modifier = Modifier.padding(start = 4.dp)) {
          title()
        }
      }
    },
    subtitle = {
      subtitle?.let {
        Box(modifier = Modifier.padding(start = 4.dp)) {
          subtitle()
        }
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
        valueRange = remember(valueRange) {
          valueRange.start.toFloat()..valueRange.endInclusive.toFloat()
        },
        stepPolicy = remember(stepPolicy) {
          { valueRange ->
            stepPolicy(valueRange.start.toValue()..valueRange.endInclusive.toValue())
          }
        }
      )
    },
    leading = leading,
    trailing = valueText?.let {
      {
        Box(
          modifier = Modifier.widthIn(min = 72.dp),
          contentAlignment = Alignment.CenterEnd
        ) {
          CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.body2) {
            valueText(stepPolicy.stepValue(internalValue.toValue(), valueRange))
          }
        }
      }
    },
    contentPadding = contentPadding,
    textPadding = textPadding
  )
}
