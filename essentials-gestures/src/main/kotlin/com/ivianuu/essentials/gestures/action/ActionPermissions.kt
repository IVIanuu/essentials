package com.ivianuu.essentials.gestures.action

import android.content.Context
import androidx.ui.foundation.Icon
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Accessibility
import androidx.ui.material.icons.filled.Adb
import androidx.ui.material.icons.filled.SettingsApplications
import com.ivianuu.essentials.accessibility.DefaultAccessibilityService
import com.ivianuu.essentials.permission.Desc
import com.ivianuu.essentials.permission.Icon
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.Title
import com.ivianuu.essentials.permission.accessibility.AccessibilityServicePermission
import com.ivianuu.essentials.permission.root.RootPermission
import com.ivianuu.essentials.permission.withValue
import com.ivianuu.essentials.permission.writesecuresettings.WriteSecureSettingsPermission
import com.ivianuu.essentials.permission.writesettings.WriteSettingsPermission
import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.ForApplication
import com.ivianuu.injekt.Scoped

@Scoped(ApplicationComponent::class)
internal class ActionPermissions(context: @ForApplication Context) {
    val accessibility = AccessibilityServicePermission(
        DefaultAccessibilityService::class,
        Permission.Title withValue "Accessibility", // todo res
        Permission.Desc withValue "Required to click buttons", // todo res
        Permission.Icon withValue { Icon(Icons.Default.Accessibility) }
    )
    val root = RootPermission(
        Permission.Title withValue "Root", // todo res
        Permission.Icon withValue { Icon(Icons.Default.Adb) }
    )
    val writeSecureSettings = WriteSecureSettingsPermission(
        Permission.Title withValue "Write secure settings", // todo res
        Permission.Desc withValue "Required to change the navigation bar visibility", // todo res
        Permission.Icon withValue { Icon(Icons.Default.SettingsApplications) }
    )
    val writeSettings = WriteSettingsPermission(
        context,
        Permission.Title withValue "Write Settings", // todo res
        Permission.Desc withValue "Required to change settings", // todo res
        Permission.Icon withValue { Icon(Icons.Default.SettingsApplications) } // todo change icon
    )
}
