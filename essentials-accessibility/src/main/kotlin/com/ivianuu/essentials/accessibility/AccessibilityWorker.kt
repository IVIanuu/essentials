package com.ivianuu.essentials.accessibility

import com.ivianuu.injekt.Module
import com.ivianuu.injekt.Qualifier
import com.ivianuu.injekt.android.ServiceComponent
import com.ivianuu.injekt.composition.BindingEffect
import com.ivianuu.injekt.composition.installIn
import com.ivianuu.injekt.map
import com.ivianuu.injekt.set
import kotlin.reflect.KClass

/**
 * Runs while a accessiblity service is alive
 */
@BindingEffect(ServiceComponent::class)
annotation class AccessibilityWorker {
    companion object {
        @Module
        operator fun <T : suspend () -> Unit> invoke() {
            set<@AccessibilityWorker Set<suspend () -> Unit>, suspend () -> Unit> {
                add<T>()
            }
        }
    }
}

@Target(AnnotationTarget.TYPE)
@Qualifier
annotation class AccessibilityWorkers

@Module
fun EsNotificationWorkerModule() {
    installIn<ServiceComponent>()
    set<@AccessibilityWorkers Set<suspend () -> Unit>, suspend () -> Unit>()
}