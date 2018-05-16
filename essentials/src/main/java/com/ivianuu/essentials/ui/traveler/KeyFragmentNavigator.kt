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

package com.ivianuu.essentials.ui.traveler

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import com.ivianuu.traveler.commands.Command
import com.ivianuu.traveler.commands.Forward
import com.ivianuu.traveler.commands.Replace
import com.ivianuu.traveler.fragments.FragmentNavigator

/**
 * Navigator for key based navigation
 */
open class KeyFragmentNavigator(
    fragmentManager: FragmentManager,
    containerId: Int,
    private val exit: () -> Unit = {}
): FragmentNavigator(fragmentManager, containerId) {

    override fun createFragment(key: Any, data: Any?): Fragment? {
        return if (key is FragmentKey) {
            key.newInstance(data)
        } else {
            null
        }
    }

    override fun setupFragmentTransaction(
        command: Command,
        currentFragment: Fragment?,
        nextFragment: Fragment,
        transaction: FragmentTransaction
    ) {
        val key = when(command) {
            is Forward -> command.key as FragmentKey
            is Replace -> command.key as FragmentKey
            else -> null
        }

        key?.setupFragmentTransaction(command, currentFragment,
            nextFragment, transaction)
    }

    override fun exit() {
        exit.invoke()
    }
}