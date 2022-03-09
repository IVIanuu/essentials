/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.content.pm.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.gestures.*
import com.ivianuu.essentials.gestures.action.*

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
