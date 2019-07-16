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

package com.ivianuu.essentials.ui.epoxy

import android.graphics.drawable.Drawable
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItemsMultiChoice
import com.airbnb.epoxy.EpoxyController
import com.ivianuu.essentials.R

fun EpoxyController.MultiSelectListDialogListItem(
    id: Any?,

    values: Set<String>,
    onSelected: (Set<String>) -> Unit,

    title: String? = null,
    titleRes: Int = 0,

    text: String? = null,
    textRes: Int = 0,

    icon: Drawable? = null,
    iconRes: Int = 0,

    avatar: Drawable? = null,
    avatarRes: Int = 0,

    dialogTitle: String? = title,
    dialogTitleRes: Int = titleRes,

    positiveDialogButtonText: String? = null,
    positiveDialogButtonTextRes: Int = R.string.es_ok,
    negativeDialogButtonText: String? = null,
    negativeDialogButtonTextRes: Int = R.string.es_cancel,

    entries: Array<String>? = null,
    entriesRes: Int = 0,
    entryValues: Array<String>? = null,
    entryValuesRes: Int = 0,

    dialogBlock: (MaterialDialog.() -> Unit)? = null,
    builderBlock: (FunModelBuilder.() -> Unit)? = null
) = DialogListItem(
    id = id,
    buildDialog = {
        var finalEntries = entries
        if (finalEntries == null && entriesRes != 0) {
            finalEntries = context.resources.getStringArray(entriesRes)
        }
        if (finalEntries == null) {
            finalEntries = emptyArray()
        }

        var finalEntryValues = entryValues
        if (finalEntryValues == null && entryValuesRes != 0) {
            finalEntryValues = context.resources.getStringArray(entryValuesRes)
        }
        if (finalEntryValues == null) {
            finalEntryValues = emptyArray()
        }

        title(res = dialogTitleRes, text = dialogTitle)
        positiveButton(res = positiveDialogButtonTextRes, text = positiveDialogButtonText)
        negativeButton(res = negativeDialogButtonTextRes, text = negativeDialogButtonText)

        val selectedIndices = values
            .map { finalEntryValues.indexOf(it) }
            .filter { it != -1 }
            .toIntArray()

        listItemsMultiChoice(
            items = finalEntries.toList(),
            initialSelection = selectedIndices,
            allowEmptySelection = true
        ) { _, positions, _ ->
            val newValue = finalEntryValues.toList()
                .filterIndexed { index, _ -> positions.contains(index) }
                .map { it }
                .toSet()
            onSelected(newValue)
        }

        dialogBlock?.invoke(this)

        show()
    },
    title = title,
    titleRes = titleRes,
    text = text,
    textRes = textRes,
    icon = icon,
    iconRes = iconRes,
    avatar = avatar,
    avatarRes = avatarRes,
    builderBlock = {
        state(values)
        state(dialogTitle, dialogTitleRes)
        state(negativeDialogButtonText, negativeDialogButtonTextRes)
        state(entries, entriesRes)
        state(entryValues, entryValuesRes)
        state(dialogBlock != null)

        builderBlock?.invoke(this)
    }
)