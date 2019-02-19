/*
 * Copyright 2018 Manuel Wrage
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

import androidx.lifecycle.ViewModel
import com.ivianuu.essentials.util.asMainCoroutineScope

/**
 * A [ViewModel] which auto disposes itself
 */
abstract class EsViewModel : ViewModel() {

    val coroutineScope by lazy { scope.asMainCoroutineScope() }

    private val clearedListeners = mutableListOf<(() -> Unit)>()

    override fun onCleared() {
        clearedListeners.toList().forEach { it() }
        super.onCleared()
    }

    fun addClearedListener(listener: () -> Unit) {
        if (!clearedListeners.contains(listener)) {
            clearedListeners.add(listener)
        }
    }

    fun removeClearedListener(listener: () -> Unit) {
        clearedListeners.remove(listener)
    }

}