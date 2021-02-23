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

import com.ivianuu.essentials.app.AppWorker
import com.ivianuu.essentials.app.AppWorkerBinding
import com.ivianuu.essentials.broadcast.BroadcastsFactory
import com.ivianuu.essentials.store.DispatchAction
import com.ivianuu.essentials.torch.TorchAction.UpdateTorchEnabled
import com.ivianuu.injekt.Given
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

@AppWorkerBinding
@Given
fun torchBroadcastWorker(
    @Given broadcastsFactory: BroadcastsFactory,
    @Given dispatch: DispatchAction<TorchAction>
): AppWorker = {
    broadcastsFactory(ACTION_DISABLE_TORCH)
        .onEach { dispatch(UpdateTorchEnabled(false)) }
        .collect()
}

const val ACTION_DISABLE_TORCH = "com.ivianuu.essentials.torch.DISABLE_TORCH"
