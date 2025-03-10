/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.gestures.action.actions

import android.view.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import essentials.gestures.action.*
import essentials.ui.navigation.*
import injekt.*

@Provide object StopActionId : ActionId("media_stop") {
  @Provide val action
    get() = Action(
      id = StopActionId,
      title = "Media stop",
      icon = { Icon(Icons.Default.Stop, null) }
    )

  @Provide fun executor(
    mediaActionSender: MediaActionSender
  ) = ActionExecutor<StopActionId> {
    mediaActionSender.sendMediaAction(KeyEvent.KEYCODE_MEDIA_STOP)
  }

  @Provide inline val settingsScreen: @ActionSettingsScreen<StopActionId> Screen<Unit>
    get() = MediaActionSettingsScreen()
}
