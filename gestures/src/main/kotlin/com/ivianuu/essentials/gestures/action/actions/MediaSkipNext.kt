/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.view.KeyEvent
import com.ivianuu.essentials.Resources
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionId
import com.ivianuu.essentials.gestures.action.ActionSettingsKey
import com.ivianuu.essentials.ui.navigation.Screen
import com.ivianuu.injekt.Provide

@Provide object SkipNextActionId : ActionId("media_skip_next")

@Provide fun skipNextMediaAction(resources: Resources) = Action(
  id = SkipNextActionId,
  title = resources(R.string.es_action_media_skip_next),
  icon = staticActionIcon(R.drawable.es_ic_skip_next)
)

@Provide fun skipNextMediaActionExecutor(
  mediaActionSender: MediaActionSender
) = ActionExecutor<SkipNextActionId> {
  mediaActionSender(KeyEvent.KEYCODE_MEDIA_NEXT)
}

@Provide inline val skipNextMediaActionSettingsScreen: @ActionSettingsKey<SkipNextActionId> Screen<Unit>
  get() = MediaActionSettingsScreen()
