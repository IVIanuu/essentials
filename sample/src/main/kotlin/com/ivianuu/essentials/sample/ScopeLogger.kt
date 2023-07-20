/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample

import com.ivianuu.essentials.Scope
import com.ivianuu.essentials.ScopeObserver
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.injekt.Provide

@Provide fun observer(logger: Logger): ScopeObserver = object : ScopeObserver {
  override fun onEnter(scope: Scope<*>) {
    logger.log { "${scope.name.value} on enter" }
  }

  override fun onExit(scope: Scope<*>) {
    logger.log { "${scope.name.value} on exit" }
  }

  override fun onEnterChild(scope: Scope<*>) {
    logger.log { "${scope.parent!!.name.value} on enter child ${scope.name.value}" }
  }

  override fun onExitChild(scope: Scope<*>) {
    logger.log { "${scope.parent!!.name.value} on exit child ${scope.name.value}" }
  }
}
