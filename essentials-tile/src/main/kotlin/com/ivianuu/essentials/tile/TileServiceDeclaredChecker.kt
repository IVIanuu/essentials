package com.ivianuu.essentials.tile

import android.content.Intent
import android.content.pm.PackageManager
import com.ivianuu.essentials.app.ScopeWorker
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.android.AppContext
import com.ivianuu.injekt.scope.AppScope

@Provide
fun tileServiceDeclaredChecker(
        context: AppContext,
        packageManager: PackageManager,
        tileIds: Set<TileId> = emptySet()
): ScopeWorker<AppScope> = {
  tileIds.forEach { tileId ->
    val intent = Intent(context, tileId.clazz.java)
    val resolveInfo = packageManager.queryIntentServices(intent, PackageManager.MATCH_DEFAULT_ONLY)
    if (resolveInfo.isEmpty()) {
      throw IllegalStateException("A model for tile ${tileId.clazz} was provided but not declared in the manifest")
    }
  }
}
