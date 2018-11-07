package com.ivianuu.essentials.sample.app

import com.ivianuu.essentials.injection.EssentialsModule
import com.ivianuu.essentials.sample.injection.ActivityBindingModule
import com.ivianuu.essentials.sample.injection.ViewModelModule
import com.ivianuu.essentials.sample.injection.WorkerModule
import dagger.Component
import dagger.android.AndroidInjector
import javax.inject.Singleton

/**
 * App component
 */
@Singleton
@Component(
    modules = [
        ActivityBindingModule::class,
        AppModule::class,
        EssentialsModule::class,
        ViewModelModule::class,
        WorkerModule::class
    ]
)
interface AppComponent : AndroidInjector<App> {
    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<App>()
}