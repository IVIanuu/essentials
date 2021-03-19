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

import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.torch.TorchAction.UpdateTorchEnabled
import com.ivianuu.essentials.util.ScopeCoroutineScope
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.scope.Scoped
import com.ivianuu.injekt.scope.AppGivenScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterIsInstance

@Scoped<AppGivenScope>
@Given
fun torchState(
    @Given scope: ScopeCoroutineScope<AppGivenScope>,
    @Given initial: @Initial TorchState = TorchState(),
    @Given actions: Flow<TorchAction>
): StateFlow<TorchState> = actions
    .filterIsInstance<UpdateTorchEnabled>()
    .state(scope, initial) { copy(torchEnabled = it.value) }

@Given
val torchActions = EventFlow<TorchAction>()

data class TorchState(val torchEnabled: Boolean = false)

sealed class TorchAction {
    data class UpdateTorchEnabled(val value: Boolean) : TorchAction()
}
