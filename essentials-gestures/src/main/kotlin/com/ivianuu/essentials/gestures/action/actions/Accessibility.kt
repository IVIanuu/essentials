package com.ivianuu.essentials.gestures.action.actions

import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionIcon
import com.ivianuu.essentials.gestures.action.permissions
import com.ivianuu.essentials.util.Resources
import com.ivianuu.injekt.Reader

@Reader
internal fun accessibilityAction(
    key: String,
    accessibilityAction: Int,
    titleRes: Int,
    icon: ActionIcon
) = Action(
    key = key,
    title = Resources.getString(titleRes),
    icon = icon,
    permissions = permissions { listOf(accessibility) },
    execute = {
        performGlobalAction(accessibilityAction)
    }
)
