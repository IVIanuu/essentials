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

package com.ivianuu.essentials.sample.ui.widget2

import android.widget.CheckBox
import com.google.android.material.checkbox.MaterialCheckBox
import com.ivianuu.essentials.sample.ui.widget2.exp.AndroidContextAmbient
import com.ivianuu.essentials.sample.ui.widget2.lib.BuildContext
import com.ivianuu.essentials.sample.ui.widget2.lib.ViewWidget

open class Checkbox(
    val value: Boolean,
    val onChange: (Boolean) -> Unit,
    key: Any? = null
) : ViewWidget<CheckBox>(key) {

    override fun createView(context: BuildContext): CheckBox =
        MaterialCheckBox(AndroidContextAmbient(context))

    override fun updateView(context: BuildContext, view: CheckBox) {
        super.updateView(context, view)
        view.isChecked = value
        view.setOnClickListener { onChange(!value) }
    }

}