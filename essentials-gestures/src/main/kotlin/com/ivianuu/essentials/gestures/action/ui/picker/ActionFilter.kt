package com.ivianuu.essentials.gestures.action.ui.picker

import com.ivianuu.injekt.Provide

typealias ActionFilter = (String) -> Boolean

@Provide val defaultActionFilter: ActionFilter = { true }
