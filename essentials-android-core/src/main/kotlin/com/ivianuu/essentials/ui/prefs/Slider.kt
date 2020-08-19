/*
 * Copyright 2019 Manuel Wrage
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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.stateFor
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Box
import androidx.compose.foundation.ContentGravity
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Stack
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.datastore.DataStore
import com.ivianuu.essentials.ui.datastore.asState
import com.ivianuu.essentials.ui.layout.align
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.util.UnitValueTextProvider
import kotlin.time.Duration

@Composable
fun DoubleSliderListItem(
    dataStore: DataStore<Double>,
    title: @Composable (() -> Unit)? = null,
    subtitle: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    valueText: @Composable ((Double) -> Unit)? = null,
    valueRange: ClosedFloatingPointRange<Double> = 0.0..1.0,
    steps: Int = 0,
    modifier: Modifier = Modifier
) {
    val state = dataStore.asState()
    DoubleSliderListItem(
        value = state.value,
        onValueChange = { state.value = it },
        modifier = modifier,
        title = title,
        subtitle = subtitle,
        leading = leading,
        valueRange = valueRange,
        steps = steps,
        valueText = valueText
    )
}

@Composable
fun DoubleSliderListItem(
    value: Double,
    onValueChange: (Double) -> Unit,
    title: @Composable (() -> Unit)? = null,
    subtitle: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    valueText: @Composable ((Double) -> Unit)? = null,
    valueRange: ClosedFloatingPointRange<Double> = 0.0..1.0,
    steps: Int = 0,
    modifier: Modifier = Modifier
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
        steps = steps,
        valueText = valueText
    )
}

@Composable
fun FloatSliderListItem(
    dataStore: DataStore<Float>,
    title: @Composable (() -> Unit)? = null,
    subtitle: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    valueText: @Composable ((Float) -> Unit)? = null,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    modifier: Modifier = Modifier
) {
    val state = dataStore.asState()
    FloatSliderListItem(
        value = state.value,
        onValueChange = { state.value = it },
        modifier = modifier,
        title = title,
        subtitle = subtitle,
        leading = leading,
        valueText = valueText,
        valueRange = valueRange,
        steps = steps
    )
}

@Composable
fun FloatSliderListItem(
    value: Float,
    onValueChange: (Float) -> Unit,
    title: @Composable (() -> Unit)? = null,
    subtitle: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    valueText: @Composable ((Float) -> Unit)? = null,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    modifier: Modifier = Modifier
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
        steps = steps,
        valueText = valueText
    )
}

@Composable
fun IntSliderListItem(
    dataStore: DataStore<Int>,
    title: @Composable (() -> Unit)? = null,
    subtitle: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    valueText: @Composable ((Int) -> Unit)? = null,
    valueRange: IntRange = 0..100,
    steps: Int = 0,
    modifier: Modifier = Modifier
) {
    val state = dataStore.asState()
    IntSliderListItem(
        value = state.value,
        onValueChange = { state.value = it },
        modifier = modifier,
        title = title,
        subtitle = subtitle,
        leading = leading,
        valueText = valueText,
        valueRange = valueRange,
        steps = steps
    )
}

@Composable
fun IntSliderListItem(
    value: Int,
    onValueChange: (Int) -> Unit,
    title: @Composable (() -> Unit)? = null,
    subtitle: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    valueText: @Composable ((Int) -> Unit)? = null,
    valueRange: IntRange = 0..100,
    steps: Int = 0,
    modifier: Modifier = Modifier
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
        steps = steps
    )
}

@Composable
fun LongSliderListItem(
    dataStore: DataStore<Long>,
    title: @Composable (() -> Unit)? = null,
    subtitle: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    valueText: @Composable ((Long) -> Unit)? = null,
    valueRange: LongRange = 0L..100L,
    steps: Int = 0,
    modifier: Modifier = Modifier
) {
    val state = dataStore.asState()
    LongSliderListItem(
        value = state.value,
        onValueChange = { state.value = it },
        modifier = modifier,
        title = title,
        subtitle = subtitle,
        leading = leading,
        valueText = valueText,
        valueRange = valueRange,
        steps = steps
    )
}

@Composable
fun LongSliderListItem(
    value: Long,
    onValueChange: (Long) -> Unit,
    title: @Composable (() -> Unit)? = null,
    subtitle: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    valueText: @Composable ((Long) -> Unit)? = null,
    valueRange: LongRange = 0L..100L,
    steps: Int = 0,
    modifier: Modifier = Modifier
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
        steps = steps
    )
}

@Composable
fun DpSliderListItem(
    dataStore: DataStore<Dp>,
    title: @Composable (() -> Unit)? = null,
    subtitle: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    valueText: @Composable ((Dp) -> Unit)? = null,
    valueRange: ClosedRange<Dp> = 0.dp..1.dp,
    steps: Int = 0,
    modifier: Modifier = Modifier
) {
    val state = dataStore.asState()
    DpSliderListItem(
        value = state.value,
        onValueChange = { state.value = it },
        modifier = modifier,
        title = title,
        subtitle = subtitle,
        leading = leading,
        valueText = valueText,
        valueRange = valueRange,
        steps = steps
    )
}

@Composable
fun DpSliderListItem(
    value: Dp,
    onValueChange: (Dp) -> Unit,
    title: @Composable (() -> Unit)? = null,
    subtitle: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    valueText: @Composable ((Dp) -> Unit)? = null,
    valueRange: ClosedRange<Dp> = 0.dp..1.dp,
    steps: Int = 0,
    modifier: Modifier = Modifier
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
        steps = steps
    )
}

@Composable
fun DurationSliderListItem(
    dataStore: DataStore<Duration>,
    title: @Composable (() -> Unit)? = null,
    subtitle: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    valueText: @Composable ((Duration) -> Unit)? = null,
    valueRange: ClosedRange<Duration>,
    steps: Int = 0,
    modifier: Modifier = Modifier
) {
    val state = dataStore.asState()
    DurationSliderListItem(
        value = state.value,
        onValueChange = { state.value = it },
        modifier = modifier,
        title = title,
        subtitle = subtitle,
        leading = leading,
        valueRange = valueRange,
        steps = steps,
        valueText = valueText
    )
}

@Composable
fun DurationSliderListItem(
    value: Duration,
    onValueChange: (Duration) -> Unit,
    title: @Composable (() -> Unit)? = null,
    subtitle: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    valueText: @Composable ((Duration) -> Unit)? = null,
    valueRange: ClosedRange<Duration>,
    steps: Int = 0,
    modifier: Modifier = Modifier
) {
    BaseSliderListItem(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        toFloat = { it.toFloat() },
        fromFloat = { it.toDuration() },
        title = title,
        subtitle = subtitle,
        leading = leading,
        valueRange = valueRange,
        steps = steps,
        valueText = valueText
    )
}

private fun Float.toDuration(): Duration {
    return Duration::class.java.getDeclaredConstructor(Double::class.java)
        .also { it.isAccessible = true }
        .newInstance(this.toDouble())
}

private fun Duration.toFloat(): Float {
    return javaClass.declaredFields
        .first { it.type == Double::class.java }
        .also { it.isAccessible = true }
        .get(this)!!
        .let { it as Double }
        .toFloat()
}

@Composable
fun <T : Comparable<T>> BaseSliderListItem(
    value: T,
    onValueChange: (T) -> Unit,
    toFloat: (T) -> Float,
    fromFloat: (Float) -> T,
    title: @Composable (() -> Unit)? = null,
    subtitle: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    valueRange: ClosedRange<T>,
    steps: Int = 0,
    valueText: @Composable ((T) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Stack(modifier = modifier) {
        ListItem(
            modifier = Modifier.gravity(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            title = title,
            subtitle = subtitle,
            leading = leading,
            onClick = {}
        )

        Row(
            modifier = Modifier
                .gravity(Alignment.BottomCenter)
                .padding(
                    start = 12.dp, // make the slider pretty
                    end = 16.dp
                ),
            verticalGravity = Alignment.CenterVertically
        ) {
            val sliderState = stateFor(value) { toFloat(value) }

            val floatRange = remember(toFloat, valueRange) {
                toFloat(valueRange.start)..toFloat(valueRange.endInclusive)
            }

            Slider(
                value = sliderState.value,
                onValueChange = { sliderState.value = it },
                onValueChangeEnd = {
                    onValueChange(fromFloat(sliderState.value))
                },
                valueRange = floatRange,
                steps = steps,
                modifier = Modifier.weight(1f)
            )

            if (valueText != null) {
                Box(
                    modifier = Modifier.widthIn(minWidth = 72.dp),
                    gravity = ContentGravity.CenterEnd
                ) {
                    valueText(fromFloat(sliderState.value))
                }
            }
        }
    }
}

@Composable
fun <T> SliderValueText(value: T) {
    Text(
        text = value.toString(),
        style = MaterialTheme.typography.body2
    )
}

@Composable
fun <T> SimpleValueTextProvider(toString: (T) -> String = { it.toString() }): @Composable (T) -> Unit {
    return { SliderValueText(toString(it)) }
}

@Composable
fun <T> UnitValueTextProvider(
    unit: UnitValueTextProvider.Unit,
    toString: (T) -> String = { it.toString() }
): @Composable (T) -> Unit {
    val textProvider = UnitValueTextProvider(ContextAmbient.current, unit)
    return { SliderValueText(textProvider(toString(it))) }
}
