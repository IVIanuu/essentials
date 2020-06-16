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

import androidx.compose.Composable
import androidx.compose.remember
import androidx.compose.stateFor
import androidx.ui.core.Alignment
import androidx.ui.core.ContextAmbient
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.foundation.ContentGravity
import androidx.ui.foundation.Text
import androidx.ui.layout.Row
import androidx.ui.layout.Stack
import androidx.ui.layout.padding
import androidx.ui.layout.widthIn
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Slider
import androidx.ui.unit.Dp
import androidx.ui.unit.dp
import com.ivianuu.essentials.store.Box
import com.ivianuu.essentials.ui.box.asState
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.util.UnitValueTextProvider
import com.ivianuu.essentials.util.cast
import kotlin.time.Duration

@Composable
fun DoubleSliderListItem(
    box: Box<Double>,
    title: @Composable (() -> Unit)? = null,
    subtitle: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    valueText: @Composable ((Double) -> Unit)? = null,
    valueRange: ClosedFloatingPointRange<Double> = 0.0..1.0,
    steps: Int = 0,
    modifier: Modifier = Modifier
) {
    val state = box.asState()
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
    box: Box<Float>,
    title: @Composable (() -> Unit)? = null,
    subtitle: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    valueText: @Composable ((Float) -> Unit)? = null,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    modifier: Modifier = Modifier
) {
    val state = box.asState()
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
    box: Box<Int>,
    title: @Composable (() -> Unit)? = null,
    subtitle: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    valueText: @Composable ((Int) -> Unit)? = null,
    valueRange: IntRange = 0..100,
    steps: Int = 0,
    modifier: Modifier = Modifier
) {
    val state = box.asState()
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
    box: Box<Long>,
    title: @Composable (() -> Unit)? = null,
    subtitle: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    valueText: @Composable ((Long) -> Unit)? = null,
    valueRange: LongRange = 0L..100L,
    steps: Int = 0,
    modifier: Modifier = Modifier
) {
    val state = box.asState()
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
    box: Box<Dp>,
    title: @Composable (() -> Unit)? = null,
    subtitle: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    valueText: @Composable ((Dp) -> Unit)? = null,
    valueRange: ClosedRange<Dp> = 0.dp..1.dp,
    steps: Int = 0,
    modifier: Modifier = Modifier
) {
    val state = box.asState()
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
    box: Box<Duration>,
    title: @Composable (() -> Unit)? = null,
    subtitle: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    valueText: @Composable ((Duration) -> Unit)? = null,
    valueRange: ClosedRange<Duration>,
    steps: Int = 0,
    modifier: Modifier = Modifier
) {
    val state = box.asState()
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
        .cast<Double>()
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
            modifier = Modifier.gravity(Alignment.BottomCenter)
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
                steps = steps
            )

            if (valueText != null) {
                Box(
                    modifier = Modifier.widthIn(minWidth = 72.dp),
                    gravity = ContentGravity.Center
                ) {
                    valueText(fromFloat(sliderState.value))
                }
            }
        }
    }
}

@Composable
fun <T> SimpleSliderValueText(value: T) {
    Text(
        text = value.toString(),
        style = MaterialTheme.typography.body2
    )
}

@Composable
fun <T> SimpleValueTextProvider(toString: (T) -> String = { it.toString() }): @Composable (T) -> Unit {
    return { SimpleSliderValueText(toString(it)) }
}

@Composable
fun <T> UnitValueTextProvider(
    unit: UnitValueTextProvider.Unit,
    toString: (T) -> String = { it.toString() }
): @Composable (T) -> Unit {
    val textProvider = UnitValueTextProvider(
        ContextAmbient.current, unit
    )
    return { SimpleSliderValueText(textProvider(toString(it))) }
}
