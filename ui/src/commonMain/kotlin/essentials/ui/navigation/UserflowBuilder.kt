/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.ui.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.util.*
import essentials.*
import injekt.*

fun interface UserflowBuilder : ExtensionPoint<UserflowBuilder> {
  suspend fun createUserflow(): List<Screen<*>>
}

@Provide @Composable fun UserflowBuilder(
  builders: List<UserflowBuilder>,
  logger: Logger = inject,
  navigator: Navigator
): ScopeContent<UiScope> {
  LaunchedEffect(true) {
    val userflowScreens = builders
      .fastFlatMap { it.createUserflow() }

    d { "Userflow -> $userflowScreens" }

    if (userflowScreens.isEmpty()) return@LaunchedEffect

    navigator.newBackStack(backStack = navigator.backStack + userflowScreens)
  }
}
