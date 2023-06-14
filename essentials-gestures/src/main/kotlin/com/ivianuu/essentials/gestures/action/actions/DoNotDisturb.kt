/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.app.NotificationManager
import androidx.compose.material3.Icon
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.ivianuu.essentials.Resources
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionIcon
import com.ivianuu.essentials.gestures.action.ActionId
import com.ivianuu.essentials.gestures.action.ActionNotificationPolicyPermission
import com.ivianuu.essentials.util.BroadcastsFactory
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.android.SystemService
import com.ivianuu.injekt.common.typeKeyOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

@Provide object DoNotDisturbAction : ActionId("do_not_disturb")

@Provide fun doNotDisturbAction(
  icon: DoNotDisturbIcon,
  resources: Resources
) = Action(
  id = DoNotDisturbAction,
  title = resources(R.string.es_action_do_not_disturb),
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
  notificationManager: @SystemService NotificationManager
) = DoNotDisturbIcon {
  val doNotDisturb by remember {
    broadcastsFactory(
      NotificationManager.ACTION_INTERRUPTION_FILTER_CHANGED
    )
      .onStart<Any?> { emit(Unit) }
      .map {
        notificationManager.currentInterruptionFilter ==
            NotificationManager.INTERRUPTION_FILTER_PRIORITY
      }
  }.collectAsState(false)

  Icon(
    if (doNotDisturb) R.drawable.es_ic_do_not_disturb_on
    else R.drawable.es_ic_do_not_disturb_off
  )
}
