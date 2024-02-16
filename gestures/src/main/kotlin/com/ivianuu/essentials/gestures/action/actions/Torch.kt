/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import androidx.compose.material.Icon
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.res.painterResource
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
  title = resources(R.string.action_torch),
  icon = {
    Icon(
      painterResource(
        if (torchManager.torchEnabled.collectAsState().value) com.ivianuu.essentials.torch.R.drawable.ic_flashlight_on
        else R.drawable.ic_flashlight_off
      ),
      null
    )
  }
)

@Provide fun torchActionExecutor(torchManager: TorchManager) = ActionExecutor<TorchActionId>
{ torchManager.updateTorchState(!torchManager.torchEnabled.value) }
