/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.content.ComponentName
import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import com.ivianuu.essentials.Resources
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionId
import com.ivianuu.essentials.gestures.action.ActionSystemOverlayPermission
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.typeKeyOf

@Provide object SearchActionId : ActionId("search")

@Provide fun searchAction(resources: Resources) = Action(
  id = SearchActionId,
  title = resources.resource(R.string.es_action_search),
  icon = staticActionIcon(Icons.Default.Search),
  permissions = listOf(typeKeyOf<ActionSystemOverlayPermission>())
)

@Provide fun searchActionExecutor(intentSender: ActionIntentSender) =
  ActionExecutor<SearchActionId> {
    intentSender(
      Intent(Intent.ACTION_MAIN).apply {
        component = ComponentName(
          "com.google.android.googlequicksearchbox",
          "com.google.android.apps.gsa.queryentry.QueryEntryActivity"
        )
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      },
      null
    )
}
