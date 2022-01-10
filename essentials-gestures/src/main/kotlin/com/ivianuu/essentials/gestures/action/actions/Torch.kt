/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import androidx.compose.material.*
import androidx.compose.runtime.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.*
import com.ivianuu.essentials.torch.*
import com.ivianuu.injekt.*

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
  { torch.setTorchState(!torch.torchEnabled.value) }
