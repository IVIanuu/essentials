package com.ivianuu.essentials.ui.navigation

import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.composition.installIn
import com.ivianuu.injekt.scoped

@Module
fun esNavigatorModule() {
    installIn<ApplicationComponent>()
    scoped { Navigator() }
}
