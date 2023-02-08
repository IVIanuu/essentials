/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import androidx.compose.material.Icon
import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.essentials.compose.bind
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionId
import com.ivianuu.essentials.torch.TorchManager
import com.ivianuu.injekt.Provide

@Provide object TorchActionId : ActionId("torch")

@Provide fun torchAction(
  resourceProvider: ResourceProvider,
  torchManager: TorchManager
) = Action(
  id = TorchActionId,
  title = resourceProvider(R.string.es_action_torch),
  icon = {
    Icon(
      if (torchManager.torchEnabled.bind()) R.drawable.es_ic_flashlight_on
      else R.drawable.es_ic_flashlight_off
    )
  }
)

@Provide fun torchActionExecutor(torchManager: TorchManager) = ActionExecutor<TorchActionId>
{ torchManager.setTorchState(!torchManager.torchEnabled.value) }
