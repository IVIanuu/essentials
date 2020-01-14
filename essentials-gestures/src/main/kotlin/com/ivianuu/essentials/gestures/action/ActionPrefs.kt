package com.ivianuu.essentials.gestures.action

import com.ivianuu.essentials.store.prefs.PrefBoxFactory
import com.ivianuu.essentials.store.prefs.string
import com.ivianuu.injekt.Single
import com.ivianuu.injekt.android.ApplicationScope

@ApplicationScope
@Single
class ActionPrefs(factory: PrefBoxFactory) {
    val actionMediaApp = factory.string("action_media_app")
}