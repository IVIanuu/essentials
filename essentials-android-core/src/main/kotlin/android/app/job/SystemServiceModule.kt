package android.app.job

import android.content.Context
import com.ivianuu.injekt.Provide

object SystemServiceModule {
  @Provide inline fun jobScheduler(context: Context): JobScheduler =
    context.getSystemService(JobScheduler::class.java)
}
