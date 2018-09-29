package com.ivianuu.essentials.sample.injection

import com.ivianuu.essentials.injection.PerActivity
import com.ivianuu.essentials.sample.ui.MainActivity
import com.ivianuu.essentials.sample.ui.MainActivityModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Activity binding module
 */
@Module
abstract class ActivityBindingModule {

    @PerActivity
    @ContributesAndroidInjector(
        modules = [
            MainActivityModule::class,
            ControllerBindingModule_Contributions::class
        ]
    )
    abstract fun bindMainActivity(): MainActivity

}