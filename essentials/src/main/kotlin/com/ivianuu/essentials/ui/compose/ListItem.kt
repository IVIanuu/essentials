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

package com.ivianuu.essentials.ui.compose

import android.view.View
import androidx.core.view.isVisible
import com.ivianuu.compose.ComponentComposition
import com.ivianuu.compose.View
import com.ivianuu.compose.byId
import com.ivianuu.compose.layoutRes
import com.ivianuu.essentials.R
import kotlinx.android.synthetic.main.es_list_item.view.*

fun ComponentComposition.ListItem(
    title: String? = null,
    titleRes: Int? = null,

    text: String? = null,
    textRes: Int? = null,

    leadingAction: (ComponentComposition.() -> Unit)? = null,
    trailingAction: (ComponentComposition.() -> Unit)? = null,

    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,

    enabled: Boolean = true
) {
    View<View> {
        layoutRes(R.layout.es_list_item)

        bindView {
            when {
                title != null -> {
                    es_list_title.text = title
                    es_list_title.isVisible = true
                }
                titleRes != null -> {
                    es_list_title.setText(titleRes)
                    es_list_title.isVisible = true
                }
                else -> {
                    es_list_title.text = null
                    es_list_title.isVisible = false
                }
            }
            es_list_title.isEnabled = enabled

            when {
                text != null -> {
                    es_list_text.text = text
                    es_list_text.isVisible = true
                }
                textRes != null -> {
                    es_list_text.setText(textRes)
                    es_list_text.isVisible = true
                }
                else -> {
                    es_list_text.text = null
                    es_list_text.isVisible = false
                }
            }
            es_list_text.isEnabled = enabled

            if (es_list_leading_action != null) {
                es_list_leading_action.isEnabled = enabled
                (0 until es_list_leading_action.childCount)
                    .map { es_list_leading_action.getChildAt(it) }
                    .forEach { it.isEnabled = enabled }
            }

            if (es_list_trailing_action != null) {
                es_list_trailing_action.isEnabled = enabled
                (0 until es_list_trailing_action.childCount)
                    .map { es_list_trailing_action.getChildAt(it) }
                    .forEach { it.isEnabled = enabled }
            }

            if (onClick != null) {
                setOnClickListener { onClick() }
            } else {
                setOnClickListener(null)
            }

            if (onLongClick != null) {
                setOnLongClickListener { onLongClick(); true }
            } else {
                setOnLongClickListener(null)
            }

            isEnabled = enabled && (onClick != null || onLongClick != null)
        }

        if (leadingAction != null) {
            View<View> {
                byId(R.id.es_list_leading_action)
                leadingAction()
            }
        }

        if (trailingAction != null) {
            View<View> {
                byId(R.id.es_list_trailing_action)
                trailingAction()
            }
        }
    }
}