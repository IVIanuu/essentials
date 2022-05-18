/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.permission.writesecuresettings

import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.analytics.Analytics
import com.ivianuu.essentials.analytics.log

@Composable internal fun SecureSettingsHeader(text: String) {
  Text(
    text = text,
    style = MaterialTheme.typography.body2,
    modifier = Modifier.padding(all = 16.dp)
  )
}

internal fun Analytics.logPermissionRequestResult(
  adb: Boolean,
  success: Boolean,
  additionalParams: Map<String, String> = emptyMap()
) {
  log("write_secure_settings_permission_requested") {
    put("method", if (adb) "adb" else "root")
    put("granted", success.toString())
    putAll(additionalParams)
  }
}
