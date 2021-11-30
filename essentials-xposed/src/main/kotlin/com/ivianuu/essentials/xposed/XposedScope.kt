/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.xposed

import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.Element

object XposedScope

@Provide @Element<XposedScope>
data class XposedHooksComponent(val hooks: () -> List<Hooks>)
