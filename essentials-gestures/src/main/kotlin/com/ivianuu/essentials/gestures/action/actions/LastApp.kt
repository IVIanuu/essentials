package com.ivianuu.essentials.gestures.action.actions

import android.content.pm.PackageManager
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionId
import com.ivianuu.essentials.gestures.action.ActionSystemOverlayPermission
import com.ivianuu.essentials.loadResource
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.typeKeyOf

@Provide object LastAppActionId : ActionId("last_app")

@Provide fun lastAppAction(RP: ResourceProvider) = Action(
  id = LastAppActionId,
  title = loadResource(R.string.es_action_last_app),
  permissions = accessibilityActionPermissions + typeKeyOf<ActionSystemOverlayPermission>(),
  unlockScreen = true,
  closeSystemDialogs = true,
  icon = staticActionIcon(R.drawable.es_ic_arrow_back)
)

@Provide fun lastAppActionExecutor(
  actionIntentSender: ActionIntentSender,
  appSwitchManager: AppSwitchManager,
  context: AppContext,
  packageManager: PackageManager
) = ActionExecutor<LastAppActionId> {
  appSwitchManager.lastApp()
    ?.let { switchToApp(it, R.anim.es_slide_in_right, R.anim.es_slide_out_right) }
}
