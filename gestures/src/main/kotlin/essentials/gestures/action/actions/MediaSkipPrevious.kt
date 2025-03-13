/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.gestures.action.actions

import android.view.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import essentials.gestures.action.*
import injekt.*

@Provide object SkipPreviousActionId : ActionId("media_skip_previous") {
  @Provide val action
    get() = Action(
      id = SkipPreviousActionId,
      title = "Media skip previous",
      icon = { Icon(Icons.Default.SkipPrevious, null) }
    )

  @Provide suspend fun execute(
    mediaActionSender: MediaActionSender
  ): ActionExecutorResult<SkipPreviousActionId> {
    mediaActionSender.sendMediaAction(KeyEvent.KEYCODE_MEDIA_PREVIOUS)
  }

  @Provide inline val settingsScreen: ActionSettingsScreen<SkipPreviousActionId>
    get() = MediaActionSettingsScreen()
}

