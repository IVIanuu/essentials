/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.gestures.action.actions

import android.annotation.*
import android.app.*
import android.os.*
import androidx.compose.material3.*
import androidx.core.content.*
import compose.icons.*
import compose.icons.fontawesomeicons.*
import compose.icons.fontawesomeicons.brands.*
import essentials.gestures.action.*
import injekt.*

@Provide object AssistantActionId : ActionId("assistant") {
  @Provide val action get() = Action(
    id = AssistantActionId,
    title = "Assistant",
    closeSystemDialogs = true,
    turnScreenOn = true,
    icon = { Icon(FontAwesomeIcons.Brands.Google, null) }
  )

  @SuppressLint("DiscouragedPrivateApi")
  @Provide fun execute(context: Application):
      ActionExecutorResult<AssistantActionId> {
    val launchAssist = SearchManager::class.java
        .getDeclaredMethod("launchAssist", Bundle::class.java)
      launchAssist.invoke(context.getSystemService(), Bundle())
    }
}
