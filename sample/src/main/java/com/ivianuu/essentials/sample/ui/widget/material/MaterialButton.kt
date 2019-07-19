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
import com.google.android.material.button.MaterialButton
import com.ivianuu.essentials.sample.ui.widget.lib.Widget

class MaterialButton(
    override val key: Any? = null,
    val text: String? = null,
    val textRes: Int? = null,
    val onClick: (() -> Unit)? = null
) : Widget<MaterialButton>() {

    override fun bind(view: MaterialButton) {
        super.bind(view)
        when {
            text != null -> view.text = text
            textRes != null -> view.setText(textRes)
            else -> view.text = null
        }
        if (onClick != null) {
            view.setOnClickListener { onClick!!() }
            view.isEnabled = true
        } else {
            view.setOnClickListener(null)
            view.isEnabled = false
        }
    }

    override fun createView(container: ViewGroup) = MaterialButton(container.context)
}