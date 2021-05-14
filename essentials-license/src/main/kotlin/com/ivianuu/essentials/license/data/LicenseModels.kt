package com.ivianuu.essentials.license.data

import kotlinx.serialization.*

@Serializable data class Project(
  @SerialName("project") val project: String,
  @SerialName("description") val description: String?,
  @SerialName("licenses") val licenses: List<License>
)

@Serializable data class License(
  @SerialName("license") val license: String,
  @SerialName("license_url") val url: String
)
