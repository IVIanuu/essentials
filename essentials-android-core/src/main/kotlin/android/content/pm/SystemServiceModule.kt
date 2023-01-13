/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package android.content.pm

import android.content.Context
import com.ivianuu.injekt.Provide

object SystemServiceModule {
  @Provide inline fun launcherApps(context: Context): LauncherApps =
    context.getSystemService(LauncherApps::class.java)

  @Provide inline fun packageManager(context: Context): PackageManager =
    context.packageManager
}
