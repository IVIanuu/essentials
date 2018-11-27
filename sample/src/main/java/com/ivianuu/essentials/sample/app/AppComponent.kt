package com.ivianuu.essentials.sample.app

import com.ivianuu.essentials.app.EssentialsAppModule
import com.ivianuu.essentials.app.glide.EssentialsAppGlideModule
import com.ivianuu.essentials.hidenavbar.EssentialsNavBarModule
import com.ivianuu.essentials.injection.EssentialsModule
import com.ivianuu.essentials.picker.EssentialsPickerModule
import com.ivianuu.essentials.sample.injection.ActivityBindingModule
import com.ivianuu.essentials.sample.injection.AssistedModule
import com.ivianuu.essentials.sample.injection.WorkerModule
import com.ivianuu.essentials.work.EssentialsWorkInitializerModule
import com.ivianuu.essentials.work.EssentialsWorkModule
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
        EssentialsModule::class,
        EssentialsAppModule::class,
        EssentialsAppGlideModule::class,
        EssentialsNavBarModule::class,
        EssentialsPickerModule::class,
        EssentialsWorkModule::class,
        EssentialsWorkInitializerModule::class,
        WorkerModule::class
    ]
)
interface AppComponent : Injector<App> {
    @Component.Builder
    abstract class Builder : Injector.Builder<App>()
}