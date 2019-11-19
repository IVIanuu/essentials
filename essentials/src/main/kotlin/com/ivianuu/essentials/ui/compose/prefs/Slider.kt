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
import androidx.ui.core.Opacity
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.layout.Container
import androidx.ui.layout.CrossAxisAlignment
import androidx.ui.layout.DpConstraints
import androidx.ui.layout.EdgeInsets
import androidx.ui.layout.Padding
import androidx.ui.layout.Row
import androidx.ui.layout.Spacing
import androidx.ui.layout.Stack
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Slider
import androidx.ui.material.SliderPosition
import com.ivianuu.essentials.ui.compose.core.ambient
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.core.composableWithKey
import com.ivianuu.essentials.ui.compose.core.effect
import com.ivianuu.essentials.ui.compose.core.invoke
import com.ivianuu.essentials.ui.compose.core.invokeAsComposable
import com.ivianuu.essentials.ui.compose.core.memo
import com.ivianuu.essentials.ui.compose.core.state
import com.ivianuu.essentials.ui.compose.layout.WithModifier
import com.ivianuu.essentials.util.UnitValueTextProvider
import com.ivianuu.kprefs.Pref

@Composable
fun SliderPreference(
    pref: Pref<Int>,
    valueRange: IntRange = 0..100,
    steps: Int? = null,
    title: @Composable() () -> Unit,
    summary: @Composable() (() -> Unit)? = null,
    leading: @Composable() (() -> Unit)? = null,
    valueText: @Composable() ((Int) -> Unit)? = {
        SimpleSliderValueText(it)
    },
    onChange: ((Int) -> Boolean)? = null,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null
) = composableWithKey("SliderPreference:${pref.key}") {
    val finalEnabled = enabled && dependencies?.checkAll() ?: true

    fun valueChanged(newValue: Int) {
        if (onChange?.invoke(newValue) != false) {
            pref.set(newValue)
        }
    }

    Stack {
        aligned(Alignment.BottomCenter) {
            Padding(bottom = 32.dp) {
                Preference(
                    pref = pref,
                    title = title,
                    summary = summary,
                    leading = leading,
                    onChange = onChange,
                    enabled = enabled,
                    dependencies = dependencies
                )
            }
        }

        aligned(Alignment.BottomCenter) {
            Row(
                crossAxisAlignment = CrossAxisAlignment.Center
            ) {
                val internalValue = state { pref.get() }

                val onChanged: ((Int) -> Unit)? = if (dependencies.checkAll()) {
                    { newValue ->
                        if (onChange?.invoke(newValue) != false) {
                            internalValue.value = newValue
                        }
                    }
                } else {
                    null
                }

                WithModifier(modifier = Flexible(1f) wraps Spacing(left = 8.dp)) {
                    val position = memo(valueRange, steps) {
                        val initial = pref.get().toFloat()
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
                            position.value = it
                            onChanged?.invoke(it.toInt())
                        },
                        onValueChangeEnd = { valueChanged(position.value.toInt()) }
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
                        Opacity(if (finalEnabled) 1f else 0.5f) {
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