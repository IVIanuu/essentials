/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import com.ivianuu.essentials.BuildInfo
import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionAccessibilityPermission
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionId
import com.ivianuu.essentials.gestures.action.ActionRootPermission
import com.ivianuu.essentials.recentapps.CurrentAppProvider
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.typeKeyOf
import kotlinx.coroutines.flow.first

@Provide object KillCurrentAppActionId : ActionId("kill_current_app")

context(ResourceProvider) @Provide fun killCurrentAppAction() = Action(
  id = KillCurrentAppActionId,
  title = loadResource(R.string.es_action_kill_current_app),
  icon = staticActionIcon(Icons.Default.Clear),
  permissions = typeKeyOf<ActionAccessibilityPermission>() + typeKeyOf<ActionRootPermission>()
)

context(ActionRootCommandRunner, BuildInfo, CurrentAppProvider, PackageManager)
    @Provide fun killCurrentAppActionExecutor() = ActionExecutor<KillCurrentAppActionId> {
  val currentApp = currentApp.first()
  if (currentApp != "android" &&
    currentApp != "com.android.systemui" &&
    currentApp != packageName && // we have no suicidal intentions :D
    currentApp != getHomePackage()
  ) {
    runActionRootCommand("am force-stop $currentApp")
  }
}

context(PackageManager) private fun getHomePackage(): String {
  val intent = Intent(Intent.ACTION_MAIN).apply {
    addCategory(Intent.CATEGORY_HOME)
  }
  return resolveActivity(
    intent,
    PackageManager.MATCH_DEFAULT_ONLY
  )?.activityInfo?.packageName ?: ""
}
