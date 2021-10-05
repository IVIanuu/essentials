package com.ivianuu.essentials.gestures.action.actions

import android.app.NotificationManager
import androidx.compose.material.Icon
import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.essentials.broadcast.BroadcastsFactory
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionIcon
import com.ivianuu.essentials.gestures.action.ActionId
import com.ivianuu.essentials.loadResource
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.android.SystemService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

@Provide object DoNotDisturbAction : ActionId("do_not_disturb")

@Provide fun doNotDisturbAction(
  icon: Flow<DoNotDisturbIcon>,
  rp: ResourceProvider
): Action<DoNotDisturbAction> = Action(
  id = DoNotDisturbAction,
  title = loadResource(R.string.es_action_do_not_disturb),
  icon = icon
)

@Provide fun lastAppNativeActionExecutor(
  notificationManager: @SystemService NotificationManager
): ActionExecutor<DoNotDisturbAction> = {
  notificationManager.setInterruptionFilter(
    if (notificationManager.currentInterruptionFilter != NotificationManager.INTERRUPTION_FILTER_PRIORITY)
      NotificationManager.INTERRUPTION_FILTER_PRIORITY else NotificationManager.INTERRUPTION_FILTER_ALL
  )
}

typealias DoNotDisturbIcon = ActionIcon

@Provide fun doNotDisturbIcon(
  broadcastsFactory: BroadcastsFactory,
  notificationManager: @SystemService NotificationManager,
): Flow<DoNotDisturbIcon> = broadcastsFactory(
  NotificationManager.ACTION_INTERRUPTION_FILTER_CHANGED
)
  .map { Unit }
  .onStart { emit(Unit) }
  .map {
    notificationManager.currentInterruptionFilter == NotificationManager.INTERRUPTION_FILTER_PRIORITY
  }
  .map {
    if (it) R.drawable.es_ic_do_not_disturb_on
    else R.drawable.es_ic_do_not_disturb_off
  }
  .distinctUntilChanged()
  .map {
    {
      Icon(it)
    }
  }

