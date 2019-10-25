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
import androidx.compose.state
import androidx.compose.unaryPlus
import androidx.ui.core.Alignment
import androidx.ui.core.Opacity
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.layout.Container
import androidx.ui.layout.CrossAxisAlignment
import androidx.ui.layout.DpConstraints
import androidx.ui.layout.EdgeInsets
import androidx.ui.layout.Padding
import androidx.ui.layout.Row
import androidx.ui.layout.Stack
import androidx.ui.material.themeTextStyle
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.material.Slider
import com.ivianuu.kprefs.Pref

@Composable
fun SliderPreference(
    pref: Pref<Int>,
    min: Int = 0,
    max: Int = 100,
    divisions: Int? = null,
    title: @Composable() () -> Unit,
    summary: @Composable() (() -> Unit)? = null,
    leading: @Composable() (() -> Unit)? = null,
    valueText: @Composable() ((Int) -> Unit)? = {
        SimpleSliderValueText(
            it
        )
    },
    onChange: ((Int) -> Boolean)? = null,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null
) = composable("SliderPreference:${pref.key}") {
    val finalEnabled = enabled && dependencies?.checkAll() ?: true

    fun valueChanged(newValue: Int) {
        if (onChange?.invoke(newValue) != false) {
            pref.set(newValue)
        }
    }

    Stack {
        aligned(Alignment.BottomCenter) {
            Padding(bottom = 16.dp) {
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
                val internalValue = +state { pref.get() }

                val onChanged: ((Int) -> Unit)? = if (dependencies.checkAll()) {
                    { newValue ->
                        if (onChange?.invoke(newValue) != false) {
                            internalValue.value = newValue
                        }
                    }
                } else {
                    null
                }

                Container(modifier = Flexible(1f)) {
                    Slider(
                        value = pref.get(),
                        min = min,
                        max = max,
                        divisions = divisions,
                        onChanged = onChanged,
                        onChangeEnd = { valueChanged(it) }
                    )
                }

                if (valueText != null) {
                    Container(
                        modifier = Inflexible,
                        constraints = DpConstraints(
                            minWidth = 36.dp
                        ),
                        padding = EdgeInsets(right = 8.dp)
                    ) {
                        Opacity(if (finalEnabled) 1f else 0.5f) {
                            valueText(internalValue.value)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SimpleSliderValueText(value: Int) = composable("SimpleSliderValueText") {
    Text(
        text = value.toString(),
        style = +themeTextStyle { body2 },
        maxLines = 1
    )
}