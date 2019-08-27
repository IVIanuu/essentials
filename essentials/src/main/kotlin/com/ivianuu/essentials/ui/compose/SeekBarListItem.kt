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

import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.view.isVisible
import com.ivianuu.compose.ComponentComposition
import com.ivianuu.compose.ViewByLayoutRes
import com.ivianuu.compose.onBindView
import com.ivianuu.essentials.R
import com.ivianuu.kommon.core.widget.setOnSeekBarChangeListener
import com.ivianuu.kprefs.Pref
import kotlinx.android.synthetic.main.es_list_item_seek_bar.view.*
import java.lang.Math.round

fun ComponentComposition.SeekBarListItem(
    title: String? = null,
    titleRes: Int? = null,

    text: String? = null,
    textRes: Int? = null,

    leadingAction: (ComponentComposition.() -> Unit)? = null,

    value: Int,
    onChange: ((Int) -> Unit)? = null,
    max: Int = 100,
    min: Int = 0,
    inc: Int = 1,
    valueTextProvider: ((Int) -> String)? = { it.toString() },

    icon: Drawable? = null,
    iconRes: Int? = null,

    avatar: Drawable? = null,
    avatarRes: Int? = null,

    enabled: Boolean = true
) {
    ViewByLayoutRes<View>(layoutRes = R.layout.es_list_item_seek_bar) {
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

/**
fun ComponentComposition.SeekBarListItem(
    pref: Pref<Int>,

    id: Any? = pref.key,

    onChangePredicate: ((Int) -> Boolean)? = null,
    max: Int = 100,
    min: Int = 0,
    inc: Int = 1,
    valueTextProvider: ((Int) -> String)? = { it.toString() },

    title: String? = null,
    titleRes: Int? = null,

    text: String? = null,
    textRes: Int? = null,

    icon: Drawable? = null,
    iconRes: Int? = null,

    avatar: Drawable? = null,
    avatarRes: Int? = null,

    enabled: Boolean = true,

    builderBlock: (FunModelBuilder.() -> Unit)? = null
) = SeekBarListItem(
    id = id,
    value = pref.get(),
    onChange = {
        if (onChangePredicate == null || onChangePredicate(it)) {
            pref.set(it)
        }
    },
    max = max,
    min = min,
    inc = inc,
    valueTextProvider = valueTextProvider,
    title = title,
    titleRes = titleRes,
    text = text,
    textRes = textRes,
    icon = icon,
    iconRes = iconRes,
    avatar = avatar,
    avatarRes = avatarRes,
    enabled = enabled,
    builderBlock = builderBlock
)*/