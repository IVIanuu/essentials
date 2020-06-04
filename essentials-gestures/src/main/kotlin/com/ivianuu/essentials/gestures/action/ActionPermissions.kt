package com.ivianuu.essentials.gestures.action

import android.content.Context
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Accessibility
import androidx.ui.material.icons.filled.Adb
import androidx.ui.material.icons.filled.SettingsApplications
import com.ivianuu.essentials.accessibility.ComponentAccessibilityService
import com.ivianuu.essentials.permission.Desc
import com.ivianuu.essentials.permission.Icon
import com.ivianuu.essentials.permission.Metadata
import com.ivianuu.essentials.permission.Title
import com.ivianuu.essentials.permission.accessibility.AccessibilityServicePermission
import com.ivianuu.essentials.permission.root.RootPermission
import com.ivianuu.essentials.permission.withValue
import com.ivianuu.essentials.permission.writesecuresettings.WriteSecureSettingsPermission
import com.ivianuu.essentials.permission.writesettings.WriteSettingsPermission
import com.ivianuu.essentials.ui.image.Icon
import com.ivianuu.injekt.ApplicationScoped
import com.ivianuu.injekt.ForApplication

@ApplicationScoped
internal class ActionPermissions(context: @ForApplication Context) {
    val accessibility = AccessibilityServicePermission(
        ComponentAccessibilityService::class,
        Metadata.Title withValue "Accessibility", // todo res
        Metadata.Desc withValue "Required to click buttons", // todo res
        Metadata.Icon withValue { Icon(Icons.Default.Accessibility) }
    )
    val root = RootPermission(
        Metadata.Title withValue "Root", // todo res
        Metadata.Icon withValue { Icon(Icons.Default.Adb) }
    )
    val writeSecureSettings = WriteSecureSettingsPermission(
        Metadata.Title withValue "Write secure settings", // todo res
        Metadata.Desc withValue "Required to change the navigation bar visibility", // todo res
        Metadata.Icon withValue { Icon(Icons.Default.SettingsApplications) }
    )
    val writeSettings = WriteSettingsPermission(
        context,
        Metadata.Title withValue "Write Settings", // todo res
        Metadata.Desc withValue "Required to change settings", // todo res
        Metadata.Icon withValue { Icon(Icons.Default.SettingsApplications) } // todo change icon
    )
}
