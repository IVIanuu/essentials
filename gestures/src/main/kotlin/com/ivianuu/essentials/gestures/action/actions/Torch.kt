/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import androidx.compose.material.Icon
import androidx.compose.runtime.collectAsState
import com.ivianuu.essentials.Resources
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionId
import com.ivianuu.essentials.torch.TorchManager
import com.ivianuu.injekt.Provide

@Provide object TorchActionId : ActionId("torch")

@Provide fun torchAction(resources: Resources, torchManager: TorchManager) = Action(
  id = TorchActionId,
  title = resources.resource(R.string.es_action_torch),
  icon = {
    Icon(
      if (torchManager.torchEnabled.collectAsState().value) R.drawable.es_ic_flashlight_on
      else R.drawable.es_ic_flashlight_off
    )
  }
)

@Provide fun torchActionExecutor(torchManager: TorchManager) = ActionExecutor<TorchActionId>
{ torchManager.updateTorchState(!torchManager.torchEnabled.value) }
