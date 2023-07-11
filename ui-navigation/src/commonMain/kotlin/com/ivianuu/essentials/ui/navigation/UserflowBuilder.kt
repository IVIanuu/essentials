/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.navigation

import com.ivianuu.essentials.app.ExtensionPoint
import com.ivianuu.essentials.app.ExtensionPointRecord
import com.ivianuu.essentials.app.ScopeWorker
import com.ivianuu.essentials.app.sortedWithLoadingOrder
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.ui.UiScope
import com.ivianuu.injekt.Provide

interface UserflowBuilder : suspend () -> List<Screen<*>>, ExtensionPoint<UserflowBuilder>

@Provide fun userflowBuilderWorker(
  records: List<ExtensionPointRecord<UserflowBuilder>>,
  logger: Logger,
  navigator: Navigator
) = ScopeWorker<UiScope> {
  val userflowScreens = records
    .sortedWithLoadingOrder()
    .flatMap { it.instance() }

  logger.log { "Userflow -> $userflowScreens" }

  if (userflowScreens.isEmpty()) return@ScopeWorker

  navigator.setBackStack(backStack = navigator.backStack.value + userflowScreens)
}
