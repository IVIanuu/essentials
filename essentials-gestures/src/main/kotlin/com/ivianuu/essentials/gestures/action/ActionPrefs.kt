package com.ivianuu.essentials.gestures.action

import com.ivianuu.essentials.store.android.prefs.PrefBoxFactory
import com.ivianuu.injekt.ApplicationScoped

@ApplicationScoped
class ActionPrefs(factory: PrefBoxFactory) {
    val actionMediaApp = factory.create<String?>("action_media_app", null)
}
