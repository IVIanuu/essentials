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

package com.ivianuu.essentials.data

import com.ivianuu.injekt.*
import com.ivianuu.injekt.android.*
import java.io.*

typealias DataDir = File

@Provide fun dataDir(context: AppContext): DataDir =
  File(context.applicationInfo.dataDir)

typealias PrefsDir = File

@Provide fun prefsDir(dataDir: DataDir): PrefsDir = dataDir.resolve("prefs")

@Provide inline fun packageManager(context: AppContext) = context.packageManager!!
