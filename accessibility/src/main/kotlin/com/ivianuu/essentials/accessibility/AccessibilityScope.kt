/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.accessibility

import com.ivianuu.essentials.Scope

object AccessibilityScope

val Scope<*>.accessibilityService: EsAccessibilityService
  get() = service()
