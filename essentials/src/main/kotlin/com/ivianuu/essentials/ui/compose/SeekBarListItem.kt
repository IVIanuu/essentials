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

package com.ivianuu.essentials.ui.compose

import android.view.View
import androidx.core.view.children
import androidx.core.view.isVisible
import com.ivianuu.compose.ComponentComposition
import com.ivianuu.compose.ContextAmbient
import com.ivianuu.compose.ViewById
import com.ivianuu.compose.ViewByLayoutRes
import com.ivianuu.compose.ambient
import com.ivianuu.compose.onBindView
import com.ivianuu.compose.set
import com.ivianuu.essentials.R
import com.ivianuu.essentials.util.getSecondaryTextColor
import com.ivianuu.kommon.core.widget.setOnSeekBarChangeListener
import com.ivianuu.kprefs.Pref
import kotlinx.android.synthetic.main.es_list_item_seek_bar.view.*
import java.lang.Math.round

fun ComponentComposition.SeekBarListItem(
    title: (ComponentComposition.() -> Unit)? = null,
    text: (ComponentComposition.() -> Unit)? = null,
    leading: (ComponentComposition.() -> Unit)? = null,

    value: Int,
    onChange: ((Int) -> Unit)? = null,
    max: Int = 100,
    min: Int = 0,
    inc: Int = 1,
    valueTextProvider: ((Int) -> String)? = { it.toString() },

    enabled: Boolean = true
) {
    ViewByLayoutRes<View>(layoutRes = R.layout.es_list_item_seek_bar) {
        set(enabled) { enabled ->
            es_list_text_container.isEnabled = enabled
            es_list_text_container.children.forEach {
                it.isEnabled = enabled
            }

            es_list_leading.isEnabled = enabled
            es_list_leading.children.forEach {
                it.isEnabled = enabled
            }

            isEnabled = enabled
        }

        ViewById<View>(id = R.id.es_list_text_container) {
            if (title != null) {
                TextStyle(textAppearance = R.style.TextAppearance_MaterialComponents_Subtitle1) {
                    title()
                }
            }

            if (text != null) {
                TextStyle(
                    textAppearance = R.style.TextAppearance_AppCompat_Body2,
                    textColor = ambient(ContextAmbient).getSecondaryTextColor()
                ) {
                    text()
                }
            }
        }

        ViewById<View>(id = R.id.es_list_leading) {
            leading?.invoke(composition)
        }

        onBindView {
            var internalValue = value

            fun syncView() {
                var progress = min + it.es_seekbar.progress

                if (progress < min) {
                    progress = min
                }

                if (progress > max) {
                    progress = max
                }

                internalValue = (round((progress / inc).toDouble()) * inc).toInt()

                it.es_seekbar.progress = internalValue - min

                if (valueTextProvider != null) {
                    val valueText = valueTextProvider(internalValue)
                    it.es_seekbar_value.text = valueText
                    it.es_seekbar_value.isVisible = true
                } else {
                    it.es_seekbar_value.text = null
                    it.es_seekbar_value.isVisible = false
                }
            }

            it.es_seekbar.isEnabled = enabled && onChange != null
            it.es_seekbar.max = max - min
            it.es_seekbar.progress = internalValue - min
            if (onChange != null) {
                it.es_seekbar.setOnSeekBarChangeListener(
                    onProgressChanged = { _, _, fromUser ->
                        if (fromUser) syncView()
                    },
                    onStopTrackingTouch = { onChange.invoke(internalValue) }
                )
            }

            syncView()
        }
    }
}

fun ComponentComposition.SeekBarListItem(
    title: (ComponentComposition.() -> Unit)? = null,
    text: (ComponentComposition.() -> Unit)? = null,
    leading: (ComponentComposition.() -> Unit)? = null,

    pref: Pref<Int>,
    onChangePredicate: ((Int) -> Boolean)? = null,

    max: Int = 100,
    min: Int = 0,
    inc: Int = 1,
    valueTextProvider: ((Int) -> String)? = { it.toString() },

    enabled: Boolean = true
) {
    SeekBarListItem(
        title = title,
        text = text,
        leading = leading,
        value = pref.get(),
        onChange = {
            if (onChangePredicate?.invoke(it) ?: true) {
                pref.set(it)
            }
        },
        max = max,
        min = min,
        inc = inc,
        valueTextProvider = valueTextProvider,
        enabled = enabled
    )
}