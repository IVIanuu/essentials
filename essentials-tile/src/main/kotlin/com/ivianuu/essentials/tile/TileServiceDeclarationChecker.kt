/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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