/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.tile

import android.content.Intent
import android.content.pm.PackageManager
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.app.ScopeWorker
import com.ivianuu.essentials.ui.navigation.Presenter
import com.ivianuu.injekt.Provide
import kotlin.reflect.KClass

@Provide fun tileServiceDeclarationChecker(
  appContext: AppContext,
  packageManager: PackageManager,
  tileBindings: List<Pair<KClass<AbstractEsTileService>, () -> Presenter<TileState<*>>>>
) = ScopeWorker<AppScope> {
  for ((tileClass) in tileBindings) {
    val intent = Intent(appContext, tileClass.java)
    val resolveInfo = packageManager.queryIntentServices(intent, PackageManager.MATCH_DEFAULT_ONLY)
    if (resolveInfo.isEmpty())
      throw IllegalStateException("A model for tile $tileClass was provided but not declared in the manifest")
  }
}
