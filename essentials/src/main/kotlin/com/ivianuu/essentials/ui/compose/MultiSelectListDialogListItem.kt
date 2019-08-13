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

/**
import android.graphics.drawable.Drawable
import com.afollestad.materialdialogs.MaterialDialog
import com.airbnb.epoxy.EpoxyController
import com.ivianuu.essentials.ui.dialog.pop
import com.ivianuu.essentials.ui.effect.state
import com.ivianuu.kprefs.Pref

fun ComponentComposition.MultiSelectListDialogListItem(
id: Any?,

values: Set<String>,
onSelected: (Set<String>) -> Unit,

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

positiveDialogButtonText: String? = null,
positiveDialogButtonTextRes: Int = R.string.es_ok,
negativeDialogButtonText: String? = null,
negativeDialogButtonTextRes: Int = R.string.es_cancel,

entries: Array<String>? = null,
entriesRes: Int? = null,
entryValues: Array<String>? = null,
entryValuesRes: Int? = null,

enabled: Boolean = true,

dialogBlock: (MaterialDialog.() -> Unit)? = null,
builderBlock: (FunModelBuilder.() -> Unit)? = null
) = DialogListItem<Set<String>>(
id = id,
buildDialog = { context ->
var finalEntries = entries
if (finalEntries == null && entriesRes != null) {
finalEntries = context.controller.stringArray(entriesRes)
}
if (finalEntries == null) {
finalEntries = emptyArray()
}

var finalEntryValues = entryValues
if (finalEntryValues == null && entryValuesRes != null) {
finalEntryValues = context.controller.stringArray(entryValuesRes)
}
if (finalEntryValues == null) {
finalEntryValues = emptyArray()
}

var currentValue by context.state { values }

title(res = dialogTitleRes, text = dialogTitle)
positiveButton(res = positiveDialogButtonTextRes, text = positiveDialogButtonText) {
context.pop(currentValue)
}
negativeButton(res = negativeDialogButtonTextRes, text = negativeDialogButtonText)

val selectedIndices = currentValue
.map { finalEntryValues.indexOf(it) }
.filter { it != -1 }
.toIntArray()

listItemsMultiChoice(
items = finalEntries.toList(),
initialSelection = selectedIndices,
allowEmptySelection = true,
waitForPositiveButton = false
) { _, positions, _ ->
currentValue = finalEntryValues.toList()
.filterIndexed { index, _ -> index in positions }
.map { it }
.toSet()
}

dialogBlock?.invoke(this)

show()
},
onDialogResult = { onSelected(it) },
title = title,
titleRes = titleRes,
text = text,
textRes = textRes,
icon = icon,
iconRes = iconRes,
avatar = avatar,
avatarRes = avatarRes,
enabled = enabled,
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

fun ComponentComposition.MultiSelectListDialogListItem(
pref: Pref<Set<String>>,

id: Any? = pref.key,

onSelectedPredicate: ((Set<String>) -> Boolean)? = null,

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

positiveDialogButtonText: String? = null,
positiveDialogButtonTextRes: Int = R.string.es_ok,
negativeDialogButtonText: String? = null,
negativeDialogButtonTextRes: Int = R.string.es_cancel,

entries: Array<String>? = null,
entriesRes: Int? = null,
entryValues: Array<String>? = null,
entryValuesRes: Int? = null,

enabled: Boolean = true,

dialogBlock: (MaterialDialog.() -> Unit)? = null,
builderBlock: (FunModelBuilder.() -> Unit)? = null
) = MultiSelectListDialogListItem(
id = id,
values = pref.get(),
onSelected = {
if (onSelectedPredicate == null || onSelectedPredicate(it)) {
pref.set(it)
}
},
title = title,
titleRes = titleRes,
text = text,
textRes = textRes,
icon = icon,
iconRes = iconRes,
avatar = avatar,
avatarRes = avatarRes,
dialogTitle = dialogTitle,
dialogTitleRes = dialogTitleRes,
positiveDialogButtonText = positiveDialogButtonText,
positiveDialogButtonTextRes = positiveDialogButtonTextRes,
negativeDialogButtonText = negativeDialogButtonText,
negativeDialogButtonTextRes = negativeDialogButtonTextRes,
entries = entries,
entriesRes = entriesRes,
entryValues = entryValues,
entryValuesRes = entryValuesRes,
enabled = enabled,
dialogBlock = dialogBlock,
builderBlock = builderBlock
)*/