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

package com.ivianuu.essentials.ui.epoxy

import android.graphics.drawable.Drawable
import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyController
import com.ivianuu.essentials.R
import com.ivianuu.kommon.core.view.inflate
import com.ivianuu.kommon.core.widget.setOnSeekBarChangeListener
import kotlinx.android.synthetic.main.es_item_seek_bar.*
import java.lang.Math.round

fun EpoxyController.SeekBarListItem(
    id: Any?,

    value: Int,
    onChange: ((Int) -> Unit)? = null,
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

    builderBlock: (FunModelBuilder.() -> Unit)? = null
) = ListItem(
    id = id,
    title = title,
    titleRes = titleRes,
    text = text,
    textRes = textRes,
    icon = icon,
    iconRes = iconRes,
    avatar = avatar,
    avatarRes = avatarRes,
    builderBlock = {
        state(value, onChange != null, max, min, inc, valueTextProvider != null)

        // todo remove this
        buildView { it.inflate(R.layout.es_item_seek_bar) }

        bind {
            var internalValue = value

            fun syncView() {
                var progress = min + es_seekbar.progress

                if (progress < min) {
                    progress = min
                }

                if (progress > max) {
                    progress = max
                }

                internalValue = (round((progress / inc).toDouble()) * inc).toInt()

                es_seekbar.progress = internalValue - min

                if (valueTextProvider != null) {
                    val valueText = valueTextProvider(internalValue)
                    es_seekbar_value.text = valueText
                    es_seekbar_value.isVisible = true
                } else {
                    es_seekbar_value.text = null
                    es_seekbar_value.isVisible = false
                }
            }

            es_seekbar.isEnabled = onChange != null
            es_seekbar.max = max - min
            es_seekbar.progress = internalValue - min
            if (onChange != null) {
                es_seekbar.setOnSeekBarChangeListener(
                    onProgressChanged = { _, _, fromUser ->
                        if (fromUser) syncView()
                    },
                    onStopTrackingTouch = { onChange.invoke(internalValue) }
                )
            }

            syncView()
        }
        builderBlock?.invoke(this)
    }
)