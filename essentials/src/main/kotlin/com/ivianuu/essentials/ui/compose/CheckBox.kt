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
import com.ivianuu.compose.internal.log
import com.ivianuu.essentials.R
import com.ivianuu.kprefs.Pref

fun ComponentComposition.CheckBox(
    value: Boolean,
    onChange: (Boolean) -> Unit
) {
    CompoundButton(
        layoutRes = R.layout.es_checkbox,
        value = value,
        onChange = onChange
    )
}

fun ComponentComposition.CheckBox(
    pref: Pref<Boolean>,
    onChangePredicate: ((Boolean) -> Boolean)? = null
) {
    CompoundButton(
        layoutRes = R.layout.es_checkbox,
        pref = pref,
        onChangePredicate = onChangePredicate
    )
}

fun ComponentComposition.CheckBoxPreference(
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
        trailing = { CheckBox(pref = pref, onChangePredicate = onChangePredicate) },
        onClick = {
            val newValue = !value
            if (onChangePredicate?.invoke(newValue) ?: true) {
                pref.set(newValue)
            }
        }
    )
}