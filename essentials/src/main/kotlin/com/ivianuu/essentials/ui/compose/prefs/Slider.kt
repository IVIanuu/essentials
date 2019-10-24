package com.ivianuu.essentials.ui.compose.prefs

import androidx.compose.Composable
import androidx.compose.state
import androidx.compose.unaryPlus
import androidx.ui.core.Alignment
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.layout.ConstrainedBox
import androidx.ui.layout.CrossAxisAlignment
import androidx.ui.layout.DpConstraints
import androidx.ui.layout.FlexRow
import androidx.ui.layout.Padding
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
    title: @Composable() (() -> Unit),
    summary: @Composable() (() -> Unit)? = null,
    singleLineSummary: Boolean = true,
    leading: @Composable() (() -> Unit)? = null,
    valueText: @Composable() ((Int) -> Unit)? = { SimpleSliderValueText(it) },
    onChange: ((Int) -> Boolean)? = null,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null
) = composable("SliderPreference") {
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
                    singleLineSummary = singleLineSummary,
                    leading = leading,
                    onChange = onChange,
                    enabled = enabled,
                    dependencies = dependencies
                )
            }
        }

        aligned(Alignment.BottomCenter) {
            FlexRow(
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

                flexible(1f) {
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
                    inflexible {
                        Padding(right = 8.dp) {
                            ConstrainedBox(
                                constraints = DpConstraints(
                                    minWidth = 36.dp
                                )
                            ) {
                                valueText(internalValue.value)
                            }
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