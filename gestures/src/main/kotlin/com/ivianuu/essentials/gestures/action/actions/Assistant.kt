/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.annotation.SuppressLint
import android.app.SearchManager
import android.os.Bundle
import androidx.compose.ui.res.painterResource
import com.ivianuu.essentials.Resources
import com.ivianuu.essentials.SystemService
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionId
import com.ivianuu.injekt.Provide

@Provide object AssistantActionId : ActionId("assistant")

@Provide fun assistantAction(resources: Resources) = Action(
  id = AssistantActionId,
  title = resources(R.string.es_action_assistant),
  closeSystemDialogs = true,
  turnScreenOn = true,
  icon = staticActionIcon(R.drawable.es_ic_google)
)

@SuppressLint("DiscouragedPrivateApi")
@Provide fun assistantActionExecutor(
  searchManager: @SystemService SearchManager
) = ActionExecutor<AssistantActionId> {
  val launchAssist = SearchManager::class.java
    .getDeclaredMethod("launchAssist", Bundle::class.java)
  launchAssist.invoke(searchManager, Bundle())
}
