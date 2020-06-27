package com.ivianuu.essentials.gestures.action

import com.ivianuu.essentials.datastore.DiskDataStoreFactory
import com.ivianuu.injekt.ApplicationScoped

@ApplicationScoped
class ActionPrefs(factory: DiskDataStoreFactory) {
    val actionMediaApp = factory.create<String?>("action_media_app") { null }
}
