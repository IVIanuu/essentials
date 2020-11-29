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
import com.ivianuu.essentials.coroutines.GlobalScope
import com.ivianuu.essentials.store.Actions
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.ui.store.GlobalStateBinding
import com.ivianuu.essentials.ui.store.Initial
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

@GlobalStateBinding
fun clipboardState(
    scope: GlobalScope,
    initial: @Initial ClipboardState = ClipboardState(),
    actions: Actions<ClipboardAction>,
    clipboardManager: ClipboardManager,
) = scope.state(initial) {
    clipboardManager.clipboardChanges()
        .map { clipboardManager.primaryClip?.getItemAt(0)?.text?.toString() }
        .reduce { copy(text = it) }
        .launchIn(this)

    actions
        .filterIsInstance<ClipboardAction.UpdateClipboard>()
        .map { ClipData.newPlainText("", it.value) }
        .onEach { clipboardManager.setPrimaryClip(it) }
        .launchIn(this)
}

private fun ClipboardManager.clipboardChanges() = callbackFlow {
    val listener = ClipboardManager.OnPrimaryClipChangedListener { offer(Unit) }
    addPrimaryClipChangedListener(listener)
    awaitClose { removePrimaryClipChangedListener(listener) }
}
