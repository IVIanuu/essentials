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
import com.ivianuu.essentials.Err
import com.ivianuu.essentials.Ok
import com.ivianuu.essentials.catch
import com.ivianuu.essentials.util.ToastContext
import com.ivianuu.essentials.util.showToast
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.android.SystemService
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

interface Clipboard {
  val text: Flow<String?>

  suspend fun updateText(value: String, showMessage: Boolean = true)
}

@Provide class ClipboardImpl(
  private val clipboardManager: @SystemService ClipboardManager,
  private val T: ToastContext
) : Clipboard {
  override val text: Flow<String?>
    get() = callbackFlow<String?> {
      val listener = ClipboardManager.OnPrimaryClipChangedListener {
        val current = clipboardManager.primaryClip?.getItemAt(0)?.text?.toString()
        trySend(current)
      }
      awaitClose { clipboardManager.removePrimaryClipChangedListener(listener) }
    }

  override suspend fun updateText(value: String, showMessage: Boolean) {
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
}
