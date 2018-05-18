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
import com.ivianuu.essentials.util.ext.d
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

    override fun applyCommands(commands: Array<Command>) {
        commands.forEach { command ->
            when (command) {
                is Replace -> swapTo(command)
                else -> throw IllegalStateException("unsupported command $command")
            }
        }
    }

    protected open fun swapTo(command: Replace) {
        val newKey = command.key

        d { "swap to $newKey" }

        val oldFragment = getCurrentFragment()

        d { "old fragment $oldFragment" }

        // re attach a new instance of that fragment
        if (swapOnReselection && getFragmentTag(newKey) == oldFragment?.tag) {
            d { "do swap on reselection" }
            val transaction = fm.beginTransaction()
            transaction.remove(oldFragment)
            transaction.add(
                containerId,
                createFragment(newKey, command.data),
                getFragmentTag(newKey)
            )
            transaction.commitNow()
            return
        }

        val newFragment = getOrCreateFragmentForKey(newKey, command.data)

        d { "new fragment $newFragment" }

        if (newFragment == null) {
            d { "unknown $newKey" }
            unknownScreen(command)
            return
        }

        val transaction = fm.beginTransaction()

        // handle the old fragment
        if (oldFragment != null && oldFragment.isAdded) {
            when (hideStrategy) {
                HideStrategy.HIDE -> {
                    if (!oldFragment.isHidden) {
                        d { "hide old fragment" }
                        transaction.hide(oldFragment)
                    }
                }
                HideStrategy.DETACH -> {
                    if (!oldFragment.isDetached) {
                        d { "detach old fragment" }
                        transaction.detach(oldFragment)
                    }
                }
                HideStrategy.REMOVE -> {
                    d { "remove old fragment" }
                    transaction.remove(oldFragment)
                }
            }
        }

        // handle the new fragment
        val needToAdd = when (hideStrategy) {
            HideStrategy.HIDE -> {
                if (newFragment.isAdded && newFragment.isHidden) {
                    d { "show hidden new fragment" }
                    transaction.show(newFragment)
                    false
                } else {
                    d { "new fragment is not hidden" }
                    true
                }
            }
            HideStrategy.DETACH -> {
                if (newFragment.isDetached && newFragment.isDetached) {
                    transaction.attach(newFragment)
                    d { "attach detached new fragment" }
                    false
                } else {
                    d { "new fragment is not detached" }
                    true
                }
            }
            HideStrategy.REMOVE -> {
                d { "force re add due to remove strategy" }
                true
            }
        }

        // if we need to add the new fragment -> do it
        if (needToAdd) {
            d { "add new fragment" }
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

        d { "commited" }
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

    private fun getCurrentFragment(): Fragment? {
        return fm.fragments
            .filter { it.isVisible && it.isAdded && it.containerId == containerId }
            .reversed()
            .firstOrNull()
    }

    private fun getOrCreateFragmentForKey(key: Any, data: Any?): Fragment? {
        var fragment = findFragmentByKey(key)
        if (fragment == null) {
            fragment = createFragment(key, data)
        }
        return fragment
    }

    private fun findFragmentByKey(key: Any?): Fragment? {
        return if (key != null) {
            fm.findFragmentByTag(getFragmentTag(key))
        } else {
            null
        }
    }

    protected open fun unknownScreen(command: Command) {
        throw IllegalArgumentException("unknown screen $command")
    }

    enum class HideStrategy {
        HIDE, DETACH, REMOVE
    }
}