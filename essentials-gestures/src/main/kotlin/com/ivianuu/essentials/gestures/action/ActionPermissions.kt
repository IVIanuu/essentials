package com.ivianuu.essentials.gestures.action

import androidx.compose.foundation.Icon
import androidx.compose.ui.res.vectorResource
import com.ivianuu.essentials.accessibility.DefaultAccessibilityService
import com.ivianuu.essentials.app.androidApplicationContext
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.permission.Desc
import com.ivianuu.essentials.permission.Icon
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.Title
import com.ivianuu.essentials.permission.accessibility.AccessibilityServicePermission
import com.ivianuu.essentials.permission.root.RootPermission
import com.ivianuu.essentials.permission.withValue
import com.ivianuu.essentials.permission.writesecuresettings.WriteSecureSettingsPermission
import com.ivianuu.essentials.permission.writesettings.WriteSettingsPermission
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.common.ApplicationContext

@Given(ApplicationContext::class)
internal class ActionPermissions {
    val accessibility = AccessibilityServicePermission(
        DefaultAccessibilityService::class,
        Permission.Title withValue "Accessibility", // todo res
        Permission.Desc withValue "Required to click buttons", // todo res
        Permission.Icon withValue { Icon(vectorResource(R.drawable.es_ic_accessibility)) }
    )
    val root = RootPermission(
        Permission.Title withValue "Root", // todo res
        Permission.Icon withValue { Icon(vectorResource(R.drawable.es_ic_adb)) }
    )
    val writeSecureSettings = WriteSecureSettingsPermission(
        Permission.Title withValue "Write secure settings", // todo res
        Permission.Desc withValue "Required to change the navigation bar visibility", // todo res
        Permission.Icon withValue { Icon(vectorResource(R.drawable.es_ic_settings)) }
    )
    val writeSettings = WriteSettingsPermission(
        androidApplicationContext,
        Permission.Title withValue "Write Settings", // todo res
        Permission.Desc withValue "Required to change settings", // todo res
        Permission.Icon withValue { Icon(vectorResource(R.drawable.es_ic_settings)) } // todo change icon
    )
}
