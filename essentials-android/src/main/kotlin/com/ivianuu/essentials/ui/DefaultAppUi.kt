/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui

import com.ivianuu.essentials.ui.app.AppUi
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.NavigatorContent
import com.ivianuu.essentials.ui.navigation.RootKey
import com.ivianuu.injekt.Provide

@Provide fun defaultAppUi(navigator: Navigator, rootKey: RootKey?) = AppUi {
  NavigatorContent(navigator = navigator)
}
