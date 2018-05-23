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

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import com.ivianuu.traveler.commands.Command
import com.ivianuu.traveler.fragments.FragmentNavigator

/**
 * Navigator for key based navigation
 */
open class KeyFragmentNavigator(
    fragmentManager: FragmentManager,
    containerId: Int,
    private val exit: () -> Unit = {}
) : FragmentNavigator(fragmentManager, containerId) {

    private val fragmentNavigatorHelper = KeyFragmentNavigatorHelper()

    override fun createFragment(key: Any, data: Any?): Fragment? {
        return fragmentNavigatorHelper.createFragment(key, data)
    }

    override fun setupFragmentTransaction(
        command: Command,
        currentFragment: Fragment?,
        nextFragment: Fragment,
        transaction: FragmentTransaction
    ) {
        fragmentNavigatorHelper.setupFragmentTransaction(command, currentFragment, nextFragment, transaction)
    }

    override fun exit() {
        exit.invoke()
    }
}