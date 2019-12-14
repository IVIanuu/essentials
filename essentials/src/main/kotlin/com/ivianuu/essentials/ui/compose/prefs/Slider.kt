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

package com.ivianuu.essentials.ui.compose.prefs

import androidx.compose.Composable
import androidx.compose.Pivotal
import androidx.compose.ambient
import androidx.compose.remember
import androidx.compose.state
import androidx.ui.core.ContextAmbient
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.graphics.Image
import androidx.ui.layout.Container
import androidx.ui.layout.DpConstraints
import androidx.ui.layout.EdgeInsets
import androidx.ui.layout.LayoutGravity
import androidx.ui.layout.LayoutPadding
import androidx.ui.layout.Stack
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Slider
import androidx.ui.material.SliderPosition
import com.ivianuu.essentials.store.Box
import com.ivianuu.essentials.ui.compose.common.asIconComposable
import com.ivianuu.essentials.ui.compose.common.asTextComposable
import com.ivianuu.essentials.ui.compose.layout.CrossAxisAlignment
import com.ivianuu.essentials.ui.compose.layout.Row
import com.ivianuu.essentials.ui.compose.layout.WithModifier
import com.ivianuu.essentials.util.UnitValueTextProvider

@JvmName("DoubleSliderPreference")
@Composable
fun SliderPreference(
    @Pivotal box: Box<Double>,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null,
    title: String? = null,
    summary: String? = null,
    image: Image? = null,
    valueRange: ClosedFloatingPointRange<Double> = 0.0..1.0,
    steps: Int? = null,
    valueText: @Composable() ((Double) -> Unit)? = {
        SimpleSliderValueText(it)
    }
) {
    SliderPreference(
        valueController = ValueController(box),
        enabled = enabled,
        dependencies = dependencies,
        title = title.asTextComposable(),
        summary = summary.asTextComposable(),
        leading = image.asIconComposable(),
        valueRange = valueRange,
        steps = steps,
        valueText = valueText
    )
}

@JvmName("DoubleSliderPreference")
@Composable
fun SliderPreference(
    valueController: ValueController<Double>,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null,
    title: @Composable() (() -> Unit)? = null,
    summary: @Composable() (() -> Unit)? = null,
    leading: @Composable() (() -> Unit)? = null,
    valueRange: ClosedFloatingPointRange<Double> = 0.0..1.0,
    steps: Int? = null,
    valueText: @Composable() ((Double) -> Unit)? = {
        SimpleSliderValueText(it)
    }
) {
    SliderPreference(
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

@JvmName("FloatSliderPreference")
@Composable
fun SliderPreference(
    @Pivotal box: Box<Float>,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null,
    title: String? = null,
    summary: String? = null,
    image: Image? = null,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int? = null,
    valueText: @Composable() ((Float) -> Unit)? = {
        SimpleSliderValueText(it)
    }
) {
    SliderPreference(
        valueController = ValueController(box),
        enabled = enabled,
        dependencies = dependencies,
        title = title.asTextComposable(),
        summary = summary.asTextComposable(),
        leading = image.asIconComposable(),
        valueRange = valueRange,
        steps = steps,
        valueText = valueText
    )
}

@JvmName("FloatSliderPreference")
@Composable
fun SliderPreference(
    valueController: ValueController<Float>,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null,
    title: @Composable() (() -> Unit)? = null,
    summary: @Composable() (() -> Unit)? = null,
    leading: @Composable() (() -> Unit)? = null,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int? = null,
    valueText: @Composable() ((Float) -> Unit)? = {
        SimpleSliderValueText(it)
    }
) {
    SliderPreference(
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

@JvmName("IntSliderPreference")
@Composable
fun SliderPreference(
    @Pivotal box: Box<Int>,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null,
    title: String? = null,
    summary: String? = null,
    image: Image? = null,
    valueRange: IntRange = 0..100,
    steps: Int? = null,
    valueText: @Composable() ((Int) -> Unit)? = {
        SimpleSliderValueText(it)
    }
) {
    SliderPreference(
        valueController = ValueController(box),
        enabled = enabled,
        dependencies = dependencies,
        title = title.asTextComposable(),
        summary = summary.asTextComposable(),
        leading = image.asIconComposable(),
        valueRange = valueRange,
        steps = steps,
        valueText = valueText
    )
}

@JvmName("IntSliderPreference")
@Composable
fun SliderPreference(
    valueController: ValueController<Int>,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null,
    title: @Composable() (() -> Unit)? = null,
    summary: @Composable() (() -> Unit)? = null,
    leading: @Composable() (() -> Unit)? = null,
    valueRange: IntRange = 0..100,
    steps: Int? = null,
    valueText: @Composable() ((Int) -> Unit)? = {
        SimpleSliderValueText(it)
    }
) {
    SliderPreference(
        valueController = valueController,
        toFloat = { it.toFloat() },
        fromFloat = { it.toInt() },
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

@JvmName("LongSliderPreference")
@Composable
fun SliderPreference(
    @Pivotal box: Box<Long>,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null,
    title: String? = null,
    summary: String? = null,
    image: Image? = null,
    valueRange: LongRange = 0L..100L,
    steps: Int? = null,
    valueText: @Composable() ((Long) -> Unit)? = {
        SimpleSliderValueText(it)
    }
) {
    SliderPreference(
        valueController = ValueController(box),
        enabled = enabled,
        dependencies = dependencies,
        title = title.asTextComposable(),
        summary = summary.asTextComposable(),
        leading = image.asIconComposable(),
        valueRange = valueRange,
        steps = steps,
        valueText = valueText
    )
}

@JvmName("LongSliderPreference")
@Composable
fun SliderPreference(
    valueController: ValueController<Long>,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null,
    title: @Composable() (() -> Unit)? = null,
    summary: @Composable() (() -> Unit)? = null,
    leading: @Composable() (() -> Unit)? = null,
    valueRange: LongRange = 0L..100L,
    steps: Int? = null,
    valueText: @Composable() ((Long) -> Unit)? = {
        SimpleSliderValueText(it)
    }
) {
    SliderPreference(
        valueController = valueController,
        toFloat = { it.toFloat() },
        fromFloat = { it.toLong() },
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
fun <T : Comparable<T>> SliderPreference(
    valueController: ValueController<T>,
    toFloat: (T) -> Float,
    fromFloat: (Float) -> T,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null,
    title: @Composable() (() -> Unit)? = null,
    summary: @Composable() (() -> Unit)? = null,
    leading: @Composable() (() -> Unit)? = null,
    valueRange: ClosedRange<T>,
    steps: Int? = null,
    valueText: @Composable() ((T) -> Unit)? = {
        SimpleSliderValueText(it)
    }
) {
    PreferenceWrapper(
        valueController = valueController,
        enabled = enabled,
        dependencies = dependencies
    ) { context ->
        Stack {
            WithModifier(
                modifier = LayoutGravity.BottomCenter + LayoutPadding(bottom = 32.dp)
            ) {
                PreferenceLayout(
                    title = title,
                    summary = summary,
                    leading = leading
                )
            }

            WithModifier(modifier = LayoutGravity.BottomCenter) {
                Row(crossAxisAlignment = CrossAxisAlignment.Center) {
                    val internalValue = state { context.currentValue }

                    val onChanged: ((Float) -> Unit)? = if (context.dependenciesOk) {
                        { newValue ->
                            internalValue.value = fromFloat(newValue)
                        }
                    } else {
                        null
                    }

                    WithModifier(modifier = Flexible(1f) + LayoutPadding(left = 8.dp)) {
                        val position = remember(valueRange, steps) {
                            val initial = toFloat(context.currentValue)
                            val floatRange =
                                toFloat(valueRange.start)..toFloat(valueRange.endInclusive)
                            if (steps != null) {
                                SliderPosition(
                                    initial = initial,
                                    valueRange = floatRange,
                                    steps = steps
                                )
                            } else {
                                SliderPosition(
                                    initial = initial,
                                    valueRange = floatRange
                                )
                            }
                        }

                        Slider(
                            position = position,
                            onValueChange = {
                                if (context.shouldBeEnabled) {
                                    position.value = it
                                    onChanged?.invoke(it)
                                }
                            },
                            onValueChangeEnd = {
                                if (context.shouldBeEnabled) {
                                    context.setIfOk(fromFloat(position.value))
                                }
                            }
                        )
                    }

                    if (valueText != null) {
                        Container(
                            modifier = Inflexible,
                            constraints = DpConstraints(
                                minWidth = 72.dp
                            ),
                            padding = EdgeInsets(right = 8.dp)
                        ) {
                            valueText(internalValue.value)
                        }
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
        style = MaterialTheme.typography().body2,
        maxLines = 1
    )
}

@Composable
fun <T> unitValueTextProvider(
    unit: UnitValueTextProvider.Unit
): @Composable() (T) -> Unit {
    val textProvider = UnitValueTextProvider(
        ambient(ContextAmbient), unit
    )
    return {
        Text(
            text = textProvider(it.toString()),
            style = MaterialTheme.typography().body2,
            maxLines = 1
        )
    }
}
