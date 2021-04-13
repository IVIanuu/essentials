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
import com.ivianuu.essentials.coroutines.ScopeCoroutineScope
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.android.SystemService
import com.ivianuu.injekt.scope.AppGivenScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.shareIn

@Given
class Clipboard(
    @Given private val scope: ScopeCoroutineScope<AppGivenScope>,
    @Given private val clipboardManager: @SystemService ClipboardManager
) {
    val clipboardText = callbackFlow {
        val listener = ClipboardManager.OnPrimaryClipChangedListener {
            val current = clipboardManager.primaryClip?.getItemAt(0)?.text?.toString()
            offer(current)
        }
        awaitClose { clipboardManager.removePrimaryClipChangedListener(listener) }
    }.shareIn(scope, SharingStarted.Lazily, 1)

    fun updateClipboardText(value: String) {
        clipboardManager.setPrimaryClip(ClipData.newPlainText("", value))
    }
}
