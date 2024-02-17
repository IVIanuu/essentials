/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui

import com.ivianuu.essentials.ui.app.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*

@Provide fun defaultAppUi(navigator: Navigator) = AppUi {
  NavigatorContent(navigator = navigator)
}
