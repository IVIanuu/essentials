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
import androidx.compose.Pivotal
import androidx.compose.ambient
import androidx.compose.remember
import androidx.ui.core.Alignment
import androidx.ui.core.ContextAmbient
import androidx.ui.core.Dp
import androidx.ui.core.IntPx
import androidx.ui.core.Px
import androidx.ui.core.dp
import androidx.ui.core.ipx
import androidx.ui.core.px
import androidx.ui.graphics.Image
import androidx.ui.layout.Container
import androidx.ui.layout.DpConstraints
import androidx.ui.layout.LayoutGravity
import androidx.ui.layout.LayoutPadding
import androidx.ui.layout.Stack
import androidx.ui.material.MaterialTheme
import com.ivianuu.essentials.store.Box
import com.ivianuu.essentials.ui.common.asIconComposable
import com.ivianuu.essentials.ui.common.asTextComposable
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.layout.CrossAxisAlignment
import com.ivianuu.essentials.ui.layout.Row
import com.ivianuu.essentials.ui.layout.WithModifier
import com.ivianuu.essentials.ui.material.DefaultListItemStyle
import com.ivianuu.essentials.ui.material.ListItemStyleAmbient
import com.ivianuu.essentials.ui.material.Slider
import com.ivianuu.essentials.ui.material.SliderPosition
import com.ivianuu.essentials.util.UnitValueTextProvider
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

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
    valueText: @Composable() ((Double) -> Unit)? = null
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
    valueText: @Composable() ((Double) -> Unit)? = null
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
    valueText: @Composable() ((Float) -> Unit)? = null
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
    valueText: @Composable() ((Float) -> Unit)? = null
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
    valueText: @Composable() ((Int) -> Unit)? = null
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
    valueText: @Composable() ((Int) -> Unit)? = null
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
    valueText: @Composable() ((Long) -> Unit)? = null
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
    valueText: @Composable() ((Long) -> Unit)? = null
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

@JvmName("DpSliderPreference")
@Composable
fun SliderPreference(
    @Pivotal box: Box<Dp>,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null,
    title: String? = null,
    summary: String? = null,
    image: Image? = null,
    valueRange: ClosedRange<Dp> = 0.dp..1.dp,
    steps: Int? = null,
    valueText: @Composable() ((Dp) -> Unit)? = null
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

@JvmName("DpSliderPreference")
@Composable
fun SliderPreference(
    valueController: ValueController<Dp>,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null,
    title: @Composable() (() -> Unit)? = null,
    summary: @Composable() (() -> Unit)? = null,
    leading: @Composable() (() -> Unit)? = null,
    valueRange: ClosedRange<Dp> = 0.dp..1.dp,
    steps: Int? = null,
    valueText: @Composable() ((Dp) -> Unit)? = null
) {
    SliderPreference(
        valueController = valueController,
        toFloat = { it.value },
        fromFloat = { it.dp },
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

@JvmName("PxSliderPreference")
@Composable
fun SliderPreference(
    @Pivotal box: Box<Px>,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null,
    title: String? = null,
    summary: String? = null,
    image: Image? = null,
    valueRange: ClosedRange<Px> = 0.px..100.px,
    steps: Int? = null,
    valueText: @Composable() ((Px) -> Unit)? = null
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

@JvmName("PxSliderPreference")
@Composable
fun SliderPreference(
    valueController: ValueController<Px>,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null,
    title: @Composable() (() -> Unit)? = null,
    summary: @Composable() (() -> Unit)? = null,
    leading: @Composable() (() -> Unit)? = null,
    valueRange: ClosedRange<Px> = 0.px..100.px,
    steps: Int? = null,
    valueText: @Composable() ((Px) -> Unit)? = null
) {
    SliderPreference(
        valueController = valueController,
        toFloat = { it.value },
        fromFloat = { it.px },
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

@JvmName("IntPxSliderPreference")
@Composable
fun SliderPreference(
    @Pivotal box: Box<IntPx>,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null,
    title: String? = null,
    summary: String? = null,
    image: Image? = null,
    valueRange: ClosedRange<IntPx> = 0.ipx..100.ipx,
    steps: Int? = null,
    valueText: @Composable() ((IntPx) -> Unit)? = null
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

@JvmName("IntPxSliderPreference")
@Composable
fun SliderPreference(
    valueController: ValueController<IntPx>,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null,
    title: @Composable() (() -> Unit)? = null,
    summary: @Composable() (() -> Unit)? = null,
    leading: @Composable() (() -> Unit)? = null,
    valueRange: ClosedRange<IntPx> = 0.ipx..100.ipx,
    steps: Int? = null,
    valueText: @Composable() ((IntPx) -> Unit)? = null
) {
    SliderPreference(
        valueController = valueController,
        toFloat = { it.value.toFloat() },
        fromFloat = { it.toInt().ipx },
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

@JvmName("DurationSliderPreference")
@Composable
fun SliderPreference(
    @Pivotal box: Box<Duration>,
    unit: DurationUnit,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null,
    title: String? = null,
    summary: String? = null,
    image: Image? = null,
    valueRange: ClosedRange<Duration>,
    steps: Int? = null,
    valueText: @Composable() ((Duration) -> Unit)? = null
) {
    SliderPreference(
        valueController = ValueController(box),
        unit = unit,
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

@JvmName("DurationSliderPreference")
@Composable
fun SliderPreference(
    valueController: ValueController<Duration>,
    unit: DurationUnit,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null,
    title: @Composable() (() -> Unit)? = null,
    summary: @Composable() (() -> Unit)? = null,
    leading: @Composable() (() -> Unit)? = null,
    valueRange: ClosedRange<Duration>,
    steps: Int? = null,
    valueText: @Composable() ((Duration) -> Unit)? = null
) {
    SliderPreference(
        valueController = valueController,
        toFloat = {
            it.toLongMilliseconds().toFloat()
        },
        fromFloat = { DurationUnit.MILLISECONDS.convert(it.toLong(), unit).toDuration(unit) },
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
    valueText: @Composable() ((T) -> Unit)? = null
) {
    PreferenceWrapper(
        valueController = valueController,
        enabled = enabled,
        dependencies = dependencies
    ) { context ->
        Stack {
            WithModifier(modifier = LayoutGravity.BottomCenter + LayoutPadding(bottom = 32.dp)) {
                PreferenceLayout(
                    title = title,
                    summary = summary,
                    leading = leading
                )
            }

            val listItemStyle = ambient(ListItemStyleAmbient) ?: DefaultListItemStyle()

            Row(
                modifier = LayoutGravity.BottomCenter + LayoutPadding(
                    left = listItemStyle.contentPadding.left - 8.dp, // make the slider pretty
                    right = listItemStyle.contentPadding.right
                ),
                crossAxisAlignment = CrossAxisAlignment.Center
            ) {
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

                remember(context.currentValue) { position.value = toFloat(context.currentValue) }

                Slider(
                    position = position,
                    modifier = LayoutFlexible(1f),
                    onValueChange = { newFloatValue ->
                        if (context.shouldBeEnabled &&
                            valueController.canSetValue(fromFloat(newFloatValue))
                        ) {
                            position.value = newFloatValue
                        }
                    },
                    onValueChangeEnd = {
                        if (context.shouldBeEnabled) {
                            context.setIfOk(fromFloat(position.value))
                        }
                    }
                )

                if (valueText != null) {
                    Container(
                        modifier = LayoutInflexible,
                        alignment = Alignment.Center,
                        constraints = DpConstraints(
                            minWidth = 72.dp
                        )
                    ) {
                        valueText(fromFloat(position.value))
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
