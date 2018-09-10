package com.ivianuu.essentials.ui.base

import androidx.fragment.app.Fragment
import dagger.Binds
import dagger.Module

/**
 * @author Manuel Wrage (IVIanuu)
 */
@Module
abstract class BaseFragmentModule<T : BaseFragment> {

    @Binds
    abstract fun bindBaseFragment(t: T): BaseFragment

    @Binds
    abstract fun bindFragment(baseFragment: BaseFragment): Fragment
}