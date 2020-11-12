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

import androidx.compose.foundation.Icon
import androidx.compose.ui.res.vectorResource
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionBinding
import com.ivianuu.essentials.gestures.action.ActionIcon
import com.ivianuu.essentials.torch.TorchAction
import com.ivianuu.essentials.torch.TorchAction.*
import com.ivianuu.essentials.torch.TorchState
import com.ivianuu.essentials.ui.store.Dispatch
import com.ivianuu.essentials.ui.store.State
import com.ivianuu.essentials.ui.store.UiState
import com.ivianuu.essentials.util.stringResource
import com.ivianuu.injekt.Binding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

@ActionBinding("torch")
fun torchAction(
    stringResource: stringResource,
    torchIcon: TorchIcon,
    torchState: @State StateFlow<TorchState>,
    dispatchTorchAction: @Dispatch (TorchAction) -> Unit
): Action = Action(
    key = "torch",
    title = stringResource(R.string.es_action_torch),
    icon = torchIcon,
    execute = { dispatchTorchAction(UpdateTorchEnabled(!torchState.value.torchEnabled)) }
)

typealias TorchIcon = ActionIcon
@Binding
fun torchIcon(torchState: @State Flow<TorchState>): TorchIcon = torchState
    .map {
        if (it.torchEnabled) R.drawable.es_ic_flash_on
        else R.drawable.es_ic_flash_off
    }
    .map {
        {
            Icon(vectorResource(it))
        }
    }
