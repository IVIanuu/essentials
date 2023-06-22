/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.license

import com.ivianuu.essentials.AppContext
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.IOCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

interface LicenceProjectRepository {
  val licenseProjects: Flow<List<Project>>
}

@Provide class LicenceProjectRepositoryImpl(
  private val appContext: AppContext,
  private val ioCoroutineContext: IOCoroutineContext,
  private val json: Json
) : LicenceProjectRepository {
  override val licenseProjects: Flow<List<Project>> = flow {
    emit(
      withContext(ioCoroutineContext) {
        appContext.resources.assets.open(LICENSE_JSON_FILE_NAME)
          .readBytes()
          .let { String(it) }
          .let { json.decodeFromString(it) }
      }
    )
  }

  private companion object {
    private const val LICENSE_JSON_FILE_NAME = "open_source_licenses.json"
  }
}
