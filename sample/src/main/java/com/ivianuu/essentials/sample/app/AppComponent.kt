package com.ivianuu.essentials.sample.app

import com.ivianuu.essentials.app.EsAppBindingModule
import com.ivianuu.essentials.app.glide.EsAppGlideModule
import com.ivianuu.essentials.hidenavbar.EsNavBarModule
import com.ivianuu.essentials.injection.EsModule
import com.ivianuu.essentials.picker.EsPickerModule
import com.ivianuu.essentials.sample.injection.ActivityBindingModule
import com.ivianuu.essentials.sample.injection.AssistedModule
import com.ivianuu.essentials.sample.injection.WorkerModule
import com.ivianuu.essentials.work.EsWorkInitializerModule
import com.ivianuu.essentials.work.EsWorkModule
import com.ivianuu.injectors.Injector
import dagger.Component
import javax.inject.Singleton

/**
 * App component
 */
@Singleton
@Component(
    modules = [
        ActivityBindingModule::class,
        AppModule::class,
        AssistedModule::class,
        EsModule::class,
        EsAppBindingModule::class,
        EsAppGlideModule::class,
        EsNavBarModule::class,
        EsPickerModule::class,
        EsWorkModule::class,
        EsWorkInitializerModule::class,
        WorkerModule::class
    ]
)
interface AppComponent : Injector<App> {
    @Component.Builder
    abstract class Builder : Injector.Builder<App>()
}