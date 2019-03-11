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

package com.ivianuu.essentials.ui.list.internal

import android.os.Bundle
import android.os.Parcelable
import android.util.SparseArray
import android.view.View
import com.ivianuu.essentials.R
import com.ivianuu.essentials.ui.list.ListViewHolder
import com.ivianuu.essentials.ui.list.requireModel
import kotlinx.android.parcel.Parcelize

internal class ViewStateManager {

    private val viewStates = mutableMapOf<Long, ViewState>()

    fun restore(holder: ListViewHolder) {
        if (!holder.requireModel().shouldSaveViewState) return
        val state = viewStates[holder.itemId]

        if (state != null) {
            state.restore(holder.itemView)
        } else {
            holder.restoreInitialViewState()
        }
    }

    fun save(holder: ListViewHolder) {
        if (!holder.requireModel().shouldSaveViewState) return

        val state = viewStates.getOrPut(holder.itemId) { ViewState() }
        state.save(holder.itemView)
    }

    fun restoreFromBundle(bundle: Bundle) {
        viewStates.putAll(
            bundle.getParcelableArrayList<ViewStateWithId>(KEY_HOLDER_STATES)!!
                .associateBy(ViewStateWithId::id)
                .mapValues { it.value.state }
        )
    }

    fun saveToBundle(): Bundle = Bundle().apply {
        putParcelableArrayList(
            KEY_HOLDER_STATES,
            ArrayList(
                viewStates
                    .map { ViewStateWithId(it.key, it.value) }
            )
        )
    }

    @Parcelize
    private class ViewStateWithId(
        val id: Long,
        val state: ViewState
    ) : Parcelable

    @Parcelize
    class ViewState(
        private val viewHierarchyState: SparseArray<Parcelable> = SparseArray()
    ) : Parcelable {

        fun restore(view: View) {
            val originalId = view.id
            view.ensureValidId()
            view.restoreHierarchyState(viewHierarchyState)
            view.id = originalId
        }

        fun save(view: View) {
            val originalId = view.id
            view.ensureValidId()
            view.saveHierarchyState(viewHierarchyState)
            view.id = originalId
        }

        private fun View.ensureValidId() {
            if (id == -1) {
                id = R.id.state_saving_id
            }
        }

    }

    private companion object {
        private const val KEY_HOLDER_STATES = "ViewStateManager.viewStates"
    }
}