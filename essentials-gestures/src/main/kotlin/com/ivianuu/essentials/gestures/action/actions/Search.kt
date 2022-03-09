/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.content.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.gestures.*
import com.ivianuu.essentials.gestures.action.*

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
