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

package com.ivianuu.essentials.util

import android.content.*
import android.content.pm.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.android.*
import kotlinx.coroutines.flow.*

typealias AppUiStarter = IntentAppUiStarter

@Provide fun appUiStarter(
  context: AppContext,
  buildInfo: BuildInfo,
  foregroundActivity: Flow<ForegroundActivity>,
  packageManager: PackageManager,
): AppUiStarter = {
  val intent = packageManager.getLaunchIntentForPackage(buildInfo.packageName)!!
  foregroundActivity
    .onStart {
      context.startActivity(
        intent.apply {
          addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
      )
    }
    .filterNotNull()
    .first()
}
