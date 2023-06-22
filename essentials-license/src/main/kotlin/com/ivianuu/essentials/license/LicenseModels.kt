/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.license

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable data class Project(
  @SerialName("project") val project: String,
  @SerialName("description") val description: String?,
  @SerialName("licenses") val licenses: List<License>,
  @SerialName("url") val url: String?
)

@Serializable data class License(
  @SerialName("license") val license: String,
  @SerialName("license_url") val url: String
)
