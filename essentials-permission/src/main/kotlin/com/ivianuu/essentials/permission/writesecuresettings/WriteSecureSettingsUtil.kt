/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.permission.writesecuresettings

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*

@Composable internal fun SecureSettingsHeader(text: String) {
  Text(
    text = text,
    style = MaterialTheme.typography.body2,
    modifier = Modifier.padding(all = 16.dp)
  )
}
