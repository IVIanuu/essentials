/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.content.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import com.ivianuu.essentials.gestures.action.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*

@Provide object SearchActionId : ActionId("search") {
  @Provide val action
    get() = Action(
      id = SearchActionId,
      title = "Search",
      icon = staticActionIcon(Icons.Default.Search),
      permissions = listOf(typeKeyOf<ActionSystemOverlayPermission>())
    )

  @Provide fun executor(intentSender: ActionIntentSender) =
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
}
