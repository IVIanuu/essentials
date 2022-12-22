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

@Provide object SkipNextActionId : ActionId("media_skip_next")

context(ResourceProvider) @Provide fun skipNextMediaAction() = Action(
  id = SkipNextActionId,
  title = loadResource(R.string.es_action_media_skip_next),
  icon = staticActionIcon(R.drawable.es_ic_skip_next)
)

@Provide fun skipNextMediaActionExecutor(
  mediaActionSender: MediaActionSender
) = ActionExecutor<SkipNextActionId> {
  mediaActionSender(KeyEvent.KEYCODE_MEDIA_NEXT)
}

@Provide inline val skipNextMediaActionSettingsKey: @ActionSettingsKey<SkipNextActionId> Key<Unit>
  get() = MediaActionSettingsKey()
