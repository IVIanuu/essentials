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
import android.support.v4.app.containerId
import com.ivianuu.traveler.Navigator
import com.ivianuu.traveler.commands.Command
import com.ivianuu.traveler.commands.Replace

/**
 * A navigator which swaps and reuses fragments
 * This navigator does not provide any backstack functionality
 */
abstract class FragmentSwapperNavigator(
    private val fm: FragmentManager,
    private val containerId: Int,
    private val hideStrategy: HideStrategy = HideStrategy.DETACH,
    private var swapOnReselection: Boolean = true
) : Navigator {

    override fun applyCommand(command: Command) {
        when (command) {
            is Replace -> swapTo(command)
            else -> throw IllegalStateException("unsupported command $command")
        }
    }

    protected open fun swapTo(command: Replace) {
        val newKey = command.key

        val oldFragment = getCurrentFragment()

        // re attach a new instance of that fragment
        if (swapOnReselection && getFragmentTag(newKey) == oldFragment?.tag) {
            val transaction = fm.beginTransaction()
            transaction.remove(oldFragment)
            transaction.add(
                containerId,
                createFragment(newKey, command.data)!!,
                getFragmentTag(newKey)
            )
            transaction.commitNow()
            return
        }

        val newFragment = getOrCreateFragmentForKey(newKey, command.data)

        if (newFragment == null) {
            unknownScreen(command)
            return
        }

        val transaction = fm.beginTransaction()

        // handle the old fragment
        if (oldFragment != null && oldFragment.isAdded) {
            when (hideStrategy) {
                HideStrategy.HIDE -> {
                    if (!oldFragment.isHidden) {
                        transaction.hide(oldFragment)
                    }
                }
                HideStrategy.DETACH -> {
                    if (!oldFragment.isDetached) {
                        transaction.detach(oldFragment)
                    }
                }
                HideStrategy.REMOVE -> {
                    transaction.remove(oldFragment)
                }
            }
        }

        // handle the new fragment
        val needToAdd = when (hideStrategy) {
            HideStrategy.HIDE -> {
                if (newFragment.isAdded && newFragment.isHidden) {
                    transaction.show(newFragment)
                    false
                } else {
                    true
                }
            }
            HideStrategy.DETACH -> {
                if (newFragment.isDetached && newFragment.isDetached) {
                    transaction.attach(newFragment)
                    false
                } else {
                    true
                }
            }
            HideStrategy.REMOVE -> true
        }

        // if we need to add the new fragment -> do it
        if (needToAdd) {
            transaction.add(containerId, newFragment, getFragmentTag(newKey))
        }

        // apply transitions
        setupFragmentTransaction(
            command,
            oldFragment,
            newFragment,
            transaction
        )

        // commit
        transaction.commitNow()
    }

    protected abstract fun createFragment(key: Any, data: Any?): Fragment?

    protected open fun getFragmentTag(key: Any) = key.toString()

    protected open fun setupFragmentTransaction(
        command: Command,
        currentFragment: Fragment?,
        nextFragment: Fragment,
        transaction: FragmentTransaction
    ) {
    }

    private fun getCurrentFragment(): Fragment? = fm.fragments
        .filter { it.isVisible && it.isAdded && it.containerId == containerId }
        .reversed()
        .firstOrNull()

    private fun getOrCreateFragmentForKey(key: Any, data: Any?): Fragment? {
        var fragment = findFragmentByKey(key)
        if (fragment == null) {
            fragment = createFragment(key, data)
        }
        return fragment
    }

    private fun findFragmentByKey(key: Any?): Fragment? = if (key != null) {
        fm.findFragmentByTag(getFragmentTag(key))
    } else {
        null
    }

    protected open fun unknownScreen(command: Command) {
        throw IllegalArgumentException("unknown screen $command")
    }

    enum class HideStrategy {
        HIDE, DETACH, REMOVE
    }
}