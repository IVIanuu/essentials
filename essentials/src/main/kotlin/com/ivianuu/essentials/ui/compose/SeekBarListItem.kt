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

/**
import android.graphics.drawable.Drawable
import com.airbnb.epoxy.EpoxyController
import com.ivianuu.kprefs.Pref

fun ComponentComposition.SeekBarListItem(
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

enabled: Boolean = true,

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
enabled = enabled,
builderBlock = {
state(value, onChange != null, max, min, inc, valueTextProvider != null)

val finalPrimaryActionLayoutRes = when {
avatar != null || avatarRes != null -> R.layout.es_list_action_avatar
icon != null || iconRes != null -> R.layout.es_list_action_icon
else -> null
}

viewType(R.layout.es_list_item + (finalPrimaryActionLayoutRes ?: 0))

buildView {
val view = it.inflate(R.layout.es_item_seek_bar)

if (finalPrimaryActionLayoutRes != null) {
view.es_list_primary_action_container.inflate(finalPrimaryActionLayoutRes, true)
}

return@buildView view
}

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

es_seekbar.isEnabled = enabled && onChange != null
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