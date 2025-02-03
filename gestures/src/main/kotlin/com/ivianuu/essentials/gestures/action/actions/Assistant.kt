/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.annotation.*
import android.app.*
import android.os.*
import androidx.compose.material3.*
import androidx.compose.material.icons.*
import androidx.compose.ui.res.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.gestures.*
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.*
import com.ivianuu.injekt.*
import compose.icons.*
import compose.icons.fontawesomeicons.*
import compose.icons.fontawesomeicons.brands.*

@Provide object AssistantActionId : ActionId("assistant") {
  @Provide val action get() = Action(
    id = AssistantActionId,
    title = "Assistant",
    closeSystemDialogs = true,
    turnScreenOn = true,
    icon = { Icon(FontAwesomeIcons.Brands.Google, null) }
  )

  @SuppressLint("DiscouragedPrivateApi")
  @Provide fun executor(searchManager: @SystemService SearchManager) =
    ActionExecutor<AssistantActionId> {
      val launchAssist = SearchManager::class.java
        .getDeclaredMethod("launchAssist", Bundle::class.java)
      launchAssist.invoke(searchManager, Bundle())
    }
}
