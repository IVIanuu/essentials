package com.ivianuu.essentials.gestures.action

import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.composition.installIn
import com.ivianuu.injekt.map
import com.ivianuu.injekt.set

@Module
fun esActionModule() {
    installIn<ApplicationComponent>()
    map<String, Action>()
    set<ActionFactory>()
    set<ActionPickerDelegate>()
}
