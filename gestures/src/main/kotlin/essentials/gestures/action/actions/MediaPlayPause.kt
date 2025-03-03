/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.gestures.action.actions

import android.view.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import essentials.gestures.action.*
import essentials.ui.navigation.*
import injekt.*

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
