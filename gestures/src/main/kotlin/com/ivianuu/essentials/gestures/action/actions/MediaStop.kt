/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.view.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.gestures.*
import com.ivianuu.essentials.gestures.action.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*

@Provide object StopActionId : ActionId("media_stop")

@Provide fun stopMediaAction(resources: Resources) = Action(
  id = StopActionId,
  title = resources(R.string.action_media_stop),
  icon = staticActionIcon(R.drawable.ic_stop)
)

@Provide fun stopMediaActionExecutor(
  mediaActionSender: MediaActionSender
) = ActionExecutor<StopActionId> {
  mediaActionSender(KeyEvent.KEYCODE_MEDIA_STOP)
}

@Provide inline val stopMediaActionSettingsScreen: @ActionSettingsKey<StopActionId> Screen<Unit>
  get() = MediaActionSettingsScreen()
