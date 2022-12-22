/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import androidx.compose.material.Icon
import androidx.compose.runtime.collectAsState
import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionId
import com.ivianuu.essentials.torch.Torch
import com.ivianuu.injekt.Provide

@Provide object TorchActionId : ActionId("torch")

context(ResourceProvider) @Provide fun torchAction(torch: Torch) = Action(
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
