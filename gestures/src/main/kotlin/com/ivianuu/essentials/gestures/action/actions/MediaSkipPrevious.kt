/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.view.*
import androidx.compose.material3.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import com.ivianuu.essentials.gestures.action.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*

@Provide object SkipPreviousActionId : ActionId("media_skip_previous") {
  @Provide val action
    get() = Action(
      id = SkipPreviousActionId,
      title = "Media skip previous",
      icon = { Icon(Icons.Default.SkipPrevious, null) }
    )

  @Provide fun executor(
    mediaActionSender: MediaActionSender
  ) = ActionExecutor<SkipPreviousActionId> {
    mediaActionSender.sendMediaAction(KeyEvent.KEYCODE_MEDIA_PREVIOUS)
  }

  @Provide inline val settingsScreen: @ActionSettingsScreen<SkipPreviousActionId> Screen<Unit>
    get() = MediaActionSettingsScreen()
}

