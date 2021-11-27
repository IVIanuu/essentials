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
import androidx.compose.runtime.collectAsState
import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionId
import com.ivianuu.essentials.loadResource
import com.ivianuu.essentials.torch.Torch
import com.ivianuu.injekt.Provide

@Provide object TorchActionId : ActionId("torch")

@Provide fun torchAction(torch: Torch, RP: ResourceProvider) = Action(
  id = TorchActionId,
  title = loadResource(R.string.es_action_torch),
  icon = {
    Icon(
      if (torch.torchEnabled.collectAsState().value) R.drawable.es_ic_flashlight_on
      else R.drawable.es_ic_flashlight_off
    )
  }
)

@Provide fun torchActionExecutor(torch: Torch) = ActionExecutor<TorchActionId>
  { torch.setTorchState(!torch.torchEnabled.collectAsState().value) }
