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
import com.ivianuu.injekt.android.*

@Provide object AssistantActionId : ActionId("assistant")

@Provide fun assistantAction(RP: ResourceProvider) = Action(
  id = AssistantActionId,
  title = loadResource(R.string.es_action_assistant),
  closeSystemDialogs = true,
  turnScreenOn = true,
  icon = staticActionIcon(R.drawable.es_ic_google)
)

@SuppressLint("DiscouragedPrivateApi")
@Provide fun assistantActionExecutor(
  searchManager: @SystemService SearchManager
) = ActionExecutor<AssistantActionId> {
  val launchAssist = searchManager.javaClass
    .getDeclaredMethod("launchAssist", Bundle::class.java)
  launchAssist.invoke(searchManager, Bundle())
}
