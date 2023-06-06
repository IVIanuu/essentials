/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.xposed

import com.ivianuu.essentials.AppScope
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.Element

@Provide @Element<AppScope>
data class XposedHooksComponent(val hooks: () -> List<Hooks>)
