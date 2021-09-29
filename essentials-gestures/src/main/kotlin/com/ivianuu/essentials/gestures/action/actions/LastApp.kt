package com.ivianuu.essentials.gestures.action.actions

import android.content.pm.PackageManager
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionId
import com.ivianuu.essentials.loadResource
import com.ivianuu.injekt.Provide

@Provide object LastAppActionId : ActionId("last_app")

@Provide fun lastAppAction(rp: ResourceProvider): Action<LastAppActionId> = Action(
  id = LastAppActionId,
  title = loadResource(R.string.es_action_last_app),
  permissions = accessibilityActionPermissions,
  unlockScreen = true,
  closeSystemDialogs = true,
  icon = singleActionIcon(R.drawable.es_ic_arrow_back)
)

@Provide fun lastAppActionExecutor(
  actionIntentSender: ActionIntentSender,
  appSwitchManager: AppSwitchManager,
  context: AppContext,
  packageManager: PackageManager
): ActionExecutor<LastAppActionId> = {
  appSwitchManager.lastApp()
    ?.let { switchToApp(it, R.anim.es_slide_in_right, R.anim.es_slide_out_right) }
}
