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

package com.ivianuu.essentials.torch

import com.ivianuu.essentials.app.AppWorkerBinding
import com.ivianuu.essentials.broadcast.broadcasts
import com.ivianuu.essentials.torch.TorchAction.*
import com.ivianuu.essentials.ui.store.Dispatch
import com.ivianuu.essentials.ui.store.State
import com.ivianuu.essentials.ui.store.StateEffect
import com.ivianuu.injekt.FunApi
import com.ivianuu.injekt.FunBinding
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect

@StateEffect
@FunBinding
suspend fun TorchBroadcastReceiver(
    broadcasts: broadcasts,
    dispatch: @Dispatch (TorchAction) -> Unit,
    @FunApi state: TorchState,
) {
    broadcasts(ACTION_TOGGLE_TORCH).collect {
        dispatch(UpdateTorchEnabled(!state.torchEnabled))
    }
}

const val ACTION_TOGGLE_TORCH = "com.ivianuu.essentials.torch.TOGGLE_TORCH"
