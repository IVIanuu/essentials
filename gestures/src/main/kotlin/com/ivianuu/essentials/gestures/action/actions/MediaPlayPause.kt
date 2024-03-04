/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.view.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import com.ivianuu.essentials.gestures.action.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*

@Provide object PlayPauseActionId : ActionId("media_play_pause") {
  @Provide val action
    get() = Action(
      id = PlayPauseActionId,
      title = "Media play/Pause",
      icon = staticActionIcon(Icons.Default.PlayArrow)
    )

  @Provide fun executor(
    mediaActionSender: MediaActionSender
  ) = ActionExecutor<PlayPauseActionId> {
    mediaActionSender.sendMediaAction(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE)
  }

  @Provide inline val settingsScreen: @ActionSettingsScreen<PlayPauseActionId> Screen<Unit>
    get() = MediaActionSettingsScreen()
}
