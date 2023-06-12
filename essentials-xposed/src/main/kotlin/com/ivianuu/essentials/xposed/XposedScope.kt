/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.xposed

import com.ivianuu.essentials.Service
import com.ivianuu.injekt.Provide

object XposedScope

@Provide @Service<XposedScope>
data class XposedHooksComponent(val hooks: () -> List<Hooks>)
