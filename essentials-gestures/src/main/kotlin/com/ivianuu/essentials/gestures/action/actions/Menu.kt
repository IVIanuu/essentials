/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.gestures.*
import com.ivianuu.essentials.gestures.action.*

@Provide object MenuActionId : ActionId("menu")

@Provide fun menuAction(RP: ResourceProvider) = Action(
  id = MenuActionId,
  title = loadResource(R.string.es_action_menu),
  icon = staticActionIcon(Icons.Default.MoreVert),
  permissions = listOf(typeKeyOf<ActionRootPermission>())
)

@Provide fun menuActionExecutor(
  actionRootCommandRunner: ActionRootCommandRunner
) = ActionExecutor<MenuActionId> {
  actionRootCommandRunner("input keyevent 82")
}
