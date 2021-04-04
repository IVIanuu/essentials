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

import com.ivianuu.essentials.app.ScopeWorker
import com.ivianuu.essentials.broadcast.BroadcastsFactory
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.scope.AppGivenScope
import kotlinx.coroutines.flow.collect

@Given
fun torchBroadcastWorker(
    @Given broadcastsFactory: BroadcastsFactory,
    @Given torch: Torch
): ScopeWorker<AppGivenScope> = {
    broadcastsFactory(ACTION_DISABLE_TORCH)
        .collect { torch.updateTorchEnabled(false) }
}

const val ACTION_DISABLE_TORCH = "com.ivianuu.essentials.torch.DISABLE_TORCH"
