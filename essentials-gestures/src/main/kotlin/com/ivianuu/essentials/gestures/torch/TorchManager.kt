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

package com.ivianuu.essentials.gestures.torch

import android.content.Context
import android.content.Intent
import com.ivianuu.essentials.messaging.BroadcastFactory
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.android.ApplicationScope
import hu.akarnokd.kotlin.flow.BehaviorSubject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * Provides the torch state
 */
@ApplicationScope
@Inject
class TorchManager internal constructor(
    broadcastFactory: BroadcastFactory,
    private val context: Context
) {

    private val _torchState = BehaviorSubject(false)
    val torchState: Flow<Boolean>
        get() = _torchState

    init {
        GlobalScope.launch {
            try {
                TorchService.syncState(context)
            } catch (e: Exception) {
            }

            broadcastFactory.create(ACTION_TORCH_STATE_CHANGED)
                .map { it.getBooleanExtra(EXTRA_TORCH_STATE, false) }
                .collect { _torchState.emit(it) }
        }
    }

    fun toggleTorch() {
        TorchService.toggleTorch(context)
    }

    internal fun setTorchState(enabled: Boolean) {
        context.sendBroadcast(Intent(ACTION_TORCH_STATE_CHANGED).apply {
            putExtra(EXTRA_TORCH_STATE, enabled)
        })
    }

    private companion object {
        private const val ACTION_TORCH_STATE_CHANGED =
            "com.ivianuu.essentials.gestures.TORCH_STATE_CHANGED"
        private const val EXTRA_TORCH_STATE = "torch_state"
    }
}