/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.annotation.*
import android.app.*
import android.os.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.android.*

@Provide object AssistantActionId : ActionId("assistant")

@Provide fun assistantAction(rp: ResourceProvider): Action<AssistantActionId> =
  Action(
    id = AssistantActionId,
    title = loadResource(R.string.es_action_assistant),
    unlockScreen = true,
    icon = singleActionIcon(R.drawable.es_ic_google)
  )

@SuppressLint("DiscouragedPrivateApi")
@Provide fun assistantActionExecutor(
  searchManager: @SystemService SearchManager
): ActionExecutor<AssistantActionId> = {
  val launchAssist = searchManager.javaClass
    .getDeclaredMethod("launchAssist", Bundle::class.java)
  launchAssist.invoke(searchManager, Bundle())
}
