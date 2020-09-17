package com.ivianuu.essentials.gestures.action

import com.ivianuu.essentials.datastore.DiskDataStoreFactory
import com.ivianuu.injekt.ApplicationContext
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.given

@Given(ApplicationContext::class)
class ActionPrefs {
    val actionMediaApp = given<DiskDataStoreFactory>().create<String?>("action_media_app") { null }
}
