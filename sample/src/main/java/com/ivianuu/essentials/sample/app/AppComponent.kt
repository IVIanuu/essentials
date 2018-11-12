package com.ivianuu.essentials.sample.app

import com.ivianuu.essentials.app.EssentialsAppModule
import com.ivianuu.essentials.app.glide.EssentialsAppGlideModule
import com.ivianuu.essentials.injection.EssentialsModule
import com.ivianuu.essentials.picker.EssentialsPickerModule
import com.ivianuu.essentials.sample.injection.ActivityBindingModule
import com.ivianuu.essentials.sample.injection.ViewModelModule
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
        EssentialsAppModule::class,
        EssentialsAppGlideModule::class,
        EssentialsPickerModule::class,
        ViewModelModule::class
    ]
)
interface AppComponent : AndroidInjector<App> {
    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<App>()
}