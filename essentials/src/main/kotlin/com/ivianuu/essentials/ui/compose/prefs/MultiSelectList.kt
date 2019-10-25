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
import com.afollestad.materialdialogs.list.listItemsMultiChoice
import com.ivianuu.essentials.R
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.kprefs.Pref

@Composable
fun MultiSelectListPreference(
    pref: Pref<Set<String>>,
    entries: List<MultiSelectListPreference.Item>,
    dialogTitle: String? = null,
    dialogMessage: String? = null,
    dialogIcon: Drawable? = null,
    dialogPositiveButtonText: String? = +stringResource(R.string.es_ok),
    dialogNegativeButtonText: String? = +stringResource(R.string.es_cancel),
    title: @Composable() (() -> Unit),
    summary: @Composable() (() -> Unit)? = null,
    singleLineSummary: Boolean = true,
    leading: @Composable() (() -> Unit)? = null,
    onChange: ((Set<String>) -> Boolean)? = null,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null
) = composable("MultiSelectListPreference:${pref.key}") {
    DialogPreference(
        pref = pref,
        dialogTitle = dialogTitle,
        dialogMessage = dialogMessage,
        dialogIcon = dialogIcon,
        dialogPositiveButtonText = dialogPositiveButtonText,
        dialogNegativeButtonText = dialogNegativeButtonText,
        buildDialog = { dismiss ->
            val currentValues = pref.get()

            val selectedIndices = currentValues
                .map { value -> entries.indexOfFirst { it.value == value } }
                .filter { it != -1 }
                .toIntArray()

            listItemsMultiChoice(
                items = entries.map { it.title },
                initialSelection = selectedIndices,
                allowEmptySelection = true
            ) { _, positions, _ ->
                val newValue = entries
                    .filterIndexed { index, _ -> positions.contains(index) }
                    .map { it.value }
                    .toMutableSet()

                if (onChange?.invoke(newValue) != false) {
                    pref.set(newValue)
                }
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

object MultiSelectListPreference {
    data class Item(
        val title: String,
        val value: String
    ) {
        constructor(value: String) : this(value, value)
    }
}