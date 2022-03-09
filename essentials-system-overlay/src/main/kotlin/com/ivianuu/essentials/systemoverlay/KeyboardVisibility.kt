/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.systemoverlay

import android.view.inputmethod.*
import com.github.michaelbull.result.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.accessibility.*
import com.ivianuu.injekt.android.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@JvmInline value class KeyboardVisible(val value: Boolean)

@Provide fun keyboardVisible(
  accessibilityEvents: Flow<AccessibilityEvent>,
  keyboardHeightProvider: @KeyboardHeightProvider () -> Int?,
  scope: NamedCoroutineScope<AppScope>
): @Eager<AppScope> Flow<KeyboardVisible> = accessibilityEvents
  .filter {
    it.isFullScreen &&
        it.className == "android.inputmethodservice.SoftInputWindow"
  }
  .onStart<Any?> { emit(Unit) }
  .transformLatest {
    emit(true)
    while ((keyboardHeightProvider() ?: 0) > 0) {
      delay(100)
    }
    emit(false)
    awaitCancellation()
  }
  .map { KeyboardVisible(it) }
  .distinctUntilChanged()
  .stateIn(scope, SharingStarted.WhileSubscribed(1000), KeyboardVisible(false))

@Provide val keyboardVisibilityAccessibilityConfig: AccessibilityConfig
  get() = AccessibilityConfig(
    eventTypes = AndroidAccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
  )

@Tag private annotation class KeyboardHeightProvider

@Provide fun keyboardHeightProvider(
  inputMethodManager: @SystemService InputMethodManager
): @KeyboardHeightProvider () -> Int? = {
  runCatching {
    val method = inputMethodManager.javaClass.getMethod("getInputMethodWindowVisibleHeight")
    method.invoke(inputMethodManager) as Int
  }.getOrElse { null }
}
