/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.tile

import android.content.*
import android.content.pm.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.app.*
import com.ivianuu.injekt.*

@Provide fun tileServiceDeclarationChecker(
  context: AppContext,
  packageManager: PackageManager,
  tileIds: List<TileId>
) = ScopeWorker<AppScope> {
  for (tileId in tileIds) {
    val intent = Intent(context, tileId.clazz.java)
    val resolveInfo = packageManager.queryIntentServices(intent, PackageManager.MATCH_DEFAULT_ONLY)
    if (resolveInfo.isEmpty())
      throw IllegalStateException("A model for tile ${tileId.clazz} was provided but not declared in the manifest")
  }
}
