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
import com.ivianuu.essentials.accessibility.AccessibilityEvents
import com.ivianuu.essentials.accessibility.applyAccessibilityConfig
import com.ivianuu.essentials.coroutines.GlobalScope
import com.ivianuu.injekt.Binding
import com.ivianuu.injekt.FunBinding
import com.ivianuu.injekt.merge.ApplicationComponent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.isActive
import kotlin.coroutines.coroutineContext

typealias KeyboardVisible = Flow<Boolean>

@Binding(ApplicationComponent::class)
fun keyboardVisible(
    accessibilityEvents: AccessibilityEvents,
    applyAccessibilityConfig: applyAccessibilityConfig,
    getKeyboardHeight: getKeyboardHeight,
    globalScope: GlobalScope
): KeyboardVisible {
    applyAccessibilityConfig(
        AccessibilityConfig(
            eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
        )
    )
    return accessibilityEvents
        .filter {
            it.isFullScreen &&
                it.className == "android.inputmethodservice.SoftInputWindow"
        }
        .map { Unit }
        .onStart { emit(Unit) }
        .transformLatest {
            while (coroutineContext.isActive) {
                emit(Unit)
                delay(100)
            }
        }
        .mapNotNull { getKeyboardHeight() }
        .map { it > 0 }
        .distinctUntilChanged()
        .shareIn(globalScope, SharingStarted.WhileSubscribed(1000), 1)
}

@FunBinding
fun getKeyboardHeight(inputMethodManager: InputMethodManager): Int? {
    return try {
        val method = inputMethodManager.javaClass.getMethod("getInputMethodWindowVisibleHeight")
        method.invoke(inputMethodManager) as Int
    } catch (e: Throwable) {
        null
    }
}
