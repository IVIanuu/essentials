package com.ivianuu.essentials.gestures.action

import androidx.compose.foundation.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.Adb
import androidx.compose.material.icons.filled.SettingsApplications
import com.ivianuu.essentials.accessibility.DefaultAccessibilityService
import com.ivianuu.essentials.app.androidApplicationContext
import com.ivianuu.essentials.permission.Desc
import com.ivianuu.essentials.permission.Icon
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.Title
import com.ivianuu.essentials.permission.accessibility.AccessibilityServicePermission
import com.ivianuu.essentials.permission.root.RootPermission
import com.ivianuu.essentials.permission.withValue
import com.ivianuu.essentials.permission.writesecuresettings.WriteSecureSettingsPermission
import com.ivianuu.essentials.permission.writesettings.WriteSettingsPermission
import com.ivianuu.injekt.ApplicationContext
import com.ivianuu.injekt.Given

@Given(ApplicationContext::class)
internal class ActionPermissions {
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
        androidApplicationContext,
        Permission.Title withValue "Write Settings", // todo res
        Permission.Desc withValue "Required to change settings", // todo res
        Permission.Icon withValue { Icon(Icons.Default.SettingsApplications) } // todo change icon
    )
}
