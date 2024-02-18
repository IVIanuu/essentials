/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.annotation.*
import android.app.*
import android.os.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.gestures.*
import com.ivianuu.essentials.gestures.action.*
import com.ivianuu.injekt.*

@Provide object AssistantActionId : ActionId("assistant") {
  @Provide val action get() = Action(
    id = AssistantActionId,
    title = "Assistant",
    closeSystemDialogs = true,
    turnScreenOn = true,
    icon = staticActionIcon(R.drawable.ic_google)
  )

  @SuppressLint("DiscouragedPrivateApi")
  @Provide fun executor(searchManager: @SystemService SearchManager) =
    ActionExecutor<AssistantActionId> {
      val launchAssist = SearchManager::class.java
        .getDeclaredMethod("launchAssist", Bundle::class.java)
      launchAssist.invoke(searchManager, Bundle())
    }
}
