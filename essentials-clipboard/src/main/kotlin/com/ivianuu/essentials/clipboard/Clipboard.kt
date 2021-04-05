/*
 * Copyright 2020 Manuel Wrage
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

package com.ivianuu.essentials.clipboard

import android.content.ClipData
import android.content.ClipboardManager
import com.ivianuu.essentials.coroutines.updateIn
import com.ivianuu.essentials.store.ScopeStateStore
import com.ivianuu.essentials.store.State
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.scope.AppGivenScope
import com.ivianuu.injekt.scope.Scoped
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map

data class ClipboardState(val text: String? = null) : State()

@Scoped<AppGivenScope>
@Given
class Clipboard(
    @Given private val clipboardManager: ClipboardManager,
    @Given private val store: ScopeStateStore<AppGivenScope, ClipboardState>
) : StateFlow<ClipboardState> by store {
    init {
        clipboardManager.clipboardChanges()
            .map { clipboardManager.primaryClip?.getItemAt(0)?.text?.toString() }
            .updateIn(store) { copy(text = it) }
    }

    fun updateClipboard(value: String) = store.effect {
        clipboardManager.setPrimaryClip(ClipData.newPlainText("", value))
    }

    private fun ClipboardManager.clipboardChanges() = callbackFlow {
        val listener = ClipboardManager.OnPrimaryClipChangedListener { offer(Unit) }
        addPrimaryClipChangedListener(listener)
        awaitClose { removePrimaryClipChangedListener(listener) }
    }
}
