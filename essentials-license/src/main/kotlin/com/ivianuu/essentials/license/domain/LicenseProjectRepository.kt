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

import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.Result
import com.ivianuu.essentials.catch
import com.ivianuu.essentials.license.data.Project
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.coroutines.IODispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

interface LicenceProjectRepository {
  suspend fun getLicenseProjects(): Result<List<Project>, Throwable>
}

@Provide class LicenceProjectRepositoryImpl(
  private val context: AppContext,
  private val dispatcher: IODispatcher,
  private val json: Json
) : LicenceProjectRepository{
  override suspend fun getLicenseProjects(): Result<List<Project>, Throwable> = withContext(dispatcher) {
    catch {
      context.resources.assets.open(LICENSE_JSON_FILE_NAME)
        .readBytes()
        .let { String(it) }
        .let { json.decodeFromString(it) }
    }
  }

  private companion object {
    private const val LICENSE_JSON_FILE_NAME = "open_source_licenses.json"
  }
}
