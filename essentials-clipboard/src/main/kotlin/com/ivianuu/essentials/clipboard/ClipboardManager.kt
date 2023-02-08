/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.clipboard

import android.content.ClipData
import com.ivianuu.essentials.Err
import com.ivianuu.essentials.Ok
import com.ivianuu.essentials.Resources
import com.ivianuu.essentials.catch
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.essentials.util.invoke
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.android.SystemService
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import android.content.ClipboardManager as AndroidClipboardManager

interface ClipboardManager {
  val clipboardText: Flow<String?>

  suspend fun updateClipboardText(value: String, showMessage: Boolean = true)
}

@Provide class ClipboardManagerImpl(
  private val androidClipboardManager: @SystemService AndroidClipboardManager,
  private val resources: Resources,
  private val toaster: Toaster
) : ClipboardManager {
  override val clipboardText: Flow<String?>
    get() = callbackFlow<String?> {
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
          toaster(
            when (result) {
              is Ok -> R.string.copied_to_clipboard
              is Err -> R.string.copy_to_clipboard_failed
            }
          )
        }
      }
  }
}
