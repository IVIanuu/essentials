/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
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
      listener.onPrimaryClipChanged()

      clipboardManager.addPrimaryClipChangedListener(listener)
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
