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

@Provide object SkipNextActionId : ActionId("media_skip_next") {
  @Provide val action
    get() = Action(
      id = SkipNextActionId,
      title = "Media skip next",
      icon = staticActionIcon(R.drawable.ic_skip_next)
    )

  @Provide fun executor(
    mediaActionSender: MediaActionSender
  ) = ActionExecutor<SkipNextActionId> {
    mediaActionSender(KeyEvent.KEYCODE_MEDIA_NEXT)
  }

  @Provide inline val settingsScreen: @ActionSettingsKey<SkipNextActionId> Screen<Unit>
    get() = MediaActionSettingsScreen()
}
