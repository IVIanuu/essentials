/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.ui.picker

import com.ivianuu.injekt.*

fun interface ActionFilter : (String) -> Boolean

@Provide val defaultActionFilter = ActionFilter { true }
