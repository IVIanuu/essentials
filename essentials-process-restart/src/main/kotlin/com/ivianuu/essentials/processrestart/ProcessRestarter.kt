/*
 * Copyright 2020 Manuel Wrage
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

package com.ivianuu.essentials.processrestart

import android.content.Intent.*
import android.content.pm.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.android.*

typealias ProcessRestarter = suspend () -> Unit

@Provide fun processRestarter(
  buildInfo: BuildInfo,
  context: AppContext,
  logger: Logger,
  packageManager: PackageManager,
): ProcessRestarter = {
  val intent = packageManager.getLaunchIntentForPackage(buildInfo.packageName)!!
    .addFlags(FLAG_ACTIVITY_NEW_TASK)
  d { "restart process $intent" }
  ProcessRestartActivity.launch(context, intent)
  Runtime.getRuntime().exit(0)
}
