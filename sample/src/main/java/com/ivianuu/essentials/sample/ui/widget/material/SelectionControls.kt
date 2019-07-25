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

package com.ivianuu.essentials.sample.ui.widget.material

import android.widget.CompoundButton
import android.widget.Switch
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.radiobutton.MaterialRadioButton
import com.ivianuu.essentials.sample.ui.widget.lib.*
import com.ivianuu.essentials.sample.ui.widget.view.Clickable
import com.ivianuu.essentials.sample.ui.widget.view.Size
import com.ivianuu.kommon.core.content.dp
import kotlin.reflect.KClass

fun BuildContext.CheckBox(
    value: Boolean,
    onChange: (Boolean) -> Unit
) = StatelessWidget("CheckBox") {
    val context = +ContextAmbient
    +Size(size = context.dp(48).toInt()) {
        +CompoundButton(
            value = value,
            onChange = onChange,
            createView = { MaterialCheckBox(it.context) },
            viewType = MaterialCheckBox::class
        )
    }
}

fun BuildContext.RadioButton(
    value: Boolean,
    onChange: (Boolean) -> Unit
) = StatelessWidget("RadioButton") {
    val context = +ContextAmbient
    +Size(size = context.dp(48).toInt()) {
        +CompoundButton(
            value = value,
            onChange = onChange,
            createView = { MaterialRadioButton(it.context) },
            viewType = MaterialRadioButton::class
        )
    }
}

fun BuildContext.Switch(
    value: Boolean,
    onChange: (Boolean) -> Unit
) = StatelessWidget("Switch") {
    val context = +ContextAmbient
    +Size(size = context.dp(48).toInt()) {
        +CompoundButton(
            value = value,
            onChange = onChange,
            createView = { Switch(it.context) },
            viewType = Switch::class
        )
    }
}

fun <V : CompoundButton> BuildContext.CompoundButton(
    value: Boolean,
    onChange: (Boolean) -> Unit,
    createView: CreateView<V>,
    viewType: KClass<V>
): Widget = Clickable(
    onClick = { onChange(!value) },
    child = {
        +ViewWidget(
            viewType = viewType,
            createView = createView,
            updateView = { it.isChecked = value }
        )
    }
)