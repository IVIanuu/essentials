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

package com.ivianuu.essentials.gestures

import android.view.accessibility.AccessibilityEvent
import android.view.inputmethod.InputMethodManager
import com.ivianuu.essentials.gestures.accessibility.AccessibilityComponent
import com.ivianuu.essentials.util.PublishSubject
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.android.ApplicationScope
import io.reactivex.Observable
import java.lang.reflect.Method
import java.util.concurrent.TimeUnit

/**
 * Provides info about the keyboard state
 */
@Inject
@ApplicationScope
class KeyboardVisibilityDetector(
    private val inputMethodManager: InputMethodManager
) : AccessibilityComponent() {

    private val softInputChanges = PublishSubject<Unit>()

    val keyboardVisible: Observable<Boolean> = softInputChanges
        .startWith(Unit)
        .switchMap {
            Observable.interval(100, TimeUnit.MILLISECONDS)
                .startWith(0)
        }
        .map { getKeyboardHeight() }
        .map { it > 0 }
        .distinctUntilChanged()
        .share()

    private fun getKeyboardHeight(): Int {
        return try {
            val method = getInputMethodWindowVisibleHeightMethod
                ?: inputMethodManager.javaClass.getMethod("getInputMethodWindowVisibleHeight")
                    .also { getInputMethodWindowVisibleHeightMethod = it }
            method.invoke(inputMethodManager) as Int
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return

        if (!event.isFullScreen) return

        if (event.className != "android.inputmethodservice.SoftInputWindow") return

        softInputChanges.onNext(Unit)
    }

    private companion object {
        private var getInputMethodWindowVisibleHeightMethod: Method? = null
    }

}