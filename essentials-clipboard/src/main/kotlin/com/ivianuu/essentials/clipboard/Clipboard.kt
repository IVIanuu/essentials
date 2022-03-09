/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.clipboard

import android.content.*
import com.github.michaelbull.result.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.android.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*

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
    runCatching {
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
