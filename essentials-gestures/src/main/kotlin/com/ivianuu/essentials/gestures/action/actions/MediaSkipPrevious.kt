/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.view.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.*
import com.ivianuu.essentials.ui.navigation.*

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
