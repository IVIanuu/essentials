package com.ivianuu.essentials.gestures.action

import com.ivianuu.essentials.accessibility.ComponentAccessibilityService
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.permission.Desc
import com.ivianuu.essentials.permission.Icon
import com.ivianuu.essentials.permission.Metadata
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.Title
import com.ivianuu.essentials.permission.accessibility.AccessibilityServicePermission
import com.ivianuu.essentials.permission.root.RootPermission
import com.ivianuu.essentials.permission.with
import com.ivianuu.essentials.util.ResourceProvider
import com.ivianuu.injekt.DefinitionContext
import com.ivianuu.injekt.Single
import com.ivianuu.injekt.android.ApplicationScope
import com.ivianuu.injekt.get

@ApplicationScope
@Single
internal class ActionPermissions(resourceProvider: ResourceProvider) {
    val accessibility = AccessibilityServicePermission(
        ComponentAccessibilityService::class,
        Metadata.Title with "Accessibility",
        Metadata.Desc with "Required to click buttons",
        Metadata.Icon with resourceProvider.getDrawable(R.drawable.es_ic_accessibility)
    )
    val root = RootPermission(
        Metadata.Title with "Root",
        Metadata.Icon with resourceProvider.getDrawable(R.drawable.es_ic_adb)
    )
}

internal inline fun DefinitionContext.actionPermission(
    choosingBlock: ActionPermissions.() -> Permission
): Permission = get<ActionPermissions>().choosingBlock()
