/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.view.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.*
import com.ivianuu.essentials.ui.navigation.*

@Provide object PlayPauseActionId : ActionId("media_play_pause")

@Provide fun playPauseMediaAction(RP: ResourceProvider) = Action(
  id = PlayPauseActionId,
  title = loadResource(R.string.es_action_media_play_pause),
  icon = staticActionIcon(Icons.Default.PlayArrow)
)

@Provide fun playPauseMediaActionExecutor(
  mediaActionSender: MediaActionSender
) = ActionExecutor<PlayPauseActionId> {
  mediaActionSender(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE)
}

@Provide inline val playPauseMediaActionSettingsKey: @ActionSettingsKey<PlayPauseActionId> Key<Unit>
  get() = MediaActionSettingsKey()
