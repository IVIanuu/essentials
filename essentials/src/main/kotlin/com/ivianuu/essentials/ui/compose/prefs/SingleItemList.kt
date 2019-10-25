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

package com.ivianuu.essentials.ui.compose.prefs

import android.graphics.drawable.Drawable
import androidx.compose.Composable
import androidx.compose.unaryPlus
import androidx.ui.res.stringResource
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.ivianuu.essentials.R
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.kprefs.Pref

@Composable
fun SingleItemListPreference(
    pref: Pref<String>,
    entries: List<SingleItemListPreference.Item>,
    dialogTitle: String? = null,
    dialogMessage: String? = null,
    dialogIcon: Drawable? = null,
    dialogNegativeButtonText: String? = +stringResource(R.string.es_cancel),
    title: @Composable() (() -> Unit),
    summary: @Composable() (() -> Unit)? = null,
    singleLineSummary: Boolean = true,
    leading: @Composable() (() -> Unit)? = null,
    onChange: ((String) -> Boolean)? = null,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null
) = composable("SingleItemListPreference:${pref.key}") {
    DialogPreference(
        pref = pref,
        dialogTitle = dialogTitle,
        dialogMessage = dialogMessage,
        dialogIcon = dialogIcon,
        dialogPositiveButtonText = null,
        dialogNegativeButtonText = dialogNegativeButtonText,
        buildDialog = { dismiss ->
            val currentValue = pref.get()
            val selectedIndex = entries.indexOfFirst { it.value == currentValue }

            listItemsSingleChoice(
                initialSelection = selectedIndex,
                items = entries.map { it.title },
                waitForPositiveButton = false
            ) { _, position, _ ->
                val newValue = entries[position].value
                if (onChange?.invoke(newValue) != false) {
                    pref.set(newValue)
                }

                dismiss()
            }
        },
        title = title,
        summary = summary,
        singleLineSummary = singleLineSummary,
        leading = leading,
        onChange = onChange,
        enabled = enabled,
        dependencies = dependencies
    )
}

object SingleItemListPreference {
    data class Item(
        val title: String,
        val value: String
    ) {
        constructor(value: String) : this(value, value)
    }
}