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
 * Simple helper class for screens with a undo logic
 */
class UndoHelper<T>(private val callback: Callback<T>) {

    val pendingActionItems
        get() = _pendingActionItems.toList()
    private val _pendingActionItems = mutableListOf<T>()

    var pendingAction = ACTION_NONE
        private set

    fun hasPendingActions() = pendingAction != ACTION_NONE

    fun isPendingAction(item: T) =
        _pendingActionItems.contains(item)

    fun enqueueAction(action: Int, items: List<T> = emptyList()) {
        commitPendingAction()
        pendingAction = action
        _pendingActionItems.addAll(items)
    }

    fun commitPendingAction() = if (pendingAction != ACTION_NONE) {
        callback.commitAction(pendingAction, _pendingActionItems)
        _pendingActionItems.clear()
        pendingAction = ACTION_NONE
        true
    } else {
        false
    }

    fun undoPendingAction() = if (pendingAction != ACTION_NONE) {
        _pendingActionItems.clear()
        pendingAction = ACTION_NONE
        true
    } else {
        false
    }

    interface Callback<T> {
        fun commitAction(action: Int, items: List<T>)
    }

    companion object {
        const val ACTION_NONE = -1
        const val ACTION_DELETE = 0
    }
}