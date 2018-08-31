/*
 * Copyright 2018 Manuel Wrage
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

package com.ivianuu.essentials.util

/**
 * Simple helper class for multi selection screens
 */
class MultiSelectionHelper<T>(private val onChanged: (selectedItems: List<T>) -> Unit) {

    val selectedItems: List<T>
        get() = _selectedItems
    private val _selectedItems = mutableListOf<T>()

    fun setItemSelected(item: T, selected: Boolean) {
        if (selected) {
            _selectedItems.add(item)
        } else {
            _selectedItems.remove(item)
        }
        onChanged.invoke(selectedItems)
    }

    fun setItemsSelected(items: Collection<T>, selected: Boolean) {
        if (selected) {
            _selectedItems.addAll(items)
        } else {
            _selectedItems.removeAll(items)
        }
        onChanged.invoke(selectedItems)
    }

    fun deselectAll() {
        _selectedItems.clear()
        onChanged.invoke(selectedItems)
    }

    fun isSelected(item: T) = selectedItems.contains(item)

    fun hasSelections() = selectedItems.isNotEmpty()
}