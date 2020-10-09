package com.ivianuu.essentials.gestures.action

import com.ivianuu.essentials.datastore.DataStore
import com.ivianuu.essentials.datastore.DiskDataStoreFactory
import com.ivianuu.injekt.Binding
import com.ivianuu.injekt.merge.ApplicationComponent

typealias ActionMediaAppPref = DataStore<String?>

@Binding(ApplicationComponent::class)
fun actionMediaAppPref(factory: DiskDataStoreFactory): ActionMediaAppPref =
    factory.create<String?>("action_media_app") { null }