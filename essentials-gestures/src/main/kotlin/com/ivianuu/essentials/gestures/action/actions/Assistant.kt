/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
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
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.inject

@Provide object AssistantActionId : ActionId("assistant")

context(ResourceProvider) @Provide fun assistantAction() = Action(
  id = AssistantActionId,
  title = loadResource(R.string.es_action_assistant),
  closeSystemDialogs = true,
  turnScreenOn = true,
  icon = staticActionIcon(R.drawable.es_ic_google)
)

context(SearchManager)
    @SuppressLint("DiscouragedPrivateApi")
    @Provide fun assistantActionExecutor() = ActionExecutor<AssistantActionId> {
  val launchAssist = SearchManager::class.java
    .getDeclaredMethod("launchAssist", Bundle::class.java)
  launchAssist.invoke(inject<SearchManager>(), Bundle())
}
