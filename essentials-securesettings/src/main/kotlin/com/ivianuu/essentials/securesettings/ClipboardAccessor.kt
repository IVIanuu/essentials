package com.ivianuu.essentials.securesettings

import android.content.ClipboardManager
import com.ivianuu.injekt.Inject

/**
 * Allows to access the clipboard
 */
@Inject
internal class ClipboardAccessor(private val clipboardManager: ClipboardManager) {
    var clipboardText: String
        get() = clipboardManager.text.toString()
        set(value) {
            clipboardManager.text = value
        }
}