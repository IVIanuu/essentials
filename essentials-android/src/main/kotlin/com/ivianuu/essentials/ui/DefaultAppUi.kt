/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui

import androidx.compose.ui.Modifier
import com.ivianuu.essentials.ui.app.AppUi
import com.ivianuu.essentials.ui.navigation.NavigationStateContent
import com.ivianuu.injekt.Provide

@Provide fun defaultAppUi(navigationStateContent: NavigationStateContent) = AppUi {
  navigationStateContent(Modifier)
}
