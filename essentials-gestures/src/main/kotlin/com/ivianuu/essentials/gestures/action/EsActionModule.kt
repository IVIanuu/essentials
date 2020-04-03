package com.ivianuu.essentials.gestures.action

import com.ivianuu.injekt.ApplicationScope
import com.ivianuu.injekt.ComponentBuilder
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.common.map
import com.ivianuu.injekt.common.set

@ApplicationScope
@Module
private fun ComponentBuilder.esActionModule() {
    map<String, Action>()
    set<ActionFactory>()
    set<ActionPickerDelegate>()
}
