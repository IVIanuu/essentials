/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.gestures.action.actions

import android.content.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import essentials.gestures.action.*
import injekt.*

@Provide object SearchActionId : ActionId("search") {
  @Provide val action
    get() = Action(
      id = SearchActionId,
      title = "Search",
      permissions = listOf(ActionSystemOverlayPermission::class),
      icon = { Icon(Icons.Default.Search, null) }
    )

  @Provide suspend fun execute(sendActionMediaIntent: sendActionIntent):
      ActionExecutorResult<SearchActionId> {
    sendActionMediaIntent(
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
