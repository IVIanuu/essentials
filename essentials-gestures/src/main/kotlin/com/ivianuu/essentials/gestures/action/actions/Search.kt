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

import android.content.ComponentName
import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionId
import com.ivianuu.essentials.gestures.action.ActionSystemOverlayPermission
import com.ivianuu.essentials.loadResource
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.typeKeyOf

@Provide object SearchActionId : ActionId("search")

@Provide fun searchAction(RP: ResourceProvider) = Action(
  id = SearchActionId,
  title = loadResource(R.string.es_action_search),
  icon = staticActionIcon(Icons.Default.Search),
  permissions = listOf(typeKeyOf<ActionSystemOverlayPermission>())
)

@Provide fun searchActionExecutor(
  intentSender: ActionIntentSender
) = ActionExecutor<SearchActionId> {
  intentSender(
    Intent(Intent.ACTION_MAIN).apply {
      component = ComponentName(
        "com.google.android.googlequicksearchbox",
        "com.google.android.apps.gsa.queryentry.QueryEntryActivity"
      )
      addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    },
    false,
    null
  )
}
