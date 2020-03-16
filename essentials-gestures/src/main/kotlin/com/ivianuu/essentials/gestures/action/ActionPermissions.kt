package com.ivianuu.essentials.gestures.action

import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Accessibility
import androidx.ui.material.icons.filled.Adb
import androidx.ui.material.icons.filled.SettingsApplications
import com.ivianuu.essentials.accessibility.ComponentAccessibilityService
import com.ivianuu.essentials.permission.Desc
import com.ivianuu.essentials.permission.Icon
import com.ivianuu.essentials.permission.Metadata
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.Title
import com.ivianuu.essentials.permission.accessibility.AccessibilityServicePermission
import com.ivianuu.essentials.permission.root.RootPermission
import com.ivianuu.essentials.permission.withValue
import com.ivianuu.essentials.permission.writesecuresettings.WriteSecureSettingsPermission
import com.ivianuu.essentials.ui.image.Icon
import com.ivianuu.injekt.ApplicationScope
import com.ivianuu.injekt.Component
import com.ivianuu.injekt.Single

@ApplicationScope
@Single
internal class ActionPermissions {
    val accessibility = AccessibilityServicePermission(
        ComponentAccessibilityService::class,
        Metadata.Title withValue "Accessibility",
        Metadata.Desc withValue "Required to click buttons",
        Metadata.Icon withValue { Icon(Icons.Default.Accessibility) }
    )
    val root = RootPermission(
        Metadata.Title withValue "Root",
        Metadata.Icon withValue { Icon(Icons.Default.Adb) }
    )
    val secureSettings = WriteSecureSettingsPermission(
        Metadata.Title withValue "Write secure settings",
        Metadata.Desc withValue "Required to change the navigation bar visibility",
        Metadata.Icon withValue { Icon(Icons.Default.SettingsApplications) }
    )
}

internal inline fun Component.actionPermission(
    choosingBlock: ActionPermissions.() -> Permission
): Permission = get<ActionPermissions>().choosingBlock()
