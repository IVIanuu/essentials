package com.ivianuu.essentials.gestures.action

import com.ivianuu.essentials.datastore.DiskDataStoreFactory
import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.given

@Given(ApplicationComponent::class)
class ActionPrefs {
    val actionMediaApp = given<DiskDataStoreFactory>().create<String?>("action_media_app") { null }
}
