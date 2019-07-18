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
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.sample.ui.widget.lib.BuildContext
import com.ivianuu.essentials.sample.ui.widget.lib.ContainerWidget
import com.ivianuu.essentials.sample.ui.widget.lib.Widget
import com.ivianuu.essentials.util.andTrue
import com.ivianuu.kommon.core.view.inflate

class ListItem(
    override val id: Any?,

    private val title: String? = null,
    private val titleRes: Int? = null,

    private val text: String? = null,
    private val textRes: Int? = null,

    private val primaryAction: Widget<*>? = null,
    private val secondaryAction: Widget<*>? = null,

    private val onClick: (() -> Unit)? = null,
    private val onLongClick: (() -> Unit)? = null
) : ContainerWidget<ViewGroup>() {

    override val viewId: Int
        get() = R.id.es_list_item_container

    init {
        state(title, titleRes)
        state(text, textRes)
        state(onClick != null, onLongClick != null)
    }

    override fun createView(container: ViewGroup): ViewGroup =
        container.inflate<ViewGroup>(R.layout.es_list_item)

    override fun bind(view: ViewGroup) {
        super.bind(view)
        val titleView = view.findViewById<TextView>(R.id.es_list_title)
        when {
            title != null -> titleView.text = title
            titleRes != null -> titleView.setText(titleRes)
            else -> titleView.text = null
        }

        val textView = view.findViewById<TextView>(R.id.es_list_text)
        when {
            text != null -> textView.text = text
            textRes != null -> textView.setText(textRes)
            else -> textView.text = null
        }

        if (onClick != null) {
            view.setOnClickListener { onClick!!() }
        } else {
            view.setOnClickListener(null)
        }

        if (onLongClick != null) {
            view.setOnLongClickListener { onLongClick!!().andTrue() }
        } else {
            view.setOnLongClickListener(null)
        }

        view.isEnabled = onClick != null || onLongClick != null
    }

    override fun BuildContext.children() {
        if (primaryAction != null) emit(primaryAction, R.id.es_list_primary_action_container)
        if (secondaryAction != null) emit(secondaryAction, R.id.es_list_secondary_action_container)
    }
}