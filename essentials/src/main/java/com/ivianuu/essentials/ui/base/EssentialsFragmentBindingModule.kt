package com.ivianuu.essentials.ui.base

import com.ivianuu.essentials.injection.PerFragment
import com.ivianuu.essentials.ui.common.AppPickerDialog
import com.ivianuu.essentials.ui.common.TextInputDialog
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Essentials fragment binding module
 */
@Module
abstract class EssentialsFragmentBindingModule {

    @PerFragment
    @ContributesAndroidInjector
    abstract fun bindAppPickerDialog(): AppPickerDialog

    @PerFragment
    @ContributesAndroidInjector
    abstract fun bindTextInputDialog(): TextInputDialog

}