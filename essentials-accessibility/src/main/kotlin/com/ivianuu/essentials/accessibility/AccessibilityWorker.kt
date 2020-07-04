package com.ivianuu.essentials.accessibility

import com.ivianuu.injekt.Module
import com.ivianuu.injekt.android.ServiceComponent
import com.ivianuu.injekt.composition.BindingEffect
import com.ivianuu.injekt.composition.installIn
import com.ivianuu.injekt.map
import kotlin.reflect.KClass

/**
 * Runs while an accessibility service is alive
 */
interface AccessibilityWorker {
    suspend fun run()
}

@BindingEffect(ServiceComponent::class)
annotation class BindAccessibilityWorker {
    companion object {
        @Module
        inline operator fun <reified T : AccessibilityWorker> invoke() {
            map<KClass<*>, AccessibilityWorker> {
                put<T>(T::class)
            }
        }
    }
}

@Module
fun esAccessibilityRunnableModule() {
    installIn<ServiceComponent>()
    map<KClass<*>, AccessibilityWorker>()
}
