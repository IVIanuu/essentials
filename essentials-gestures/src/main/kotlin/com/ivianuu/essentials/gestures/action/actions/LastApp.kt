/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.content.pm.PackageManager
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionId
import com.ivianuu.essentials.gestures.action.ActionSystemOverlayPermission
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.typeKeyOf

@Provide object LastAppActionId : ActionId("last_app")

context(ResourceProvider) @Provide fun lastAppAction() = Action(
  id = LastAppActionId,
  title = loadResource(R.string.es_action_last_app),
  permissions = accessibilityActionPermissions + typeKeyOf<ActionSystemOverlayPermission>(),
  unlockScreen = true,
  closeSystemDialogs = true,
  icon = staticActionIcon(R.drawable.es_ic_arrow_back)
)

context(ActionIntentSender, AppSwitchManager, AppContext, PackageManager)
    @Provide fun lastAppActionExecutor() = ActionExecutor<LastAppActionId> {
  lastApp()
    ?.let { switchToApp(it, R.anim.es_slide_in_right, R.anim.es_slide_out_right) }
}
