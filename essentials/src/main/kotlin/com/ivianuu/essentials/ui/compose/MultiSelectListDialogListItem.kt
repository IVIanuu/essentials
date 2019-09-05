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

import com.afollestad.materialdialogs.list.listItemsMultiChoice
import com.ivianuu.compose.ComponentComposition
import com.ivianuu.compose.state
import com.ivianuu.essentials.R
import com.ivianuu.essentials.ui.dialog.Dialog
import com.ivianuu.kprefs.Pref

fun <T> ComponentComposition.MultiSelectListDialog(
    title: String? = null,
    titleRes: Int? = null,
    items: Array<Pair<String, T>>,
    selectedItems: Set<T>,
    onItemsSelected: (Set<T>) -> Unit
) {
    val (currentItems, setCurrentItems) = state { selectedItems.toSet() }

    val selectedIndices = currentItems
        .map { item -> items.indexOfFirst { it.second == item } }
        .toIntArray()

    Dialog {
        title(res = titleRes, text = title)
        positiveButton(res = R.string.es_ok) { onItemsSelected(currentItems) }
        negativeButton(res = R.string.es_cancel)

        listItemsMultiChoice(
            items = items.map { it.first },
            initialSelection = selectedIndices,
            allowEmptySelection = true,
            waitForPositiveButton = false
        ) { _, positions, _ ->
            setCurrentItems(
                positions
                    .map { index -> items[index].second }
                    .toSet()
            )
        }
    }
}

fun <T> ComponentComposition.MultiSelectListDialog(
    title: String? = null,
    titleRes: Int? = null,
    pref: Pref<Set<T>>,
    items: Array<Pair<String, T>>,
    onChangePredicate: ((Set<T>) -> Boolean)? = null
) {
    MultiSelectListDialog(
        title = title,
        titleRes = titleRes,
        items = items,
        selectedItems = pref.get(),
        onItemsSelected = {
            if (onChangePredicate?.invoke(it) ?: true) {
                pref.set(it)
            }
        }
    )
}