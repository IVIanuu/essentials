package com.ivianuu.essentials.gestures.action

import com.ivianuu.essentials.accessibility.ComponentAccessibilityService
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.filled.Accessibility
import com.ivianuu.essentials.material.icons.filled.Adb
import com.ivianuu.essentials.material.icons.filled.SettingsApplications
import com.ivianuu.essentials.permission.Desc
import com.ivianuu.essentials.permission.Icon
import com.ivianuu.essentials.permission.Metadata
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.Title
import com.ivianuu.essentials.permission.accessibility.AccessibilityServicePermission
import com.ivianuu.essentials.permission.root.RootPermission
import com.ivianuu.essentials.permission.withValue
import com.ivianuu.essentials.permission.writesecuresettings.WriteSecureSettingsPermission
import com.ivianuu.essentials.ui.painter.VectorRenderable
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
        Metadata.Title withValue "Accessibility",
        Metadata.Desc withValue "Required to click buttons",
        Metadata.Icon withValue VectorRenderable(Icons.Default.Accessibility)
    )
    val root = RootPermission(
        Metadata.Title withValue "Root",
        Metadata.Icon withValue VectorRenderable(Icons.Default.Adb)
    )
    val secureSettings = WriteSecureSettingsPermission(
        Metadata.Title withValue "Write secure settings",
        Metadata.Desc withValue "Required to change the navigation bar visibility",
        Metadata.Icon withValue VectorRenderable(Icons.Default.SettingsApplications)
    )
}

internal inline fun DefinitionContext.actionPermission(
    choosingBlock: ActionPermissions.() -> Permission
): Permission = get<ActionPermissions>().choosingBlock()
