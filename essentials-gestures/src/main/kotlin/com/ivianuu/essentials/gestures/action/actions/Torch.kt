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

import androidx.compose.material.*
import androidx.compose.ui.res.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.*
import com.ivianuu.essentials.torch.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.flow.*

@Given
object TorchActionId : ActionId("torch")

@Given
fun torchAction(
    @Given stringResource: StringResourceProvider,
    @Given torchIcon: Flow<TorchIcon>,
) = Action<TorchActionId>(
    id = TorchActionId,
    title = stringResource(R.string.es_action_torch, emptyList()),
    icon = torchIcon
)

@Given
fun torchActionExecutor(
    @Given torch: MutableStateFlow<TorchState>
): ActionExecutor<TorchActionId> = { torch.update { not() } }

private typealias TorchIcon = ActionIcon

@Given
fun torchIcon(@Given torchState: Flow<TorchState>): Flow<TorchIcon> = torchState
    .map {
        if (it) R.drawable.es_ic_flash_on
        else R.drawable.es_ic_flash_off
    }
    .map {
        {
            Icon(painterResource(it), null)
        }
    }
