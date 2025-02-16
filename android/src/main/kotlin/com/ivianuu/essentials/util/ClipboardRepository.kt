/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.util

import android.content.*
import androidx.compose.runtime.*
import com.ivianuu.essentials.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*
import android.content.ClipboardManager as AndroidClipboardManager

@Stable @Provide class ClipboardRepository(
  private val androidClipboardManager: @SystemService AndroidClipboardManager
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

  suspend fun updateClipboardText(value: String) {
    catch { androidClipboardManager.setPrimaryClip(ClipData.newPlainText("", value)) }
  }
}
