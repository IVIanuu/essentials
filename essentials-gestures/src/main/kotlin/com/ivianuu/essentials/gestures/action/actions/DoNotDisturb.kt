package com.ivianuu.essentials.gestures.action.actions

import android.app.NotificationManager
import androidx.compose.material.Icon
import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionIcon
import com.ivianuu.essentials.gestures.action.ActionId
import com.ivianuu.essentials.gestures.action.ActionNotificationPolicyPermission
import com.ivianuu.essentials.loadResource
import com.ivianuu.essentials.state.valueFromFlow
import com.ivianuu.essentials.util.BroadcastsFactory
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Tag
import com.ivianuu.injekt.android.SystemService
import com.ivianuu.injekt.common.typeKeyOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

@Provide object DoNotDisturbAction : ActionId("do_not_disturb")

@Provide fun doNotDisturbAction(icon: DoNotDisturbIcon, RP: ResourceProvider) = Action(
  id = DoNotDisturbAction,
  title = loadResource(R.string.es_action_do_not_disturb),
  icon = icon,
  permissions = listOf(typeKeyOf<ActionNotificationPolicyPermission>())
)

@Provide fun doNotDisturbActionExecutor(
  notificationManager: @SystemService NotificationManager
): ActionExecutor<DoNotDisturbAction> = {
  notificationManager.setInterruptionFilter(
    if (notificationManager.currentInterruptionFilter != NotificationManager.INTERRUPTION_FILTER_PRIORITY)
      NotificationManager.INTERRUPTION_FILTER_PRIORITY else NotificationManager.INTERRUPTION_FILTER_ALL
  )
}

@Tag annotation class DoNotDisturbIconTag
typealias DoNotDisturbIcon = @DoNotDisturbIconTag ActionIcon

@Provide fun doNotDisturbIcon(
  broadcastsFactory: BroadcastsFactory,
  notificationManager: @SystemService NotificationManager,
): DoNotDisturbIcon = {
  val doNotDisturb = valueFromFlow(false) {
    broadcastsFactory(
      NotificationManager.ACTION_INTERRUPTION_FILTER_CHANGED
    )
      .onStart<Any?> { emit(Unit) }
      .map {
        notificationManager.currentInterruptionFilter == NotificationManager.INTERRUPTION_FILTER_PRIORITY
      }
  }

  Icon(
    if (doNotDisturb) R.drawable.es_ic_do_not_disturb_on
    else R.drawable.es_ic_do_not_disturb_off
  )
}