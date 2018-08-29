package com.ivianuu.essentials.ui.base

import com.ivianuu.essentials.injection.PerActivity
import com.ivianuu.essentials.ui.common.ActivityResultActivity
import com.ivianuu.essentials.ui.common.PermissionActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Essentials activity binding module
 */
@Module
abstract class EssentialsActivityBindingModule {

    @PerActivity
    @ContributesAndroidInjector
    abstract fun bindActivityResultActivity(): ActivityResultActivity

    @PerActivity
    @ContributesAndroidInjector
    abstract fun bindPermissionActivity(): PermissionActivity

}