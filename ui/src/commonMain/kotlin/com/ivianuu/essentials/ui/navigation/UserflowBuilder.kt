/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.navigation

import androidx.compose.ui.util.fastFlatMap
import com.ivianuu.essentials.*
import com.ivianuu.essentials.app.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.essentials.ui.*
import com.ivianuu.injekt.*

fun interface UserflowBuilder : ExtensionPoint<UserflowBuilder> {
  suspend fun createUserflow(): List<Screen<*>>
}

@Provide fun userflowBuilderWorker(
  records: List<ExtensionPointRecord<UserflowBuilder>>,
  logger: Logger,
  navigator: Navigator
) = ScopeWorker<UiScope> {
  val userflowScreens = records
    .sortedWithLoadingOrder()
    .fastFlatMap { it.instance.createUserflow() }

  logger.d { "Userflow -> $userflowScreens" }

  if (userflowScreens.isEmpty()) return@ScopeWorker

  navigator.setBackStack(backStack = navigator.backStack + userflowScreens)
}
