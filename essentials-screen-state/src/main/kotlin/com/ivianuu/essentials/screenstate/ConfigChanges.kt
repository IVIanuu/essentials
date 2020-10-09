package com.ivianuu.essentials.screenstate

import android.content.ComponentCallbacks2
import android.content.res.Configuration
import com.ivianuu.essentials.coroutines.offerSafe
import com.ivianuu.injekt.FunBinding
import com.ivianuu.injekt.android.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

@FunBinding
fun configChanges(applicationContext: ApplicationContext): Flow<Unit> = callbackFlow {
    val callbacks = object : ComponentCallbacks2 {
        override fun onConfigurationChanged(newConfig: Configuration) {
            offerSafe(Unit)
        }

        override fun onLowMemory() {
        }

        override fun onTrimMemory(level: Int) {
        }
    }
    applicationContext.registerComponentCallbacks(callbacks)
    awaitClose { applicationContext.unregisterComponentCallbacks(callbacks) }
}
