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
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.ivianuu.compass.fragment.CompassFragmentKey
import com.ivianuu.compass.fragment.fragmentDetourOrNull
import com.ivianuu.compass.fragment.fragmentOrNull
import com.ivianuu.traveler.Command
import com.ivianuu.traveler.Forward
import com.ivianuu.traveler.Replace

/**
 * Fragment swapper navigator which uses keys
 */
class CompassFragmentSwapperNavigator(
    fm: FragmentManager,
    containerId: Int,
    hideStrategy: HideStrategy = HideStrategy.DETACH,
    swapOnReselection: Boolean = true
) : FragmentSwapperNavigator(fm, containerId, hideStrategy, swapOnReselection) {

    override fun createFragment(key: Any, data: Any?): Fragment? {
        if (key is CompassFragmentKey) return key.createFragment(data)
        return key.fragmentOrNull() ?: super.createFragment(key, data)
    }

    override fun setupFragmentTransaction(
        command: Command,
        currentFragment: Fragment?,
        nextFragment: Fragment,
        transaction: FragmentTransaction
    ) {
        val key = when (command) {
            is Forward -> command.key
            is Replace -> command.key
            else -> null
        }

        val data = when (command) {
            is Forward -> command.data
            is Replace -> command.data
            else -> null
        }

        if (key is CompassFragmentKey) {
            key.setupFragmentTransaction(command, currentFragment, nextFragment, transaction)
        } else if (key != null) {
            key.fragmentDetourOrNull()?.setupTransaction(
                key, data,
                currentFragment, nextFragment, transaction
            )
        }
    }

}

fun FragmentActivity.CompassFragmentSwapperNavigator(
    containerId: Int,
    hideStrategy: FragmentSwapperNavigator.HideStrategy = FragmentSwapperNavigator.HideStrategy.DETACH,
    swapOnReselection: Boolean = true
) = CompassFragmentSwapperNavigator(
    supportFragmentManager,
    containerId,
    hideStrategy,
    swapOnReselection
)

fun Fragment.CompassFragmentSwapperNavigator(
    containerId: Int,
    hideStrategy: FragmentSwapperNavigator.HideStrategy = FragmentSwapperNavigator.HideStrategy.DETACH,
    swapOnReselection: Boolean = true
) = CompassFragmentSwapperNavigator(
    childFragmentManager,
    containerId,
    hideStrategy,
    swapOnReselection
)