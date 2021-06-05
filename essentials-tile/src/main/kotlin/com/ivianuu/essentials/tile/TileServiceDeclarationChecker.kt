package com.ivianuu.essentials.tile

import android.content.*
import android.content.pm.*
import com.ivianuu.essentials.app.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.android.*
import com.ivianuu.injekt.scope.*

@Provide fun tileServiceDeclarationChecker(
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
