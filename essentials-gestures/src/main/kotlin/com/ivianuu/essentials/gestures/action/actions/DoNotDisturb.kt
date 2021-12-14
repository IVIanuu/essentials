/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.app.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.android.*
import com.ivianuu.injekt.common.*
import kotlinx.coroutines.flow.*

@Provide object DoNotDisturbAction : ActionId("do_not_disturb")

@Provide fun doNotDisturbAction(icon: DoNotDisturbIcon, RP: ResourceProvider) = Action(
  id = DoNotDisturbAction,
  title = loadResource(R.string.es_action_do_not_disturb),
  icon = icon,
  permissions = listOf(typeKeyOf<ActionNotificationPolicyPermission>())
)

@Provide fun doNotDisturbActionExecutor(
  notificationManager: @SystemService NotificationManager
) = ActionExecutor<DoNotDisturbAction> {
  notificationManager.setInterruptionFilter(
    if (notificationManager.currentInterruptionFilter != NotificationManager.INTERRUPTION_FILTER_PRIORITY)
      NotificationManager.INTERRUPTION_FILTER_PRIORITY else NotificationManager.INTERRUPTION_FILTER_ALL
  )
}

fun interface DoNotDisturbIcon : ActionIcon

@Provide fun doNotDisturbIcon(
  broadcastsFactory: BroadcastsFactory,
  notificationManager: @SystemService NotificationManager,
) = DoNotDisturbIcon {
  val doNotDisturb by remember {
    broadcastsFactory(
      NotificationManager.ACTION_INTERRUPTION_FILTER_CHANGED
    )
      .onStart<Any?> { emit(Unit) }
      .map {
        notificationManager.currentInterruptionFilter == NotificationManager.INTERRUPTION_FILTER_PRIORITY
      }
  }.collectAsState(false)

  Icon(
    if (doNotDisturb) R.drawable.es_ic_do_not_disturb_on
    else R.drawable.es_ic_do_not_disturb_off
  )
}
