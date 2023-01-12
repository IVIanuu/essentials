package android.app
import android.content.Context
import com.ivianuu.injekt.Provide

object SystemServiceModule {
  @Provide fun activityManager(context: Context): ActivityManager =
    context.getSystemService(ActivityManager::class.java)

  @Provide fun alarmManager(context: Context): AlarmManager =
    context.getSystemService(AlarmManager::class.java)

  @Provide fun downloadManager(context: Context): DownloadManager =
    context.getSystemService(DownloadManager::class.java)

  @Provide fun keyguardManager(context: Context): KeyguardManager =
    context.getSystemService(KeyguardManager::class.java)

  @Provide fun notificationManager(context: Context): NotificationManager =
    context.getSystemService(NotificationManager::class.java)

  @Provide fun searchManager(context: Context): SearchManager =
    context.getSystemService(SearchManager::class.java)

  @Provide fun uiModeManager(context: Context): UiModeManager =
    context.getSystemService(UiModeManager::class.java)

  @Provide fun wallpaperManager(context: Context): WallpaperManager =
    context.getSystemService(WallpaperManager::class.java)
}
