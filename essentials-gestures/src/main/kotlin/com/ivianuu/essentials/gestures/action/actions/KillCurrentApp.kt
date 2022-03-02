/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.content.*
import android.content.pm.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.*
import com.ivianuu.essentials.recentapps.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*
import kotlinx.coroutines.flow.*

@Provide object KillCurrentAppActionId : ActionId("kill_current_app")

@Provide fun killCurrentAppAction(RP: ResourceProvider) = Action(
  id = KillCurrentAppActionId,
  title = loadResource(R.string.es_action_kill_current_app),
  icon = staticActionIcon(Icons.Default.Clear),
  permissions = typeKeyOf<ActionAccessibilityPermission>() + typeKeyOf<ActionRootPermission>()
)

@Provide fun killCurrentAppActionExecutor(
  actionRootCommandRunner: ActionRootCommandRunner,
  buildInfo: BuildInfo,
  currentAppFlow: Flow<CurrentApp?>,
  packageManager: PackageManager
) = ActionExecutor<KillCurrentAppActionId> {
  val currentApp = currentAppFlow.first()?.value
  if (currentApp != "android" &&
    currentApp != "com.android.systemui" &&
    currentApp != buildInfo.packageName && // we have no suicidal intentions :D
    currentApp != packageManager.getHomePackage()
  ) {
    actionRootCommandRunner("am force-stop $currentApp")
  }
}

private fun PackageManager.getHomePackage(): String {
  val intent = Intent(Intent.ACTION_MAIN).apply {
    addCategory(Intent.CATEGORY_HOME)
  }
  return resolveActivity(
    intent,
    PackageManager.MATCH_DEFAULT_ONLY
  )?.activityInfo?.packageName ?: ""
}
