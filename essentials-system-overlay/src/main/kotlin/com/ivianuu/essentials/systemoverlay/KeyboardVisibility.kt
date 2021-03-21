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

package com.ivianuu.essentials.systemoverlay

import android.view.inputmethod.InputMethodManager
import com.github.michaelbull.result.getOrElse
import com.github.michaelbull.result.runCatching
import com.ivianuu.essentials.accessibility.AccessibilityConfig
import com.ivianuu.essentials.accessibility.AccessibilityEvent
import com.ivianuu.essentials.accessibility.AndroidAccessibilityEvent
import com.ivianuu.essentials.util.ScopeCoroutineScope
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.scope.AppGivenScope
import com.ivianuu.injekt.scope.Scoped
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transformLatest

typealias KeyboardVisible = Boolean

@Given
fun keyboardVisible(
    @Given accessibilityEvents: Flow<AccessibilityEvent>,
    @Given keyboardHeightProvider: KeyboardHeightProvider,
    @Given scope: ScopeCoroutineScope<AppGivenScope>
): @Scoped<AppGivenScope> Flow<KeyboardVisible> = accessibilityEvents
    .filter {
        it.isFullScreen &&
                it.className == "android.inputmethodservice.SoftInputWindow"
    }
    .map { Unit }
    .onStart { emit(Unit) }
    .transformLatest {
        emit(true)
        while ((keyboardHeightProvider() ?: 0) > 0) {
            delay(100)
        }
        emit(false)
        awaitCancellation()
    }
    .distinctUntilChanged()
    .stateIn(scope, SharingStarted.WhileSubscribed(1000), false)

@Given
val keyboardVisibilityAccessibilityConfig = flow {
    emit(
        AccessibilityConfig(
            eventTypes = AndroidAccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
        )
    )
}

typealias KeyboardHeightProvider = () -> Int?

@Given
fun keyboardHeightProvider(
    @Given inputMethodManager: InputMethodManager
): KeyboardHeightProvider = {
    runCatching {
        val method = inputMethodManager.javaClass.getMethod("getInputMethodWindowVisibleHeight")
        method.invoke(inputMethodManager) as Int
    }.getOrElse { null }
}
