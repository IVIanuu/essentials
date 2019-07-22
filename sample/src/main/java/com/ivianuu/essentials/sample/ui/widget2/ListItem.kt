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

import android.view.ViewGroup
import android.widget.TextView
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.sample.ui.widget2.lib.ContainerAmbient
import com.ivianuu.essentials.sample.ui.widget2.lib.BuildContext
import com.ivianuu.essentials.sample.ui.widget2.lib.IdViewGroupWidget
import com.ivianuu.essentials.sample.ui.widget2.lib.ViewGroupWidget
import com.ivianuu.essentials.sample.ui.widget2.lib.Widget
import com.ivianuu.essentials.util.andTrue
import com.ivianuu.kommon.core.view.inflate

open class ListItem(
    val title: String? = null,
    val titleRes: Int? = null,

    val text: String? = null,
    val textRes: Int? = null,

    val primaryAction: Widget? = null,
    val secondaryAction: Widget? = null,

    val onClick: (() -> Unit)? = null,
    val onLongClick: (() -> Unit)? = null,

    key: Any? = null
) : ViewGroupWidget<ViewGroup>(
    listOf(
        IdViewGroupWidget<ViewGroup>(
            id = R.id.es_list_primary_action_container,
            children = listOfNotNull(primaryAction)
        ),
        IdViewGroupWidget<ViewGroup>(
            id = R.id.es_list_secondary_action_container,
            children = listOfNotNull(secondaryAction)
        )
    ),
    key
) {

    override fun createView(context: BuildContext): ViewGroup =
        ContainerAmbient(context).inflate<ViewGroup>(R.layout.es_list_item)

    override fun updateView(context: BuildContext, view: ViewGroup) {
        super.updateView(context, view)
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
}