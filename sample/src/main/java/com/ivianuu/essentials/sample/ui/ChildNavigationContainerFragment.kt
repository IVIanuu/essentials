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

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import com.ivianuu.daggerextensions.AutoContribute
import com.ivianuu.essentials.injection.FragmentBindingModule
import com.ivianuu.essentials.injection.PerFragment
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.ui.base.BaseFragment
import com.ivianuu.essentials.ui.traveler.key.FragmentClassKey
import com.ivianuu.essentials.ui.traveler.key.requireKey
import com.ivianuu.essentials.ui.traveler.setupKeyFragmentRouter
import com.ivianuu.essentials.util.analytics.IgnoreNamedScreen
import kotlinx.android.parcel.Parcelize

/**
 * @author Manuel Wrage (IVIanuu)
 */
@FragmentBindingModule
@PerFragment
@AutoContribute
class ChildNavigationContainerFragment : BaseFragment(), IgnoreNamedScreen {

    override val layoutRes = R.layout.fragment_child_navigation_container

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val key = requireKey<ChildNavigationContainerKey>()

        val router = setupKeyFragmentRouter(R.id.child_navigation_container)

        if (savedInstanceState == null) {
            router.replaceScreen(ChildNavigationKey(key.index, 1))
        }
    }
}

@Parcelize
data class ChildNavigationContainerKey(val index: Int) :
    FragmentClassKey(ChildNavigationContainerFragment::class), Parcelable