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
import androidx.ui.core.Alignment
import androidx.ui.core.ContextAmbient
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.graphics.Image
import androidx.ui.layout.Container
import androidx.ui.layout.DpConstraints
import androidx.ui.layout.EdgeInsets
import androidx.ui.layout.Padding
import androidx.ui.layout.Spacing
import androidx.ui.layout.Stack
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Slider
import androidx.ui.material.SliderPosition
import com.ivianuu.essentials.store.Box
import com.ivianuu.essentials.ui.compose.common.asIconComposable
import com.ivianuu.essentials.ui.compose.common.asTextComposable
import com.ivianuu.essentials.ui.compose.core.ambient
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.core.composableWithKey
import com.ivianuu.essentials.ui.compose.core.effect
import com.ivianuu.essentials.ui.compose.core.invoke
import com.ivianuu.essentials.ui.compose.core.invokeAsComposable
import com.ivianuu.essentials.ui.compose.core.remember
import com.ivianuu.essentials.ui.compose.core.state
import com.ivianuu.essentials.ui.compose.layout.CrossAxisAlignment
import com.ivianuu.essentials.ui.compose.layout.Row
import com.ivianuu.essentials.ui.compose.layout.WithModifier
import com.ivianuu.essentials.util.UnitValueTextProvider

@Composable
fun SliderPreference(
    box: Box<Int>,
    onChange: ((Int) -> Boolean)? = null,
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
) = composableWithKey("SliderPreference:$box") {
    SliderPreference(
        box = box,
        onChange = onChange,
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

@Composable
fun SliderPreference(
    box: Box<Int>,
    onChange: ((Int) -> Boolean)? = null,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null,
    title: (@Composable() () -> Unit)? = null,
    summary: (@Composable() () -> Unit)? = null,
    leading: (@Composable() () -> Unit)? = null,
    valueRange: IntRange = 0..100,
    steps: Int? = null,
    valueText: @Composable() ((Int) -> Unit)? = {
        SimpleSliderValueText(it)
    }
) = composableWithKey("SliderPreference:$box") {
    PreferenceWrapper(
        box = box,
        onChange = onChange,
        enabled = enabled,
        dependencies = dependencies
    ) { context ->
        Stack {
            aligned(Alignment.BottomCenter) {
                Padding(bottom = 32.dp) {
                    PreferenceLayout(
                        title = title,
                        summary = summary,
                        leading = leading
                    )
                }
            }

            aligned(Alignment.BottomCenter) {
                Row(crossAxisAlignment = CrossAxisAlignment.Center) {
                    val internalValue = state { context.currentValue }

                    val onChanged: ((Int) -> Unit)? = if (context.dependenciesOk) {
                        { newValue ->
                            if (onChange?.invoke(newValue) != false) {
                                internalValue.value = newValue
                            }
                        }
                    } else {
                        null
                    }

                    WithModifier(modifier = Flexible(1f) wraps Spacing(left = 8.dp)) {
                        val position = remember(valueRange, steps) {
                            val initial = context.currentValue.toFloat()
                            val floatRange = valueRange.first.toFloat()..valueRange.last.toFloat()
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
                                    onChanged?.invoke(it.toInt())
                                }
                            },
                            onValueChangeEnd = {
                                if (context.shouldBeEnabled) {
                                    context.setIfOk(position.value.toInt())
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
                            valueText.invokeAsComposable(internalValue.value)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SimpleSliderValueText(value: Int) = composable {
    Text(
        text = value.toString(),
        style = MaterialTheme.typography()().body2,
        maxLines = 1
    )
}

@Composable
fun unitValueTextProvider(
    unit: UnitValueTextProvider.Unit
): @Composable() (Int) -> Unit = effect {
    val textProvider = UnitValueTextProvider(
        ambient(ContextAmbient), unit
    )
    return@effect {
        Text(
            text = textProvider(it),
            style = MaterialTheme.typography()().body2,
            maxLines = 1
        )
    }
}