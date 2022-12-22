/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.view.KeyEvent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionId
import com.ivianuu.essentials.gestures.action.ActionSettingsKey
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.injekt.Provide

@Provide object PlayPauseActionId : ActionId("media_play_pause")

context(ResourceProvider) @Provide fun playPauseMediaAction() = Action(
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
