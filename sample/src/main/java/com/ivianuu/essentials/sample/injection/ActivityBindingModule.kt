package com.ivianuu.essentials.sample.injection

import com.ivianuu.essentials.injection.PerActivity
import com.ivianuu.essentials.sample.ui.MainActivity
import com.ivianuu.essentials.sample.ui.MainActivityModule
import com.ivianuu.essentials.ui.base.EssentialsActivityModule
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
            EssentialsActivityModule::class,
            FragmentBindingModule::class,
            MainActivityModule::class
        ]
    )
    abstract fun bindMainActivity(): MainActivity

}