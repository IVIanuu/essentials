/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import com.ivianuu.essentials.AppConfig
import com.ivianuu.essentials.Resources
import com.ivianuu.essentials.compose.asFlow
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionAccessibilityPermission
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionId
import com.ivianuu.essentials.gestures.action.ActionRootPermission
import com.ivianuu.essentials.recentapps.CurrentApp
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.typeKeyOf
import kotlinx.coroutines.flow.first

@Provide object KillCurrentAppActionId : ActionId("kill_current_app")

@Provide fun killCurrentAppAction(resources: Resources) = Action(
  id = KillCurrentAppActionId,
  title = resources(R.string.es_action_kill_current_app),
  icon = staticActionIcon(Icons.Default.Clear),
  permissions = typeKeyOf<ActionAccessibilityPermission>() + typeKeyOf<ActionRootPermission>()
)

@Provide fun killCurrentAppActionExecutor(
  appConfig: AppConfig,
  currentApp: @Composable () -> CurrentApp?,
  packageManager: PackageManager,
  rootCommandRunner: ActionRootCommandRunner
) = ActionExecutor<KillCurrentAppActionId> {
  val currentApp = currentApp.asFlow().first()?.value
  if (currentApp != "android" &&
    currentApp != "com.android.systemui" &&
    currentApp != appConfig.packageName && // we have no suicidal intentions :D
    currentApp != getHomePackage(packageManager)
  ) {
    rootCommandRunner("am force-stop $currentApp")
  }
}

private fun getHomePackage(@Inject packageManager: PackageManager): String {
  val intent = Intent(Intent.ACTION_MAIN).apply {
    addCategory(Intent.CATEGORY_HOME)
  }
  return packageManager.resolveActivity(
    intent,
    PackageManager.MATCH_DEFAULT_ONLY
  )?.activityInfo?.packageName ?: ""
}
