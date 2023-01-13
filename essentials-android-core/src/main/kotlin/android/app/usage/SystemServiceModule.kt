package android.app.usage

import android.content.Context
import com.ivianuu.injekt.Provide

object SystemServiceModule {
  @Provide inline fun usageStatsManager(context: Context): UsageStatsManager =
    context.getSystemService(UsageStatsManager::class.java)
}
