/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.util

import android.content.ClipData
import arrow.core.Either
import com.ivianuu.essentials.SystemService
import com.ivianuu.essentials.android.R
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import android.content.ClipboardManager as AndroidClipboardManager

@Provide class ClipboardRepository(
  private val androidClipboardManager: @SystemService AndroidClipboardManager,
  private val toaster: Toaster
) {
  val clipboardText: Flow<String?> = callbackFlow {
    val listener = AndroidClipboardManager.OnPrimaryClipChangedListener {
      val current = androidClipboardManager.primaryClip?.getItemAt(0)?.text?.toString()
      trySend(current)
    }
    listener.onPrimaryClipChanged()

    androidClipboardManager.addPrimaryClipChangedListener(listener)
    awaitClose { androidClipboardManager.removePrimaryClipChangedListener(listener) }
  }

  suspend fun updateClipboardText(value: String, showMessage: Boolean) {
    Either.catch { androidClipboardManager.setPrimaryClip(ClipData.newPlainText("", value)) }
      .also { result ->
        if (showMessage)
          toaster(result.fold({ R.string.copy_to_clipboard_failed }, { R.string.copied_to_clipboard }))
      }
  }
}