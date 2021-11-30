/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.accessibility

import androidx.compose.runtime.*
import com.ivianuu.injekt.*

@Provide val accessibilityServiceRef = mutableStateOf<EsAccessibilityService?>(null)
