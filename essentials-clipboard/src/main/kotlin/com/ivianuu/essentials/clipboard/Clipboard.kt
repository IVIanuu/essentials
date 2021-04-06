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
import com.ivianuu.essentials.clipboard.ClipboardAction.UpdateClipboard
import com.ivianuu.essentials.store.StoreBuilder
import com.ivianuu.essentials.store.cancellableUpdates
import com.ivianuu.essentials.store.onAction
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.scope.AppGivenScope

data class ClipboardState(val text: String? = null)

sealed class ClipboardAction {
    data class UpdateClipboard(val value: String) : ClipboardAction()
}

@Given
fun clipboardStore(
    @Given clipboardManager: ClipboardManager
): StoreBuilder<AppGivenScope, ClipboardState, ClipboardAction> = {
    cancellableUpdates { update ->
        val listener = ClipboardManager.OnPrimaryClipChangedListener {
            val current = clipboardManager.primaryClip?.getItemAt(0)?.text?.toString()
            update { copy(text = current) }
        }
        onCancel { clipboardManager.removePrimaryClipChangedListener(listener) }
    }

    onAction<UpdateClipboard> {
        clipboardManager.setPrimaryClip(ClipData.newPlainText("", it.value))
    }
}
