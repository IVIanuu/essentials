/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.annotation.SuppressLint
import android.app.SearchManager
import android.os.Bundle
import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionId
import com.ivianuu.essentials.loadResource
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.android.SystemService

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
