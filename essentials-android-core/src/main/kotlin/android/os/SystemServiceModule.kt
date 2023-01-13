/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package android.os

import android.content.Context
import com.ivianuu.injekt.Provide

object SystemServiceModule {
  @Provide inline fun batteryManager(context: Context): BatteryManager =
    context.getSystemService(BatteryManager::class.java)

  @Provide inline fun dropBoxManager(context: Context): DropBoxManager =
    context.getSystemService(DropBoxManager::class.java)

  @Provide inline fun powerManager(context: Context): PowerManager =
    context.getSystemService(PowerManager::class.java)

  @Provide inline fun userManager(context: Context): UserManager =
    context.getSystemService(UserManager::class.java)

  @Provide inline fun vibrator(context: Context): Vibrator =
    context.getSystemService(Vibrator::class.java)
}
