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
import androidx.compose.key
import androidx.compose.remember
import androidx.compose.state
import androidx.ui.core.Alignment
import androidx.ui.core.ContextAmbient
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.foundation.ContentGravity
import androidx.ui.layout.Stack
import androidx.ui.layout.padding
import androidx.ui.layout.preferredWidthIn
import androidx.ui.material.MaterialTheme
import androidx.ui.unit.Dp
import androidx.ui.unit.IntPx
import androidx.ui.unit.dp
import androidx.ui.unit.ipx
import com.ivianuu.essentials.store.Box
import com.ivianuu.essentials.ui.core.DefaultTextComposableStyle
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.core.currentOrElse
import com.ivianuu.essentials.ui.layout.CrossAxisAlignment
import com.ivianuu.essentials.ui.layout.Row
import com.ivianuu.essentials.ui.material.DefaultListItemStyle
import com.ivianuu.essentials.ui.material.ListItemStyleAmbient
import com.ivianuu.essentials.ui.material.Slider
import com.ivianuu.essentials.util.UnitValueTextProvider
import com.ivianuu.essentials.util.cast
import kotlin.time.Duration

@Composable
inline fun DoubleSliderPreference(
    box: Box<Double>,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null,
    noinline title: @Composable (() -> Unit)? = null,
    noinline summary: @Composable (() -> Unit)? = null,
    noinline leading: @Composable (() -> Unit)? = null,
    noinline valueText: @Composable ((Double) -> Unit)? = null,
    valueRange: ClosedFloatingPointRange<Double> = 0.0..1.0,
    steps: Int = 0
) {
    key(box) {
        DoubleSliderPreference(
            valueController = ValueController(box),
            enabled = enabled,
            dependencies = dependencies,
            title = title,
            summary = summary,
            leading = leading,
            valueRange = valueRange,
            steps = steps,
            valueText = valueText
        )
    }
}

@Composable
fun DoubleSliderPreference(
    valueController: ValueController<Double>,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null,
    title: @Composable (() -> Unit)? = null,
    summary: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    valueText: @Composable ((Double) -> Unit)? = null,
    valueRange: ClosedFloatingPointRange<Double> = 0.0..1.0,
    steps: Int = 0
) {
    BaseSliderPreference(
        valueController = valueController,
        toFloat = { it.toFloat() },
        fromFloat = { it.toDouble() },
        enabled = enabled,
        dependencies = dependencies,
        title = title,
        summary = summary,
        leading = leading,
        valueRange = valueRange,
        steps = steps,
        valueText = valueText
    )
}

@Composable
inline fun FloatSliderPreference(
    box: Box<Float>,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null,
    noinline title: @Composable (() -> Unit)? = null,
    noinline summary: @Composable (() -> Unit)? = null,
    noinline leading: @Composable (() -> Unit)? = null,
    noinline valueText: @Composable ((Float) -> Unit)? = null,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0
) {
    key(box) {
        FloatSliderPreference(
            valueController = ValueController(box),
            enabled = enabled,
            dependencies = dependencies,
            title = title,
            summary = summary,
            leading = leading,
            valueText = valueText,
            valueRange = valueRange,
            steps = steps
        )
    }
}

@Composable
fun FloatSliderPreference(
    valueController: ValueController<Float>,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null,
    title: @Composable (() -> Unit)? = null,
    summary: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    valueText: @Composable ((Float) -> Unit)? = null,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0
) {
    BaseSliderPreference(
        valueController = valueController,
        toFloat = { it },
        fromFloat = { it },
        enabled = enabled,
        dependencies = dependencies,
        title = title,
        summary = summary,
        leading = leading,
        valueRange = valueRange,
        steps = steps,
        valueText = valueText
    )
}

@Composable
inline fun IntSliderPreference(
    box: Box<Int>,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null,
    noinline title: @Composable (() -> Unit)? = null,
    noinline summary: @Composable (() -> Unit)? = null,
    noinline leading: @Composable (() -> Unit)? = null,
    noinline valueText: @Composable ((Int) -> Unit)? = null,
    valueRange: IntRange = 0..100,
    steps: Int = 0
) {
    key(box) {
        IntSliderPreference(
            valueController = ValueController(box),
            enabled = enabled,
            dependencies = dependencies,
            title = title,
            summary = summary,
            leading = leading,
            valueText = valueText,
            valueRange = valueRange,
            steps = steps
        )
    }
}

@Composable
fun IntSliderPreference(
    valueController: ValueController<Int>,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null,
    title: @Composable (() -> Unit)? = null,
    summary: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    valueText: @Composable ((Int) -> Unit)? = null,
    valueRange: IntRange = 0..100,
    steps: Int = 0
) {
    BaseSliderPreference(
        valueController = valueController,
        toFloat = { it.toFloat() },
        fromFloat = { it.toInt() },
        enabled = enabled,
        dependencies = dependencies,
        title = title,
        summary = summary,
        leading = leading,
        valueText = valueText,
        valueRange = valueRange,
        steps = steps
    )
}

@Composable
inline fun LongSliderPreference(
    box: Box<Long>,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null,
    noinline title: @Composable (() -> Unit)? = null,
    noinline summary: @Composable (() -> Unit)? = null,
    noinline leading: @Composable (() -> Unit)? = null,
    noinline valueText: @Composable ((Long) -> Unit)? = null,
    valueRange: LongRange = 0L..100L,
    steps: Int = 0
) {
    key(box) {
        LongSliderPreference(
            valueController = ValueController(box),
            enabled = enabled,
            dependencies = dependencies,
            title = title,
            summary = summary,
            leading = leading,
            valueText = valueText,
            valueRange = valueRange,
            steps = steps
        )
    }
}

@Composable
fun LongSliderPreference(
    valueController: ValueController<Long>,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null,
    title: @Composable (() -> Unit)? = null,
    summary: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    valueText: @Composable ((Long) -> Unit)? = null,
    valueRange: LongRange = 0L..100L,
    steps: Int = 0
) {
    BaseSliderPreference(
        valueController = valueController,
        toFloat = { it.toFloat() },
        fromFloat = { it.toLong() },
        enabled = enabled,
        dependencies = dependencies,
        title = title,
        summary = summary,
        leading = leading,
        valueText = valueText,
        valueRange = valueRange,
        steps = steps
    )
}

@Composable
inline fun DpSliderPreference(
    box: Box<Dp>,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null,
    noinline title: @Composable (() -> Unit)? = null,
    noinline summary: @Composable (() -> Unit)? = null,
    noinline leading: @Composable (() -> Unit)? = null,
    noinline valueText: @Composable ((Dp) -> Unit)? = null,
    valueRange: ClosedRange<Dp> = 0.dp..1.dp,
    steps: Int = 0
) {
    key(box) {
        DpSliderPreference(
            valueController = ValueController(box),
            enabled = enabled,
            dependencies = dependencies,
            title = title,
            summary = summary,
            leading = leading,
            valueText = valueText,
            valueRange = valueRange,
            steps = steps
        )
    }
}

@Composable
fun DpSliderPreference(
    valueController: ValueController<Dp>,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null,
    title: @Composable (() -> Unit)? = null,
    summary: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    valueText: @Composable ((Dp) -> Unit)? = null,
    valueRange: ClosedRange<Dp> = 0.dp..1.dp,
    steps: Int = 0
) {
    BaseSliderPreference(
        valueController = valueController,
        toFloat = { it.value },
        fromFloat = { it.dp },
        enabled = enabled,
        dependencies = dependencies,
        title = title,
        summary = summary,
        leading = leading,
        valueText = valueText,
        valueRange = valueRange,
        steps = steps
    )
}

@Composable
inline fun IntPxSliderPreference(
    box: Box<IntPx>,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null,
    noinline title: @Composable (() -> Unit)? = null,
    noinline summary: @Composable (() -> Unit)? = null,
    noinline leading: @Composable (() -> Unit)? = null,
    noinline valueText: @Composable ((IntPx) -> Unit)? = null,
    valueRange: ClosedRange<IntPx> = 0.ipx..100.ipx,
    steps: Int = 0
) {
    key(box) {
        IntPxSliderPreference(
            valueController = ValueController(box),
            enabled = enabled,
            dependencies = dependencies,
            title = title,
            summary = summary,
            leading = leading,
            valueText = valueText,
            valueRange = valueRange,
            steps = steps
        )
    }
}

@Composable
fun IntPxSliderPreference(
    valueController: ValueController<IntPx>,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null,
    title: @Composable (() -> Unit)? = null,
    summary: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    valueText: @Composable ((IntPx) -> Unit)? = null,
    valueRange: ClosedRange<IntPx> = 0.ipx..100.ipx,
    steps: Int = 0
) {
    BaseSliderPreference(
        valueController = valueController,
        toFloat = { it.value.toFloat() },
        fromFloat = { it.toInt().ipx },
        enabled = enabled,
        dependencies = dependencies,
        title = title,
        summary = summary,
        leading = leading,
        valueText = valueText,
        valueRange = valueRange,
        steps = steps
    )
}

@Composable
inline fun DurationSliderPreference(
    box: Box<Duration>,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null,
    noinline title: @Composable (() -> Unit)? = null,
    noinline summary: @Composable (() -> Unit)? = null,
    noinline leading: @Composable (() -> Unit)? = null,
    noinline valueText: @Composable ((Duration) -> Unit)? = null,
    valueRange: ClosedRange<Duration>,
    steps: Int = 0
) {
    key(box) {
        DurationSliderPreference(
            valueController = ValueController(box),
            enabled = enabled,
            dependencies = dependencies,
            title = title,
            summary = summary,
            leading = leading,
            valueRange = valueRange,
            steps = steps,
            valueText = valueText
        )
    }
}

@Composable
fun DurationSliderPreference(
    valueController: ValueController<Duration>,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null,
    title: @Composable (() -> Unit)? = null,
    summary: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    valueText: @Composable ((Duration) -> Unit)? = null,
    valueRange: ClosedRange<Duration>,
    steps: Int = 0
) {
    BaseSliderPreference(
        valueController = valueController,
        toFloat = { it.toFloat() },
        fromFloat = { it.toDuration() },
        enabled = enabled,
        dependencies = dependencies,
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
    valueController: ValueController<T>,
    toFloat: (T) -> Float,
    fromFloat: (Float) -> T,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null,
    title: @Composable (() -> Unit)? = null,
    summary: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    valueRange: ClosedRange<T>,
    steps: Int = 0,
    valueText: @Composable ((T) -> Unit)? = null
) {
    PreferenceWrapper(
        valueController = valueController,
        enabled = enabled,
        dependencies = dependencies
    ) { context ->
        Stack {
            PreferenceLayout(
                modifier = Modifier.gravity(Alignment.BottomCenter)
                    .padding(bottom = 32.dp),
                title = title,
                summary = summary,
                leading = leading,
                enabled = false,
                onClick = {}
            )

            val listItemStyle = ListItemStyleAmbient.currentOrElse { DefaultListItemStyle() }

            Row(
                modifier = Modifier.gravity(Alignment.BottomCenter)
                    .padding(
                        start = listItemStyle.contentPadding.start - 4.dp, // make the slider pretty
                        end = listItemStyle.contentPadding.end
                    ),
                crossAxisAlignment = CrossAxisAlignment.Center
            ) {
                val sliderState = state { toFloat(context.currentValue) }

                val floatRange = remember(toFloat, valueRange) {
                    toFloat(valueRange.start)..toFloat(valueRange.endInclusive)
                }

                remember(context.currentValue) { sliderState.value = toFloat(context.currentValue) }

                Slider(
                    value = sliderState.value,
                    onValueChange = { newFloatValue ->
                        if (context.shouldBeEnabled &&
                            valueController.canSetValue(fromFloat(newFloatValue))
                        ) {
                            sliderState.value = newFloatValue
                        }
                    },
                    onValueChangeEnd = {
                        if (context.shouldBeEnabled) {
                            context.setIfOk(fromFloat(sliderState.value))
                        }
                    },
                    valueRange = floatRange,
                    steps = steps,
                    modifier = LayoutFlexible(1f)
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
