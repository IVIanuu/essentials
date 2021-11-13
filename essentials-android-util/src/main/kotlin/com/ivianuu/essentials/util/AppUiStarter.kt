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

import android.content.Intent
import android.content.pm.PackageManager
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.BuildInfo
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Tag
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onStart

@Tag annotation class AppUiStarterTag
typealias AppUiStarter = @AppUiStarterTag com.ivianuu.essentials.ui.android.navigation.IntentAppUiStarter

@Provide fun intentAppUiStarter(appUiStarter: AppUiStarter): com.ivianuu.essentials.ui.android.navigation.IntentAppUiStarter = appUiStarter

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
