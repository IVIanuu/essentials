/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.view.*
import com.ivianuu.essentials.gestures.*
import com.ivianuu.essentials.gestures.action.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*

@Provide object StopActionId : ActionId("media_stop") {
  @Provide val action
    get() = Action(
      id = StopActionId,
      title = "Media stop",
      icon = staticActionIcon(R.drawable.ic_stop)
    )

  @Provide fun executor(
    mediaActionSender: MediaActionSender
  ) = ActionExecutor<StopActionId> {
    mediaActionSender(KeyEvent.KEYCODE_MEDIA_STOP)
  }

  @Provide inline val settingsScreen: @ActionSettingsKey<StopActionId> Screen<Unit>
    get() = MediaActionSettingsScreen()
}
