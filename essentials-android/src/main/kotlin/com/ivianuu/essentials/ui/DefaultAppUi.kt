/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui

import androidx.compose.ui.*
import com.ivianuu.essentials.ui.app.*
import com.ivianuu.essentials.ui.navigation.*

@Provide fun defaultAppUi(navigationStateContent: NavigationStateContent) = AppUi {
  navigationStateContent(Modifier)
}
