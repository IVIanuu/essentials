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

package com.ivianuu.essentials.sample.ui.widget

import android.view.ViewGroup
import android.widget.CheckBox
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.sample.ui.widget.lib.Widget
import com.ivianuu.kommon.core.view.inflate

class Checkbox(
    override val id: Any?,
    private val value: Boolean,
    private val onChange: (Boolean) -> Unit
) : Widget<CheckBox>() {

    override val viewId: Int
        get() = R.id.es_list_widget_checkbox

    init {
        state(value)
    }

    override fun createView(container: ViewGroup) =
        container.inflate<CheckBox>(R.layout.es_list_action_checkbox)

    override fun bind(view: CheckBox) {
        super.bind(view)
        view.isChecked = value
        view.setOnClickListener { onChange(!value) }
    }

    override fun unbind(view: CheckBox) {
        super.unbind(view)
        view.setOnCheckedChangeListener(null)
    }

}