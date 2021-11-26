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
import androidx.compose.runtime.Composable
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.BuildInfo
import com.ivianuu.essentials.state.asComposedFlow
import com.ivianuu.essentials.ui.navigation.IntentAppUiStarter
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first

fun interface AppUiStarter : IntentAppUiStarter

@Provide fun intentAppUiStarter(appUiStarter: AppUiStarter): IntentAppUiStarter = appUiStarter

@Provide fun appUiStarter(
  context: AppContext,
  buildInfo: BuildInfo,
  foregroundActivity: @Composable () -> ForegroundActivity,
  packageManager: PackageManager,
) = AppUiStarter {
  val intent = packageManager.getLaunchIntentForPackage(buildInfo.packageName)!!
  context.startActivity(
    intent.apply {
      addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
  )

  foregroundActivity.asComposedFlow().filterNotNull().first()
}
