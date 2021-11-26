package com.ivianuu.essentials.gestures.action.ui.picker

import com.ivianuu.injekt.Provide

fun interface ActionFilter : (String) -> Boolean

@Provide val defaultActionFilter = ActionFilter { true }
