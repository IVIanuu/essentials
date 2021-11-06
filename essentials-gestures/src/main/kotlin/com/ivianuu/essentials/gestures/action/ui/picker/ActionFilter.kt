package com.ivianuu.essentials.gestures.action.ui.picker

import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Tag

@Tag annotation class ActionFilterTag
typealias ActionFilter = @ActionFilterTag (String) -> Boolean

@Provide val defaultActionFilter: ActionFilter = { true }
