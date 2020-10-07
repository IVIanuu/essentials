package com.ivianuu.essentials.gestures.action.actions

import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionIcon
import com.ivianuu.essentials.gestures.action.choosePermissions
import com.ivianuu.essentials.util.Resources
import com.ivianuu.injekt.Assisted
import com.ivianuu.injekt.FunBinding

@FunBinding
fun accessibilityAction(
    choosePermissions: choosePermissions,
    performGlobalAction: performGlobalAction,
    resources: Resources,
    key: @Assisted String,
    accessibilityAction: @Assisted Int,
    titleRes: @Assisted Int,
    icon: @Assisted ActionIcon,
): Action = Action(
    key = key,
    title = resources.getString(titleRes),
    icon = icon,
    permissions = choosePermissions { listOf(accessibility) },
    execute = {
        performGlobalAction(accessibilityAction)
    }
)
