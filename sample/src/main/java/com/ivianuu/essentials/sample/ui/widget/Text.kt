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
import android.widget.TextView
import com.ivianuu.essentials.sample.ui.widget.lib.Widget

class Text(
    val text: String? = null,
    val textRes: Int? = null,
    val textAppearance: Int? = null
) : Widget<TextView>() {

    override fun bind(view: TextView) {
        super.bind(view)
        when {
            text != null -> view.text = text
            textRes != null -> view.setText(textRes)
        }

        if (textAppearance != null) view.setTextAppearance(textAppearance)
    }

    override fun createView(container: ViewGroup) = TextView(container.context)

}