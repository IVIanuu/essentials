/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.content.pm.PackageManager
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.Resources
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionId
import com.ivianuu.essentials.gestures.action.ActionSystemOverlayPermission
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.typeKeyOf

@Provide object NextAppActionId : ActionId("next_app")

@Provide fun nextAppAction(resources: Resources) = Action(
  id = NextAppActionId,
  title = resources(R.string.es_action_next_app),
  permissions = accessibilityActionPermissions + typeKeyOf<ActionSystemOverlayPermission>(),
  unlockScreen = true,
  closeSystemDialogs = true,
  icon = staticActionIcon(R.drawable.es_ic_arrow_forward)
)

@Provide fun nextAppActionExecutor(
  appContext: AppContext,
  appSwitchManager: AppSwitchManager,
  intentSender: ActionIntentSender,
  packageManager: PackageManager
) = ActionExecutor<NextAppActionId> {
  appSwitchManager.nextApp()
    ?.let { switchToApp(it, R.anim.es_slide_in_left, R.anim.es_slide_out_left) }
}
