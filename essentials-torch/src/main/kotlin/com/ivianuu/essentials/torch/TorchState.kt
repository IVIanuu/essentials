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

import com.ivianuu.essentials.coroutines.GlobalScope
import com.ivianuu.essentials.store.Actions
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.torch.TorchAction.UpdateTorchEnabled
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.common.Scoped
import com.ivianuu.injekt.component.AppComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterIsInstance

@Scoped<AppComponent>
@Given
fun torchState(
    @Given scope: GlobalScope,
    @Given initial: @Initial TorchState = TorchState(),
    @Given actions: Actions<TorchAction>
): StateFlow<TorchState> = actions
    .filterIsInstance<UpdateTorchEnabled>()
    .state(scope, initial) { copy(torchEnabled = it.value) }

data class TorchState(val torchEnabled: Boolean = false)

sealed class TorchAction {
    data class UpdateTorchEnabled(val value: Boolean) : TorchAction()
}
