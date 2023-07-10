/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.clipboard

import android.content.ClipData
import com.ivianuu.essentials.SystemService
import com.ivianuu.essentials.result.catch
import com.ivianuu.essentials.result.fold
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import android.content.ClipboardManager as AndroidClipboardManager

interface ClipboardRepository {
  val clipboardText: Flow<String?>

  suspend fun updateClipboardText(value: String, showMessage: Boolean = true)
}

@Provide class ClipboardRepositoryImpl(
  private val androidClipboardManager: @SystemService AndroidClipboardManager,
  private val toaster: Toaster
) : ClipboardRepository {
  override val clipboardText: Flow<String?> = callbackFlow {
    val listener = AndroidClipboardManager.OnPrimaryClipChangedListener {
      val current = androidClipboardManager.primaryClip?.getItemAt(0)?.text?.toString()
      trySend(current)
    }
    listener.onPrimaryClipChanged()

    androidClipboardManager.addPrimaryClipChangedListener(listener)
    awaitClose { androidClipboardManager.removePrimaryClipChangedListener(listener) }
  }

  override suspend fun updateClipboardText(value: String, showMessage: Boolean) {
    catch { androidClipboardManager.setPrimaryClip(ClipData.newPlainText("", value)) }
      .also { result ->
        if (showMessage) {
          toaster(result.fold({ R.string.copied_to_clipboard }, { R.string.copy_to_clipboard_failed }))
        }
      }
  }
}
