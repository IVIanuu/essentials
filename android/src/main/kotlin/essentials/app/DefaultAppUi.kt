/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.app

import androidx.compose.runtime.Composable
import essentials.ui.app.*
import essentials.ui.navigation.*
import injekt.*

@Provide @Composable fun DefaultAppUi(navigator: Navigator): AppUi {
  NavigatorContent(navigator = navigator)
}
