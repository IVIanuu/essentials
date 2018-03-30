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

    private val pendingDeletions = mutableSetOf<T>()

    fun isPendingDeletion(item: T) =
        pendingDeletions.contains(item)

    fun enqueueDeletion(items: List<T>) {
        commitPendingDeletions()
        pendingDeletions.addAll(items)
        callback.update(pendingDeletions.toList())
    }

    fun commitPendingDeletions() {
        if (pendingDeletions.isNotEmpty()) {
            val deletions = pendingDeletions.toList()
            callback.commitPendingDeletions(deletions)
            pendingDeletions.clear()
        }
    }

    fun undoPendingDeletions() {
        pendingDeletions.clear()
        callback.update(pendingDeletions.toList())
    }

    interface Callback<T> {
        fun commitPendingDeletions(items: List<T>)
        fun update(pendingDeletions: List<T>)
    }
}