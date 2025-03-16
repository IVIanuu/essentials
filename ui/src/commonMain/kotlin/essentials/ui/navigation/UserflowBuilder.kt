/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.ui.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.util.*
import essentials.*
import essentials.logging.*
import essentials.ui.*
import injekt.*

fun interface UserflowBuilder : ExtensionPoint<UserflowBuilder> {
  suspend fun createUserflow(): List<Screen<*>>
}

@Provide @Composable fun UserflowBuilder(
  records: List<ExtensionPointRecord<UserflowBuilder>>,
  logger: Logger = inject,
  navigator: Navigator
): ScopeContent<UiScope> {
  LaunchedEffect(true) {
    val userflowScreens = records
      .sortedWithLoadingOrder()
      .fastFlatMap { it.instance.createUserflow() }

    d { "Userflow -> $userflowScreens" }

    if (userflowScreens.isEmpty()) return@LaunchedEffect

    navigator.setBackStack(backStack = navigator.backStack + userflowScreens)
  }
}
