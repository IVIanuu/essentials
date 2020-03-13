package com.ivianuu.essentials.gestures.action

import com.ivianuu.essentials.store.android.prefs.PrefBoxFactory
import com.ivianuu.injekt.ApplicationScope
import com.ivianuu.injekt.Single

@ApplicationScope
@Single
class ActionPrefs(factory: PrefBoxFactory) {
    val actionMediaApp = factory.create<String?>("action_media_app", null)
}
