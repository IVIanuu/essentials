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

package com.ivianuu.essentials.android.ui.prefs

import androidx.compose.Composable
import androidx.compose.Pivotal
import androidx.compose.remember
import androidx.ui.core.Alignment
import androidx.ui.core.AnimationClockAmbient
import androidx.ui.core.ContextAmbient
import androidx.ui.layout.Container
import androidx.ui.layout.DpConstraints
import androidx.ui.layout.LayoutGravity
import androidx.ui.layout.LayoutPadding
import androidx.ui.layout.Stack
import androidx.ui.material.MaterialTheme
import androidx.ui.material.SliderPosition
import androidx.ui.unit.Dp
import androidx.ui.unit.IntPx
import androidx.ui.unit.Px
import androidx.ui.unit.dp
import androidx.ui.unit.ipx
import androidx.ui.unit.px
import com.ivianuu.essentials.android.ui.core.Text
import com.ivianuu.essentials.android.ui.core.currentOrNull
import com.ivianuu.essentials.android.ui.layout.CrossAxisAlignment
import com.ivianuu.essentials.android.ui.layout.Row
import com.ivianuu.essentials.android.ui.material.DefaultListItemStyle
import com.ivianuu.essentials.android.ui.material.ListItemStyleAmbient
import com.ivianuu.essentials.android.ui.material.Slider
import com.ivianuu.essentials.android.util.UnitValueTextProvider
import com.ivianuu.essentials.store.Box
import com.ivianuu.essentials.util.cast
import kotlin.time.Duration

@Composable
fun DoubleSliderPreference(
    @Pivotal box: Box<Double>,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null,
    title: @Composable (() -> Unit)? = null,
    summary: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    valueRange: ClosedFloatingPointRange<Double> = 0.0..1.0,
    steps: Int = 0,
    valueText: @Composable ((Double) -> Unit)? = null
) {
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

@Composable
fun DoubleSliderPreference(
    valueController: ValueController<Double>,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null,
    title: @Composable (() -> Unit)? = null,
    summary: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    valueRange: ClosedFloatingPointRange<Double> = 0.0..1.0,
    steps: Int = 0,
    valueText: @Composable ((Double) -> Unit)? = null
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
fun FloatSliderPreference(
    @Pivotal box: Box<Float>,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null,
    title: @Composable (() -> Unit)? = null,
    summary: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    valueText: @Composable ((Float) -> Unit)? = null
) {
    FloatSliderPreference(
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

@Composable
fun FloatSliderPreference(
    valueController: ValueController<Float>,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null,
    title: @Composable (() -> Unit)? = null,
    summary: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    valueText: @Composable ((Float) -> Unit)? = null
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
fun IntSliderPreference(
    @Pivotal box: Box<Int>,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null,
    title: @Composable (() -> Unit)? = null,
    summary: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    valueRange: IntRange = 0..100,
    steps: Int = 0,
    valueText: @Composable ((Int) -> Unit)? = null
) {
    IntSliderPreference(
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

@Composable
fun IntSliderPreference(
    valueController: ValueController<Int>,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null,
    title: @Composable (() -> Unit)? = null,
    summary: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    valueRange: IntRange = 0..100,
    steps: Int = 0,
    valueText: @Composable ((Int) -> Unit)? = null
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
        valueRange = valueRange,
        steps = steps,
        valueText = valueText
    )
}

@Composable
fun LongSliderPreference(
    @Pivotal box: Box<Long>,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null,
    title: @Composable (() -> Unit)? = null,
    summary: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    valueRange: LongRange = 0L..100L,
    steps: Int = 0,
    valueText: @Composable ((Long) -> Unit)? = null
) {
    LongSliderPreference(
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

@Composable
fun LongSliderPreference(
    valueController: ValueController<Long>,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null,
    title: @Composable (() -> Unit)? = null,
    summary: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    valueRange: LongRange = 0L..100L,
    steps: Int = 0,
    valueText: @Composable ((Long) -> Unit)? = null
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
        valueRange = valueRange,
        steps = steps,
        valueText = valueText
    )
}

@Composable
fun DpSliderPreference(
    @Pivotal box: Box<Dp>,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null,
    title: @Composable (() -> Unit)? = null,
    summary: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    valueRange: ClosedRange<Dp> = 0.dp..1.dp,
    steps: Int = 0,
    valueText: @Composable ((Dp) -> Unit)? = null
) {
    DpSliderPreference(
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

@Composable
fun DpSliderPreference(
    valueController: ValueController<Dp>,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null,
    title: @Composable (() -> Unit)? = null,
    summary: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    valueRange: ClosedRange<Dp> = 0.dp..1.dp,
    steps: Int = 0,
    valueText: @Composable ((Dp) -> Unit)? = null
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
        valueRange = valueRange,
        steps = steps,
        valueText = valueText
    )
}

@Composable
fun PxSliderPreference(
    @Pivotal box: Box<Px>,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null,
    title: @Composable (() -> Unit)? = null,
    summary: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    valueRange: ClosedRange<Px> = 0.px..100.px,
    steps: Int = 0,
    valueText: @Composable ((Px) -> Unit)? = null
) {
    PxSliderPreference(
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

@Composable
fun PxSliderPreference(
    valueController: ValueController<Px>,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null,
    title: @Composable (() -> Unit)? = null,
    summary: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    valueRange: ClosedRange<Px> = 0.px..100.px,
    steps: Int = 0,
    valueText: @Composable ((Px) -> Unit)? = null
) {
    BaseSliderPreference(
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

@Composable
fun IntPxSliderPreference(
    @Pivotal box: Box<IntPx>,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null,
    title: @Composable (() -> Unit)? = null,
    summary: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    valueRange: ClosedRange<IntPx> = 0.ipx..100.ipx,
    steps: Int = 0,
    valueText: @Composable ((IntPx) -> Unit)? = null
) {
    IntPxSliderPreference(
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

@Composable
fun IntPxSliderPreference(
    valueController: ValueController<IntPx>,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null,
    title: @Composable (() -> Unit)? = null,
    summary: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    valueRange: ClosedRange<IntPx> = 0.ipx..100.ipx,
    steps: Int = 0,
    valueText: @Composable ((IntPx) -> Unit)? = null
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
        valueRange = valueRange,
        steps = steps,
        valueText = valueText
    )
}

@Composable
fun DurationSliderPreference(
    @Pivotal box: Box<Duration>,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null,
    title: @Composable (() -> Unit)? = null,
    summary: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    valueRange: ClosedRange<Duration>,
    steps: Int = 0,
    valueText: @Composable ((Duration) -> Unit)? = null
) {
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

@Composable
fun DurationSliderPreference(
    valueController: ValueController<Duration>,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null,
    title: @Composable (() -> Unit)? = null,
    summary: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    valueRange: ClosedRange<Duration>,
    steps: Int = 0,
    valueText: @Composable ((Duration) -> Unit)? = null
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
            Container(modifier = LayoutGravity.BottomCenter + LayoutPadding(bottom = 32.dp)) {
                PreferenceLayout(
                    title = title,
                    summary = summary,
                    leading = leading,
                    enabled = false,
                    onClick = {}
                )
            }

            val listItemStyle = ListItemStyleAmbient.currentOrNull ?: DefaultListItemStyle()

            Row(
                modifier = LayoutGravity.BottomCenter + LayoutPadding(
                    start = listItemStyle.contentPadding.left - 4.dp, // make the slider pretty
                    end = listItemStyle.contentPadding.right
                ),
                crossAxisAlignment = CrossAxisAlignment.Center
            ) {
                val animationClock = AnimationClockAmbient.current
                val position = remember(valueRange, steps) {
                    val initial = toFloat(context.currentValue)
                    val floatRange =
                        toFloat(valueRange.start)..toFloat(valueRange.endInclusive)
                    SliderPosition(
                        initial = initial,
                        valueRange = floatRange,
                        steps = steps,
                        animatedClock = animationClock
                    )
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
