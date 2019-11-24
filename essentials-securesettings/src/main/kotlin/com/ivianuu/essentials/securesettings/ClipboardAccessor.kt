/*
 * Copyright 2019 Manuel Wrage
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

package com.ivianuu.essentials.securesettings

import android.content.ClipboardManager
import com.ivianuu.injekt.Factory

/**
 * Allows to access the clipboard
 */
@Factory
internal class ClipboardAccessor(private val clipboardManager: ClipboardManager) {
    var clipboardText: String
        get() = clipboardManager.text.toString()
        set(value) {
            clipboardManager.text = value
        }
}