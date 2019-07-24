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

package com.ivianuu.essentials.sample.ui.widget.sample

import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.sample.ui.widget.layout.IdViewGroupWidget
import com.ivianuu.essentials.sample.ui.widget.layout.InflateViewGroupWidget
import com.ivianuu.essentials.sample.ui.widget.lib.BuildContext
import com.ivianuu.essentials.util.andTrue

fun ListItem(
    title: String? = null,
    titleRes: Int? = null,

    text: String? = null,
    textRes: Int? = null,

    primaryAction: (BuildContext.() -> Unit)? = null,
    secondaryAction: (BuildContext.() -> Unit)? = null,

    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,

    key: Any? = null
) = InflateViewGroupWidget<ConstraintLayout>(
    layoutRes = R.layout.es_list_item,
    key = key,
    updateView = { view ->
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
    },
    children = {
        if (primaryAction != null) {
            +IdViewGroupWidget<ViewGroup>(
                R.id.es_list_primary_action_container,
                children = primaryAction
            )
        }
        if (secondaryAction != null) {
            +IdViewGroupWidget<ViewGroup>(
                R.id.es_list_secondary_action_container,
                children = secondaryAction
            )
        }
    }
)