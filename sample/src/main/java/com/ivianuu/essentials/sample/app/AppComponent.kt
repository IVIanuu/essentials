package com.ivianuu.essentials.sample.app

import com.ivianuu.essentials.app.EssentialsAppModule
import com.ivianuu.essentials.app.glide.EssentialsAppGlideModule
import com.ivianuu.essentials.injection.EssentialsModule
import com.ivianuu.essentials.picker.EssentialsPickerModule
import com.ivianuu.essentials.sample.injection.ViewModelModule
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
        ActivityBindingModule_Contributions::class,
        AppModule::class,
    //    AssistedModule_AssistedModule::class,
        EssentialsModule::class,
        EssentialsAppModule::class,
        EssentialsAppGlideModule::class,
        EssentialsPickerModule::class,
        EssentialsWorkModule::class,
        EssentialsWorkInitializerModule::class,
        ViewModelModule::class,
        WorkerModule::class
    ]
)
interface AppComponent : Injector<App> {
    @Component.Builder
    abstract class Builder : Injector.Builder<App>()
}