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
import androidx.ui.layout.Row
import androidx.ui.layout.Stack
import androidx.ui.layout.padding
import androidx.ui.layout.preferredWidthIn
import androidx.ui.material.MaterialTheme
import androidx.ui.unit.Dp
import androidx.ui.unit.dp
import com.ivianuu.essentials.store.Box
import com.ivianuu.essentials.ui.box.asState
import com.ivianuu.essentials.ui.core.DefaultTextComposableStyle
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.core.currentOrElse
import com.ivianuu.essentials.ui.material.DefaultListItemStyle
import com.ivianuu.essentials.ui.material.ListItemStyleAmbient
import com.ivianuu.essentials.ui.material.Slider
import com.ivianuu.essentials.util.UnitValueTextProvider
import com.ivianuu.essentials.util.cast
import kotlin.time.Duration

@Composable
fun DoubleSliderPreference(
    box: Box<Double>,
    title: @Composable (() -> Unit)? = null,
    summary: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    valueText: @Composable ((Double) -> Unit)? = null,
    valueRange: ClosedFloatingPointRange<Double> = 0.0..1.0,
    steps: Int = 0,
    modifier: Modifier = Modifier
) {
    val state = box.asState()
    DoubleSliderPreference(
        value = state.value,
        onValueChange = { state.value = it },
        modifier = modifier,
        title = title,
        summary = summary,
        leading = leading,
        valueRange = valueRange,
        steps = steps,
        valueText = valueText
    )
}

@Composable
fun DoubleSliderPreference(
    value: Double,
    onValueChange: (Double) -> Unit,
    title: @Composable (() -> Unit)? = null,
    summary: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    valueText: @Composable ((Double) -> Unit)? = null,
    valueRange: ClosedFloatingPointRange<Double> = 0.0..1.0,
    steps: Int = 0,
    modifier: Modifier = Modifier
) {
    BaseSliderPreference(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        toFloat = { it.toFloat() },
        fromFloat = { it.toDouble() },
        title = title,
        summary = summary,
        leading = leading,
        valueRange = valueRange,
        steps = steps,
        valueText = valueText
    )
}

@Composable
fun FloatSliderPreference(
    box: Box<Float>,
    title: @Composable (() -> Unit)? = null,
    summary: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    valueText: @Composable ((Float) -> Unit)? = null,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    modifier: Modifier = Modifier
) {
    val state = box.asState()
    FloatSliderPreference(
        value = state.value,
        onValueChange = { state.value = it },
        modifier = modifier,
        title = title,
        summary = summary,
        leading = leading,
        valueText = valueText,
        valueRange = valueRange,
        steps = steps
    )
}

@Composable
fun FloatSliderPreference(
    value: Float,
    onValueChange: (Float) -> Unit,
    title: @Composable (() -> Unit)? = null,
    summary: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    valueText: @Composable ((Float) -> Unit)? = null,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    modifier: Modifier = Modifier
) {
    BaseSliderPreference(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        toFloat = { it },
        fromFloat = { it },
        title = title,
        summary = summary,
        leading = leading,
        valueRange = valueRange,
        steps = steps,
        valueText = valueText
    )
}

@Composable
fun IntSliderPreference(
    box: Box<Int>,
    title: @Composable (() -> Unit)? = null,
    summary: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    valueText: @Composable ((Int) -> Unit)? = null,
    valueRange: IntRange = 0..100,
    steps: Int = 0,
    modifier: Modifier = Modifier
) {
    val state = box.asState()
    IntSliderPreference(
        value = state.value,
        onValueChange = { state.value = it },
        modifier = modifier,
        title = title,
        summary = summary,
        leading = leading,
        valueText = valueText,
        valueRange = valueRange,
        steps = steps
    )
}

@Composable
fun IntSliderPreference(
    value: Int,
    onValueChange: (Int) -> Unit,
    title: @Composable (() -> Unit)? = null,
    summary: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    valueText: @Composable ((Int) -> Unit)? = null,
    valueRange: IntRange = 0..100,
    steps: Int = 0,
    modifier: Modifier = Modifier
) {
    BaseSliderPreference(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        toFloat = { it.toFloat() },
        fromFloat = { it.toInt() },
        title = title,
        summary = summary,
        leading = leading,
        valueText = valueText,
        valueRange = valueRange,
        steps = steps
    )
}

@Composable
fun LongSliderPreference(
    box: Box<Long>,
    title: @Composable (() -> Unit)? = null,
    summary: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    valueText: @Composable ((Long) -> Unit)? = null,
    valueRange: LongRange = 0L..100L,
    steps: Int = 0,
    modifier: Modifier = Modifier
) {
    val state = box.asState()
    LongSliderPreference(
        value = state.value,
        onValueChange = { state.value = it },
        modifier = modifier,
        title = title,
        summary = summary,
        leading = leading,
        valueText = valueText,
        valueRange = valueRange,
        steps = steps
    )
}

@Composable
fun LongSliderPreference(
    value: Long,
    onValueChange: (Long) -> Unit,
    title: @Composable (() -> Unit)? = null,
    summary: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    valueText: @Composable ((Long) -> Unit)? = null,
    valueRange: LongRange = 0L..100L,
    steps: Int = 0,
    modifier: Modifier = Modifier
) {
    BaseSliderPreference(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        toFloat = { it.toFloat() },
        fromFloat = { it.toLong() },
        title = title,
        summary = summary,
        leading = leading,
        valueText = valueText,
        valueRange = valueRange,
        steps = steps
    )
}

@Composable
fun DpSliderPreference(
    box: Box<Dp>,
    title: @Composable (() -> Unit)? = null,
    summary: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    valueText: @Composable ((Dp) -> Unit)? = null,
    valueRange: ClosedRange<Dp> = 0.dp..1.dp,
    steps: Int = 0,
    modifier: Modifier = Modifier
) {
    val state = box.asState()
    DpSliderPreference(
        value = state.value,
        onValueChange = { state.value = it },
        modifier = modifier,
        title = title,
        summary = summary,
        leading = leading,
        valueText = valueText,
        valueRange = valueRange,
        steps = steps
    )
}

@Composable
fun DpSliderPreference(
    value: Dp,
    onValueChange: (Dp) -> Unit,
    title: @Composable (() -> Unit)? = null,
    summary: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    valueText: @Composable ((Dp) -> Unit)? = null,
    valueRange: ClosedRange<Dp> = 0.dp..1.dp,
    steps: Int = 0,
    modifier: Modifier = Modifier
) {
    BaseSliderPreference(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        toFloat = { it.value },
        fromFloat = { it.dp },
        title = title,
        summary = summary,
        leading = leading,
        valueText = valueText,
        valueRange = valueRange,
        steps = steps
    )
}

@Composable
fun DurationSliderPreference(
    box: Box<Duration>,
    title: @Composable (() -> Unit)? = null,
    summary: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    valueText: @Composable ((Duration) -> Unit)? = null,
    valueRange: ClosedRange<Duration>,
    steps: Int = 0,
    modifier: Modifier = Modifier
) {
    val state = box.asState()
    DurationSliderPreference(
        value = state.value,
        onValueChange = { state.value = it },
        modifier = modifier,
        title = title,
        summary = summary,
        leading = leading,
        valueRange = valueRange,
        steps = steps,
        valueText = valueText
    )
}

@Composable
fun DurationSliderPreference(
    value: Duration,
    onValueChange: (Duration) -> Unit,
    title: @Composable (() -> Unit)? = null,
    summary: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    valueText: @Composable ((Duration) -> Unit)? = null,
    valueRange: ClosedRange<Duration>,
    steps: Int = 0,
    modifier: Modifier = Modifier
) {
    BaseSliderPreference(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        toFloat = { it.toFloat() },
        fromFloat = { it.toDuration() },
        title = title,
        summary = summary,
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
fun <T : Comparable<T>> BaseSliderPreference(
    value: T,
    onValueChange: (T) -> Unit,
    toFloat: (T) -> Float,
    fromFloat: (Float) -> T,
    title: @Composable (() -> Unit)? = null,
    summary: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    valueRange: ClosedRange<T>,
    steps: Int = 0,
    valueText: @Composable ((T) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Stack(modifier = modifier) {
        BasePreference(
            modifier = Modifier.gravity(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            title = title,
            summary = summary,
            leading = leading,
            onClick = {}
        )

        val listItemStyle = ListItemStyleAmbient.currentOrElse { DefaultListItemStyle() }

        Row(
            modifier = Modifier.gravity(Alignment.BottomCenter)
                .padding(
                    start = listItemStyle.contentPadding.start - 4.dp, // make the slider pretty
                    end = listItemStyle.contentPadding.end
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
                    modifier = Modifier.preferredWidthIn(minWidth = 72.dp),
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
        textStyle = MaterialTheme.typography.body2,
        style = DefaultTextComposableStyle(maxLines = 1)
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
