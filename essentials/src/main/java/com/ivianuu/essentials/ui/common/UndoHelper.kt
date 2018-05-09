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

package com.ivianuu.essentials.ui.common

/**
 * Simple helper class for screens with a undo logic
 */
class UndoHelper<T>(private val callback: Callback<T>) {

    val pendingActionItems = mutableSetOf<T>()
    var pendingAction = ACTION_NONE

    fun hasPendingActions() = pendingAction != ACTION_NONE

    fun isPendingAction(item: T) =
        pendingActionItems.contains(item)

    fun enqueueAction(action: Int, items: List<T>) {
        commitPendingAction()
        pendingAction = action
        pendingActionItems.addAll(items)
    }

    fun commitPendingAction() {
        callback.commitAction(pendingAction, pendingActionItems.toSet())
        pendingActionItems.clear()
        pendingAction = ACTION_NONE
    }

    fun undoPendingAction() {
        pendingActionItems.clear()
        pendingAction = ACTION_NONE
    }

    interface Callback<T> {
        fun commitAction(action: Int, items: Set<T>)
    }

    companion object {
        const val ACTION_NONE = -1
    }
}