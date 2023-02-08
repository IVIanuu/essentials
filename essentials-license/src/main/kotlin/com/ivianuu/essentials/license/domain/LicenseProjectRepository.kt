/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.license.domain

import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.Result
import com.ivianuu.essentials.catch
import com.ivianuu.essentials.license.data.Project
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.coroutines.IOContext
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

interface LicenceProjectRepository {
  suspend fun getLicenseProjects(): Result<List<Project>, Throwable>
}

@Provide class LicenceProjectRepositoryImpl(
  private val appContext: AppContext,
  private val coroutineContext: IOContext,
  private val json: Json
) : LicenceProjectRepository{
  override suspend fun getLicenseProjects(): Result<List<Project>, Throwable> = withContext(coroutineContext) {
    catch {
      appContext.resources.assets.open(LICENSE_JSON_FILE_NAME)
        .readBytes()
        .let { String(it) }
        .let { json.decodeFromString(it) }
    }
  }

  private companion object {
    private const val LICENSE_JSON_FILE_NAME = "open_source_licenses.json"
  }
}
