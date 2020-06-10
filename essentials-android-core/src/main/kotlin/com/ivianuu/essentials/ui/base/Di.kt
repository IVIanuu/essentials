package com.ivianuu.essentials.ui.base

import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.android.ActivityComponent
import com.ivianuu.injekt.composition.installIn
import com.ivianuu.injekt.scoped

@Module
fun esActivityModule() {
    installIn<ActivityComponent>()
    scoped { Navigator() }
}
