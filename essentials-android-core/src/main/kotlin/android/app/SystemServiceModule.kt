/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package android.app

import android.content.Context
import com.ivianuu.injekt.Provide

object SystemServiceModule {
  @Provide inline fun activityManager(context: Context): ActivityManager =
    context.getSystemService(ActivityManager::class.java)

  @Provide inline fun alarmManager(context: Context): AlarmManager =
    context.getSystemService(AlarmManager::class.java)

  @Provide inline fun appOpsManager(context: Context): AppOpsManager =
    context.getSystemService(AppOpsManager::class.java)

  @Provide inline fun downloadManager(context: Context): DownloadManager =
    context.getSystemService(DownloadManager::class.java)

  @Provide inline fun keyguardManager(context: Context): KeyguardManager =
    context.getSystemService(KeyguardManager::class.java)

  @Provide inline fun notificationManager(context: Context): NotificationManager =
    context.getSystemService(NotificationManager::class.java)

  @Provide inline fun searchManager(context: Context): SearchManager =
    context.getSystemService(SearchManager::class.java)

  @Provide inline fun uiModeManager(context: Context): UiModeManager =
    context.getSystemService(UiModeManager::class.java)
  @Provide inline fun wallpaperManager(context: Context): WallpaperManager =
    context.getSystemService(WallpaperManager::class.java)
}
