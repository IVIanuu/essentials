/*
 * Copyright 2021 Manuel Wrage
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
import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.essentials.coroutines.update2
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionIcon
import com.ivianuu.essentials.gestures.action.ActionId
import com.ivianuu.essentials.loadResource
import com.ivianuu.essentials.torch.TorchState
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

@Provide object TorchActionId : ActionId("torch")

@Provide fun torchAction(
  torchState: Flow<TorchState>,
  rp: ResourceProvider,
): Action<TorchActionId> = Action(
  id = TorchActionId,
  title = loadResource(R.string.es_action_torch),
  icon = torchState.torchIcon()
)

@Provide fun torchActionExecutor(
  torch: MutableStateFlow<TorchState>
): ActionExecutor<TorchActionId> = { torch.update2 { not() } }

private fun Flow<TorchState>.torchIcon(): Flow<ActionIcon> = this
  .map {
    if (it) R.drawable.es_ic_flash_on
    else R.drawable.es_ic_flash_off
  }
  .map {
    {
      Icon(it)
    }
  }
