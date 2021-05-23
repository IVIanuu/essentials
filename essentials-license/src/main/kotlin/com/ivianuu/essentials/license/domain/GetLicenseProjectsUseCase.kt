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
  dispatcher: IODispatcher,
  json: Json,
  resources: AppResources
): GetLicenseProjectsUseCase = {
  withContext(dispatcher) {
    catch {
      resources.assets.open(LICENSE_JSON_FILE_NAME)
        .readBytes()
        .let { String(it) }
        .let { json.decodeFromString(it) }
    }
  }
}

private const val LICENSE_JSON_FILE_NAME = "open_source_licenses.json"
