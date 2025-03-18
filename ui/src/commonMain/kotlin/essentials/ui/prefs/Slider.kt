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
  first: Boolean = false,
  last: Boolean = false,
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

  DecoratedListItem(
    first = first,
    last = last,
    modifier = modifier,
    headlineContent = headlineContent,
    supportingContent = {
      @Provide val scope = rememberCoroutineScope()
      CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
        EsSlider(
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
            internalValueEraseJob = launch {
              delay(1.seconds)
              internalValue = null
            }
          },
          stepPolicy = stepPolicy,
          valueRange = valueRange
        )
      }
    },
    leadingContent = leadingContent,
    trailingContent = trailingContent?.let {
      {
        Box(
          modifier = Modifier.widthIn(min = 56.dp),
          contentAlignment = Alignment.TopEnd
        ) {
          ProvideContentColorTextStyle(
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            textStyle = MaterialTheme.typography.bodyMedium
          ) {
            trailingContent(stepPolicy.stepValue(internalValue ?: value, valueRange))
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
