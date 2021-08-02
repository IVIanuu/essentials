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

package com.ivianuu.essentials.license.domain

import com.github.michaelbull.result.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.license.data.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.android.*
import com.ivianuu.injekt.coroutines.*
import kotlinx.coroutines.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*

typealias GetLicenseProjectsUseCase = suspend () -> Result<List<Project>, Throwable>

@Provide fun getLicenseProjectsUseCase(
  context: AppContext,
  dispatcher: IODispatcher,
  json: Json
): GetLicenseProjectsUseCase = {
  withContext(dispatcher) {
    catch {
      context.resources.assets.open(LICENSE_JSON_FILE_NAME)
        .readBytes()
        .let { String(it) }
        .let { json.decodeFromString(it) }
    }
  }
}

private const val LICENSE_JSON_FILE_NAME = "open_source_licenses.json"
