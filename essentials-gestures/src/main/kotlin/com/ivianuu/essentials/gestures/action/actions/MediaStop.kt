/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.view.KeyEvent
import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionId
import com.ivianuu.essentials.gestures.action.ActionSettingsKey
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.injekt.Provide

@Provide object StopActionId : ActionId("media_stop")

@Provide fun stopMediaAction(resourceProvider: ResourceProvider) = Action(
  id = StopActionId,
  title = resourceProvider(R.string.es_action_media_stop),
  icon = staticActionIcon(R.drawable.es_ic_stop)
)

@Provide fun stopMediaActionExecutor(
  mediaActionSender: MediaActionSender
) = ActionExecutor<StopActionId> {
  mediaActionSender(KeyEvent.KEYCODE_MEDIA_STOP)
}

@Provide inline val stopMediaActionSettingsKey: @ActionSettingsKey<StopActionId> Key<Unit>
  get() = MediaActionSettingsKey()
