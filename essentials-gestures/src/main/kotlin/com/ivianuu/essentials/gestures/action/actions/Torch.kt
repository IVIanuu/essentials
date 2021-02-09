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

import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionBinding
import com.ivianuu.essentials.gestures.action.ActionExecutorBinding
import com.ivianuu.essentials.gestures.action.ActionIcon
import com.ivianuu.essentials.gestures.action.ActionId
import com.ivianuu.essentials.store.DispatchAction
import com.ivianuu.essentials.torch.TorchAction
import com.ivianuu.essentials.torch.TorchAction.UpdateTorchEnabled
import com.ivianuu.essentials.torch.TorchState
import com.ivianuu.essentials.ui.core.Icon
import com.ivianuu.essentials.util.stringResource
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.GivenFun
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

object TorchActionId : ActionId("torch")

@ActionBinding<TorchActionId>
@Given
fun torchAction(
    @Given stringResource: stringResource,
    @Given torchIcon: Flow<TorchIcon>,
): Action = Action(
    id = TorchActionId,
    title = stringResource(R.string.es_action_torch),
    icon = torchIcon
)

@ActionExecutorBinding<TorchActionId>
@GivenFun
suspend fun toggleTorch(
    @Given torchState: Flow<TorchState>,
    @Given dispatchTorchAction: DispatchAction<TorchAction>,
) {
    dispatchTorchAction(UpdateTorchEnabled(!torchState.first().torchEnabled))
}

private typealias TorchIcon = ActionIcon

@Given fun torchIcon(@Given torchState: Flow<TorchState>): Flow<TorchIcon> = torchState
    .map {
        if (it.torchEnabled) R.drawable.es_ic_flash_on
        else R.drawable.es_ic_flash_off
    }
    .map {
        {
            Icon(it)
        }
    }
