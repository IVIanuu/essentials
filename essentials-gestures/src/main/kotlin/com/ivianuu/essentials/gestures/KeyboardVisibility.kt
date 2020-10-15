/*
 * Copyright 2020 Manuel Wrage
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

package com.ivianuu.essentials.gestures

import android.view.accessibility.AccessibilityEvent
import android.view.inputmethod.InputMethodManager
import com.ivianuu.essentials.accessibility.AccessibilityConfig
import com.ivianuu.essentials.accessibility.AccessibilityServices
import com.ivianuu.injekt.FunBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.transformLatest

@FunBinding
fun keyboardVisible(
    accessibilityServices: AccessibilityServices,
    getKeyboardHeight: getKeyboardHeight,
): Flow<Boolean> {
    return accessibilityServices.events
        .onEach {
            accessibilityServices.applyConfig(
                AccessibilityConfig(
                    eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
                )
            )
        }
        .filter {
            it.isFullScreen &&
                    it.className == "android.inputmethodservice.SoftInputWindow"
        }
        .map { Unit }
        .onStart { emit(Unit) }
        .transformLatest {
            while (true) {
                emit(Unit)
                delay(100)
            }
        }
        .mapNotNull { getKeyboardHeight() }
        .map { it > 0 }
        .distinctUntilChanged()
}

@FunBinding
fun getKeyboardHeight(inputMethodManager: InputMethodManager): Int? {
    return try {
        val method = inputMethodManager.javaClass.getMethod("getInputMethodWindowVisibleHeight")
        method.invoke(inputMethodManager) as Int
    } catch (t: Throwable) {
        null
    }
}
