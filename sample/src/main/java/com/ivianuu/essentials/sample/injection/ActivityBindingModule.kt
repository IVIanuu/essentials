package com.ivianuu.essentials.sample.injection

import com.ivianuu.essentials.injection.PerActivity
import com.ivianuu.essentials.sample.ui.MainActivity
import com.ivianuu.essentials.sample.ui.MainActivityModule
import com.ivianuu.injectors.ContributesInjector
import dagger.Module

/**
 * Activity binding module
 */
@Module(includes = [ActivityBindingModule_Contributions::class])
abstract class ActivityBindingModule {

    @PerActivity
    @ContributesInjector(
        modules = [
            MainActivityModule::class,
            ControllerBindingModule::class
        ]
    )
    abstract fun bindMainActivity(): MainActivity

}