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

import com.ivianuu.compose.ComponentComposition
import com.ivianuu.essentials.R
import com.ivianuu.kprefs.Pref

fun ComponentComposition.RadioButton(
    value: Boolean,
    onChange: (Boolean) -> Unit
) {
    CompoundButton(
        layoutRes = R.layout.es_radio_button,
        value = value,
        onChange = onChange
    )
}

fun ComponentComposition.RadioButton(
    pref: Pref<Boolean>,
    onChangePredicate: ((Boolean) -> Boolean)? = null
) {
    CompoundButton(
        layoutRes = R.layout.es_radio_button,
        pref = pref,
        onChangePredicate = onChangePredicate
    )
}

fun ComponentComposition.RadioButtonPreference(
    pref: Pref<Boolean>,
    onChangePredicate: ((Boolean) -> Boolean)? = null,
    title: (ComponentComposition.() -> Unit)? = null,
    text: (ComponentComposition.() -> Unit)? = null,
    leading: (ComponentComposition.() -> Unit)? = null,
    enabled: Boolean = true
) {
    val value = pref.get()
    ListItem(
        title = title,
        text = text,
        leading = leading,
        enabled = enabled,
        trailing = { RadioButton(pref = pref, onChangePredicate = onChangePredicate) },
        onClick = {
            val newValue = !value
            if (onChangePredicate?.invoke(newValue) ?: true) {
                pref.set(newValue)
            }
        }
    )
}

/**

data class RadioButtonItem<T>(
val value: T,

val title: String? = null,
val titleRes: Int? = null,

val text: String? = null,
val textRes: Int? = null,

val icon: Drawable? = null,
val iconRes: Int? = null,

val avatar: Drawable? = null,
val avatarRes: Int? = null,

val enabled: Boolean = true
)

fun <T> EpoxyController.RadioButtonGroup(
value: T,
items: List<RadioButtonItem<T>>,
onSelected: (T) -> Unit
) {
items.forEach { item ->
RadioButtonListItem(
id = item.value,
value = value == item.value,
onChange = { onSelected(item.value) },
title = item.title,
titleRes = item.titleRes,
text = item.text,
textRes = item.textRes,
icon = item.icon,
iconRes = item.iconRes,
avatar = item.avatar,
avatarRes = item.avatarRes,
enabled = item.enabled
)
}
}

fun <T> EpoxyController.RadioButtonGroup(
pref: Pref<T>,
items: List<RadioButtonItem<T>>,
onSelectedPredicate: ((T) -> Boolean)? = null
) {
RadioButtonGroup(
value = pref.get(),
items = items,
onSelected = {
if (onSelectedPredicate == null || onSelectedPredicate(it)) {
pref.set(it)
}
}
)
}*/