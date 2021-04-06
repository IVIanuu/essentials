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

package com.ivianuu.essentials.gestures.action.actions

import androidx.compose.material.Icon
import androidx.compose.ui.res.painterResource
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionBinding
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionExecutorBinding
import com.ivianuu.essentials.gestures.action.ActionIcon
import com.ivianuu.essentials.gestures.action.ActionId
import com.ivianuu.essentials.store.Store
import com.ivianuu.essentials.torch.TorchAction
import com.ivianuu.essentials.torch.TorchAction.UpdateTorchEnabled
import com.ivianuu.essentials.torch.TorchState
import com.ivianuu.essentials.util.ResourceProvider
import com.ivianuu.injekt.Given
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

@Given
object TorchActionId : ActionId("torch")

@Given
fun torchAction(
    @Given resourceProvider: ResourceProvider,
    @Given torchIcon: Flow<TorchIcon>,
): @ActionBinding<TorchActionId> Action = Action(
    id = TorchActionId,
    title = resourceProvider.string(R.string.es_action_torch),
    icon = torchIcon
)

@Given
fun torchActionExecutor(
    @Given torch: Store<TorchState, TorchAction>
): @ActionExecutorBinding<TorchActionId> ActionExecutor = {
    torch.send(UpdateTorchEnabled(!torch.first().torchEnabled))
}

private typealias TorchIcon = ActionIcon

@Given
fun torchIcon(@Given torchState: Flow<TorchState>): Flow<TorchIcon> = torchState
    .map {
        if (it.torchEnabled) R.drawable.es_ic_flash_on
        else R.drawable.es_ic_flash_off
    }
    .map {
        {
            Icon(painterResource(it), null)
        }
    }
