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
import com.ivianuu.essentials.loadResource
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.injekt.Provide

@Provide object SkipPreviousActionId : ActionId("media_skip_previous")

@Provide fun skipPreviousMediaAction(RP: ResourceProvider) = Action(
  id = SkipPreviousActionId,
  title = loadResource(R.string.es_action_media_skip_previous),
  icon = staticActionIcon(R.drawable.es_ic_skip_previous)
)

@Provide fun skipPreviousMediaActionExecutor(
  mediaActionSender: MediaActionSender
) = ActionExecutor<SkipPreviousActionId> {
  mediaActionSender(KeyEvent.KEYCODE_MEDIA_PREVIOUS)
}

@Provide inline val skipPreviousMediaActionSettingsKey: @ActionSettingsKey<SkipPreviousActionId> Key<Unit>
  get() = MediaActionSettingsKey()
