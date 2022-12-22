/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionId
import com.ivianuu.essentials.gestures.action.ActionRootPermission
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.typeKeyOf

@Provide object MenuActionId : ActionId("menu")

context(ResourceProvider) @Provide fun menuAction() = Action(
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
