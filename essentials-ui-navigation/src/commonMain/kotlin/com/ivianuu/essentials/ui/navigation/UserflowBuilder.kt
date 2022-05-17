/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.navigation

import com.ivianuu.essentials.app.LoadingOrder
import com.ivianuu.essentials.app.ScopeWorker
import com.ivianuu.essentials.app.sortedWithLoadingOrder
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.ui.UiScope
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Spread
import com.ivianuu.injekt.common.TypeKey

fun interface UserflowBuilder : suspend () -> List<Key<*>>

@Provide fun <@Spread T : UserflowBuilder> userflowBuilderElement(
  instance: T,
  key: TypeKey<T>,
  loadingOrder: LoadingOrder<T> = LoadingOrder()
) = UserflowBuilderElement(key, instance, loadingOrder as LoadingOrder<UserflowBuilder>)

data class UserflowBuilderElement(
  val key: TypeKey<UserflowBuilder>,
  val builder: UserflowBuilder,
  val loadingOrder: LoadingOrder<UserflowBuilder>
) {
  companion object {
    @Provide val treeDescriptor = object : LoadingOrder.Descriptor<UserflowBuilderElement> {
      override fun key(item: UserflowBuilderElement) = item.key
      override fun loadingOrder(item: UserflowBuilderElement) = item.loadingOrder
    }

    @Provide val defaultElements: Collection<UserflowBuilderElement> get() = emptyList()
  }
}

@Provide fun userflowBuilderWorker(
  elements: List<UserflowBuilderElement>,
  navigator: Navigator,
  L: Logger
) = ScopeWorker<UiScope> {
  val userflowKeys = elements
    .sortedWithLoadingOrder()
    .flatMap { it.builder() }

  log { "Userflow -> $userflowKeys" }

  if (userflowKeys.isEmpty()) return@ScopeWorker

  navigator.setBackStack(backStack = navigator.backStack.value + userflowKeys)
}
