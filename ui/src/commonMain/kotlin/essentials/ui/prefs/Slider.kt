/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.ui.prefs

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import essentials.*
import essentials.coroutines.*
import essentials.ui.common.*
import essentials.ui.material.*
import injekt.*
import kotlinx.coroutines.*
import kotlin.time.Duration.Companion.seconds

@Composable fun <T : Comparable<T>> SliderListItem(
  value: T,
  headlineContent: @Composable () -> Unit,
  modifier: Modifier = Modifier,
  sectionType: SectionType = SectionType.MIDDLE,
  onValueChange: ((T) -> Unit)? = null,
  onValueChangeFinished: ((T) -> Unit)? = null,
  stepPolicy: StepPolicy<T> = NoStepsStepPolicy,
  leadingContent: (@Composable () -> Unit)? = null,
  trailingContent: @Composable ((T) -> Unit)? = { Text(it.toString()) },
  lerper: Lerper<T> = inject,
  valueRange: @DefaultSliderRange ClosedRange<T> = inject,
) {
  var internalValue: T? by remember { mutableStateOf(null) }
  var internalValueEraseJob: Job? by remember { mutableStateOf(null) }

  SectionListItem(
    modifier = modifier,
    sectionType = sectionType,
    headlineContent = headlineContent,
    supportingContent = {
      @Provide val scope = rememberCoroutineScope()
      Row(verticalAlignment = Alignment.CenterVertically) {
        CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
          EsSlider(
            modifier = Modifier
              .padding(end = 16.dp)
              .weight(1f),
            value = internalValue ?: value,
            onValueChange = { newValue ->
              internalValue = newValue
              internalValueEraseJob?.cancel()
              onValueChange?.invoke(stepPolicy.stepValue(internalValue!!, valueRange))
            },
            onValueChangeFinished = { newValue ->
              onValueChangeFinished?.invoke(stepPolicy.stepValue(newValue, valueRange))
              internalValueEraseJob?.cancel()
              internalValueEraseJob = launch {
                delay(1.seconds)
                internalValue = null
              }
            },
            stepPolicy = stepPolicy,
            valueRange = valueRange,
            colors = SliderDefaults.colors(
              thumbColor = MaterialTheme.colorScheme.tertiary,
              activeTrackColor = MaterialTheme.colorScheme.tertiary,
              inactiveTrackColor = MaterialTheme.colorScheme.tertiary.copy(0.2f),
            )
          )
        }

        if (trailingContent != null)
          Box(
            modifier = Modifier.widthIn(min = 96.dp),
            contentAlignment = Alignment.TopEnd
          ) {
            ProvideContentColorTextStyle(
              contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
              textStyle = MaterialTheme.typography.headlineLarge
            ) {
              trailingContent(stepPolicy.stepValue(internalValue ?: value, valueRange))
            }
          }
      }
    },
    leadingContent = leadingContent
  )
}

@Composable fun <T> SliderListItem(
  value: T,
  values: List<T>,
  modifier: Modifier = Modifier,
  onValueChange: ((T) -> Unit)? = null,
  onValueChangeFinished: ((T) -> Unit)? = null,
  headlineContent: @Composable () -> Unit,
  leadingContent: (@Composable () -> Unit)? = null,
  trailingContent: @Composable ((T) -> Unit)? = { Text(it.toString()) }
) {
  SliderListItem(
    value = values.indexOf(value),
    modifier = modifier,
    valueRange = 0..values.lastIndex,
    onValueChange = onValueChange?.let { { onValueChange(values[it]) } },
    onValueChangeFinished = onValueChangeFinished?.let { { onValueChangeFinished(values[it]) } },
    headlineContent = headlineContent,
    leadingContent = leadingContent,
    trailingContent = trailingContent?.let { { trailingContent(values[it]) } }
  )
}
