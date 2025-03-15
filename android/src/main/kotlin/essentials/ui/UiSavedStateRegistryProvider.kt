/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.ui

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.*
import essentials.ui.app.*
import injekt.*

data object SaveableStateRegistryProvider
@Provide @Composable fun ProvideSaveableStateRegistry(
  content: @Composable () -> Unit
): AppUiDecoration<SaveableStateRegistryProvider> {
  CompositionLocalProvider(
    LocalSaveableStateRegistry provides SaveableStateRegistry(emptyMap()) { true },
    content = content
  )
}
