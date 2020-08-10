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

package com.ivianuu.essentials.ui.common

import androidx.activity.OnBackPressedCallback
import androidx.compose.runtime.Composable
import androidx.compose.runtime.onCommit
import androidx.compose.runtime.remember

@Composable
fun onBackPressed(
    enabled: Boolean = true,
    callback: () -> Unit
) {
    val onBackPressedCallback = remember(callback) {
        object : OnBackPressedCallback(enabled) {
            override fun handleOnBackPressed() {
                callback()
            }
        }
    }
    onCommit(enabled) {
        onBackPressedCallback.isEnabled = enabled
    }

    // todo use OnBackPressedDispatcherOwnerAmbient once available
    val onBackPressedDispatcher = compositionActivity
        .onBackPressedDispatcher
    onCommit(onBackPressedCallback) {
        onBackPressedDispatcher.addCallback(onBackPressedCallback)
        onDispose { onBackPressedCallback.remove() }
    }
}
