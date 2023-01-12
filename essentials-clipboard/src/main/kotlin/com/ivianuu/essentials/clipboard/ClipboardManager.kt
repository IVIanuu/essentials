/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.clipboard

import android.content.ClipboardManager as AndroidClipboardManager
import android.content.ClipData
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

interface ClipboardManager {
  val clipboardText: Flow<String?>

  suspend fun updateClipboardText(value: String, showMessage: Boolean = true)
}

context(AndroidClipboardManager, ToastContext)
@Provide class ClipboardManagerImpl : ClipboardManager {
  override val clipboardText: Flow<String?>
    get() = callbackFlow<String?> {
      val listener = AndroidClipboardManager.OnPrimaryClipChangedListener {
        val current = primaryClip?.getItemAt(0)?.text?.toString()
        trySend(current)
      }
      listener.onPrimaryClipChanged()

      addPrimaryClipChangedListener(listener)
      awaitClose { removePrimaryClipChangedListener(listener) }
    }

  override suspend fun updateClipboardText(value: String, showMessage: Boolean) {
    catch { setPrimaryClip(ClipData.newPlainText("", value)) }
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
