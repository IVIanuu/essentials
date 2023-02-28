/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.navigation

import com.ivianuu.essentials.app.ScopeWorker
import com.ivianuu.essentials.app.Service
import com.ivianuu.essentials.app.ServiceElement
import com.ivianuu.essentials.app.sortedWithLoadingOrder
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.ui.UiScope
import com.ivianuu.injekt.Provide

interface UserflowBuilder : suspend () -> List<Key<*>>, Service<UserflowBuilder>

@Provide fun userflowBuilderWorker(
  elements: List<ServiceElement<UserflowBuilder>>,
  logger: Logger,
  navigator: Navigator
) = ScopeWorker<UiScope> {
  val userflowKeys = elements
    .sortedWithLoadingOrder()
    .flatMap { it.instance() }

  logger.log { "Userflow -> $userflowKeys" }

  if (userflowKeys.isEmpty()) return@ScopeWorker

  navigator.setBackStack(backStack = navigator.backStack.value + userflowKeys)
}
