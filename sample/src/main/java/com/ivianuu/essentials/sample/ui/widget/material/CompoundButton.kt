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
import com.ivianuu.essentials.sample.ui.widget.lib.AndroidContextAmbient
import com.ivianuu.essentials.sample.ui.widget.lib.BuildContext
import com.ivianuu.essentials.sample.ui.widget.lib.CreateView
import com.ivianuu.essentials.sample.ui.widget.lib.ViewWidget
import com.ivianuu.essentials.sample.ui.widget.lib.Widget
import com.ivianuu.essentials.sample.ui.widget.view.Clickable
import kotlin.reflect.KClass

fun BuildContext.CheckBox(
    value: Boolean,
    onChange: (Boolean) -> Unit,
    key: Any? = null
) = CompoundButton(
    value = value,
    onChange = onChange,
    createView = { MaterialCheckBox(+AndroidContextAmbient) },
    viewType = MaterialCheckBox::class,
    key = key
)

fun BuildContext.RadioButton(
    value: Boolean,
    onChange: (Boolean) -> Unit,
    key: Any? = null
) = CompoundButton(
    value = value,
    onChange = onChange,
    createView = { MaterialRadioButton(+AndroidContextAmbient) },
    viewType = MaterialRadioButton::class,
    key = key
)

fun BuildContext.Switch(
    value: Boolean,
    onChange: (Boolean) -> Unit,
    key: Any? = null
) = CompoundButton(
    value = value,
    onChange = onChange,
    createView = { Switch(+AndroidContextAmbient) },
    viewType = Switch::class,
    key = key
)

fun <V : CompoundButton> BuildContext.CompoundButton(
    value: Boolean,
    onChange: (Boolean) -> Unit,
    createView: CreateView<V>,
    viewType: KClass<V>,
    key: Any? = null
): Widget = Clickable(
    child = ViewWidget(
        viewType = viewType,
        key = key,
        createView = createView,
        updateView = { it.isChecked = value }
    ),
    onClick = { onChange(!value) }
)