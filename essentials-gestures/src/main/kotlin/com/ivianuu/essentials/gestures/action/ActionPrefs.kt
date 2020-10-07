package com.ivianuu.essentials.gestures.action

import com.ivianuu.essentials.datastore.DiskDataStoreFactory
import com.ivianuu.injekt.Binding
import com.ivianuu.injekt.merge.ApplicationComponent

@Binding(ApplicationComponent::class)
class ActionPrefs(factory: DiskDataStoreFactory) {
    val actionMediaApp = factory.create<String?>("action_media_app") { null }
}
