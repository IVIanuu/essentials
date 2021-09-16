/*
 * Copyright 2021 Manuel Wrage
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
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.essentials.catch
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.essentials.util.showToast
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.android.SystemService
import com.ivianuu.injekt.coroutines.NamedCoroutineScope
import com.ivianuu.injekt.scope.AppScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.shareIn

typealias ClipboardText = String?

@Provide fun clipboardText(
  clipboardManager: @SystemService ClipboardManager,
  scope: NamedCoroutineScope<AppScope>
) = callbackFlow<ClipboardText> {
  val listener = ClipboardManager.OnPrimaryClipChangedListener {
    val current = clipboardManager.primaryClip?.getItemAt(0)?.text?.toString()
    trySend(current)
  }
  awaitClose { clipboardManager.removePrimaryClipChangedListener(listener) }
}.shareIn(scope, SharingStarted.WhileSubscribed(), 1)

typealias UpdateClipboardTextUseCase = suspend (String, Boolean) -> Result<Unit, Throwable>

@Provide fun updateClipboardTextUseCase(
  clipboardManager: @SystemService ClipboardManager,
  rp: ResourceProvider,
  toaster: Toaster
): UpdateClipboardTextUseCase = { value, showMessage ->
  catch {
    clipboardManager.setPrimaryClip(ClipData.newPlainText("", value))
  }
    .also { result ->
      if (showMessage) {
        showToast(
          when (result) {
            is Ok -> R.string.copied_to_clipboard
            is Err -> R.string.copy_to_clipboard_failed
          }
        )
      }
    }
}
