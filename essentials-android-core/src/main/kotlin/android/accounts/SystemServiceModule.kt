package android.accounts

import android.content.Context
import com.ivianuu.injekt.Provide

object SystemServiceModule {
  @Provide fun accountManager(context: Context): AccountManager =
    context.getSystemService(AccountManager::class.java)
}
