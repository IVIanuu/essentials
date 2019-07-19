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

import android.view.ViewGroup
import android.widget.RadioButton
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.sample.ui.widget.lib.Widget
import com.ivianuu.kommon.core.view.inflate

open class RadioButton(
    val value: Boolean,
    val onChange: (Boolean) -> Unit,
    override val key: Any? = null
) : Widget<RadioButton>() {

    init {
        state(value)
    }

    override fun createView(container: ViewGroup) =
        container.inflate<RadioButton>(R.layout.es_list_action_radio_button)

    override fun bind(view: RadioButton) {
        super.bind(view)
        view.isChecked = value
        view.setOnClickListener { onChange(!value) }
    }

}