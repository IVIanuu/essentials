/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.license.domain

import com.ivianuu.essentials.*
import com.ivianuu.essentials.license.data.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.coroutines.*
import kotlinx.coroutines.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*

interface LicenceProjectRepository {
  suspend fun getLicenseProjects(): Result<List<Project>, Throwable>
}

@Provide class LicenceProjectRepositoryImpl(
  private val context: AppContext,
  private val coroutineContext: IOContext,
  private val json: Json
) : LicenceProjectRepository{
  override suspend fun getLicenseProjects(): Result<List<Project>, Throwable> = withContext(coroutineContext) {
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
