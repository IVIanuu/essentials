/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.view.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.gestures.*
import com.ivianuu.essentials.gestures.action.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*

@Provide object SkipPreviousActionId : ActionId("media_skip_previous") {
  @Provide val action
    get() = Action(
      id = SkipPreviousActionId,
      title = "Media skip previous",
      icon = staticActionIcon(R.drawable.ic_skip_previous)
    )

  @Provide fun executor(
    mediaActionSender: MediaActionSender
  ) = ActionExecutor<SkipPreviousActionId> {
    mediaActionSender(KeyEvent.KEYCODE_MEDIA_PREVIOUS)
  }

  @Provide inline val settingsScreen: @ActionSettingsKey<SkipPreviousActionId> Screen<Unit>
    get() = MediaActionSettingsScreen()
}

