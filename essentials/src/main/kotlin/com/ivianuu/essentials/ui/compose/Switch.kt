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

import androidx.appcompat.widget.SwitchCompat
import com.ivianuu.compose.ComponentComposition
import com.ivianuu.compose.View
import com.ivianuu.compose.layoutRes
import com.ivianuu.essentials.R
import com.ivianuu.kprefs.Pref

fun ComponentComposition.Switch(pref: Pref<Boolean>) {
    Switch(value = pref.get(), onChange = { pref.set(it) })
}

fun ComponentComposition.Switch(
    value: Boolean,
    onChange: (Boolean) -> Unit
) {
    View<SwitchCompat> {
        layoutRes(R.layout.es_switch)
        bindView {
            isChecked = value
            setOnClickListener { onChange(!value) }
        }
    }
}