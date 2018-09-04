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

package com.ivianuu.essentials.ui.traveler.navigator

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.ivianuu.compass.CompassFragmentNavigatorHelper
import com.ivianuu.traveler.command.Command

/**
 * Fragment swapper navigator which uses keys
 */
class CompassFragmentSwapperNavigator(
    fm: FragmentManager,
    containerId: Int,
    hideStrategy: HideStrategy = HideStrategy.DETACH,
    swapOnReselection: Boolean = true
) : FragmentSwapperNavigator(fm, containerId, hideStrategy, swapOnReselection) {

    private val helper = CompassFragmentNavigatorHelper()

    override fun createFragment(key: Any, data: Any?) =
        helper.createFragment(key, data)

    override fun setupFragmentTransaction(
        command: Command,
        currentFragment: Fragment?,
        nextFragment: Fragment,
        transaction: FragmentTransaction
    ) {
        helper.setupFragmentTransaction(command, currentFragment, nextFragment, transaction)
    }

}