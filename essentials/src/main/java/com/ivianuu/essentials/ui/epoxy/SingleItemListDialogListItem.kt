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
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.airbnb.epoxy.EpoxyController
import com.ivianuu.essentials.R

fun EpoxyController.SingleItemListDialogListItem(
    id: Any?,

    value: String,
    onSelected: (String) -> Unit,

    title: String? = null,
    titleRes: Int? = null,

    text: String? = null,
    textRes: Int? = null,

    icon: Drawable? = null,
    iconRes: Int? = null,

    avatar: Drawable? = null,
    avatarRes: Int? = null,

    dialogTitle: String? = title,
    dialogTitleRes: Int? = titleRes,

    negativeDialogButtonText: String? = null,
    negativeDialogButtonTextRes: Int = R.string.es_cancel,

    entries: Array<String>? = null,
    entriesRes: Int? = null,
    entryValues: Array<String>? = null,
    entryValuesRes: Int? = null,

    dialogBlock: (MaterialDialog.() -> Unit)? = null,
    builderBlock: (FunModelBuilder.() -> Unit)? = null
) = DialogListItem(
    id = id,
    buildDialog = {
        var finalEntries = entries
        if (finalEntries == null && entriesRes != null) {
            finalEntries = context.resources.getStringArray(entriesRes)
        }
        if (finalEntries == null) {
            finalEntries = emptyArray()
        }

        var finalEntryValues = entryValues
        if (finalEntryValues == null && entryValuesRes != null) {
            finalEntryValues = context.resources.getStringArray(entryValuesRes)
        }
        if (finalEntryValues == null) {
            finalEntryValues = emptyArray()
        }

        val selectedIndex = finalEntryValues.indexOf(value)

        title(res = dialogTitleRes, text = dialogTitle)
        negativeButton(res = negativeDialogButtonTextRes, text = negativeDialogButtonText)

        listItemsSingleChoice(
            initialSelection = selectedIndex,
            items = finalEntries.toList(),
            waitForPositiveButton = false
        ) { dialog, position, _ ->
            val newValue = finalEntryValues.toList()[position]
            onSelected(newValue)
            dialog.dismiss()
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
        state(value)
        state(dialogTitle, dialogTitleRes)
        state(negativeDialogButtonText, negativeDialogButtonTextRes)
        state(entries, entriesRes)
        state(entryValues, entryValuesRes)
        state(dialogBlock != null)

        builderBlock?.invoke(this)
    }
)