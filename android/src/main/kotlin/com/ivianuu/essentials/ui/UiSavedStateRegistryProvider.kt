/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.saveable.LocalSaveableStateRegistry
import androidx.compose.runtime.saveable.SaveableStateRegistry
import com.ivianuu.injekt.Provide

@Provide val savableStateRegistryProvider = AppUiDecorator { content ->
  CompositionLocalProvider(
    LocalSaveableStateRegistry provides SaveableStateRegistry(emptyMap()) { true },
    content = content
  )
}
