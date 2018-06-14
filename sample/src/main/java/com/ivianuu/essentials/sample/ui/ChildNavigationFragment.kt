/*
 * Copyright 2018 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.sample.ui

import com.ivianuu.compass.Destination
import com.ivianuu.daggerextensions.AutoContribute
import com.ivianuu.essentials.injection.FragmentBindingModule
import com.ivianuu.essentials.injection.PerFragment
import com.ivianuu.essentials.injection.ViewBindingModule_
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.ui.base.BaseFragment
import com.ivianuu.essentials.util.ext.localRouter
import dagger.Module
import dagger.Provides
import javax.inject.Qualifier

@Destination(ChildNavigationFragment::class)
data class ChildNavigationDestination(val index: Int, val count: Int)

/**
 * @author Manuel Wrage (IVIanuu)
 */
@FragmentBindingModule
@PerFragment
@AutoContribute(modules = [ChildNavigationModule::class, ViewBindingModule_::class])
class ChildNavigationFragment : BaseFragment() {
    override val layoutRes = R.layout.view_child_navigation
}

@Qualifier
annotation class LocalRouter

@Module
object ChildNavigationModule {

    @JvmStatic
    @LocalRouter
    @Provides
    fun provideLocalRouter(fragment: ChildNavigationFragment) = fragment.localRouter

    @JvmStatic
    @Provides
    fun provideDestination(fragment: ChildNavigationFragment) =
        fragment.childNavigationDestination()

}