package com.ivianuu.essentials.gestures.action

import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.composition.installIn
import com.ivianuu.injekt.map
import com.ivianuu.injekt.set

@Module
fun EsActionModule() {
    installIn<ApplicationComponent>()
    set<Action>()
    set<ActionFactory>()
    set<ActionPickerDelegate>()
}
