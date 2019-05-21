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

import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.ivianuu.traveler.*
import com.ivianuu.traveler.common.ResultNavigator

interface FragmentKey : Parcelable {
    val fragmentTag: String get() = this.toString()
    fun createFragment(): Fragment
}

/**
 * Navigator for fragments only
 */
open class EsFragmentNavigator(
    private val fm: FragmentManager,
    private val containerId: Int
) : ResultNavigator() {

    val backstack: List<FragmentKey> get() = _backstack
    private val _backstack = mutableListOf<FragmentKey>()

    override fun applyCommandWithResult(command: Command): Boolean {
        when (command) {
            is Forward -> {
                val newKey = command.key.requireIsFragmentKey()
                val newBackstack = _backstack.toMutableList()
                newBackstack.add(newKey)
                setBackstack(newBackstack)
            }
            is Replace -> {
                val newKey = command.key.requireIsFragmentKey()
                val newBackstack = _backstack.toMutableList()
                if (newBackstack.isNotEmpty()) {
                    newBackstack.removeAt(newBackstack.lastIndex)
                }
                newBackstack.add(newKey)
                setBackstack(newBackstack)
            }
            is Back -> {
                val newBackstack = _backstack.toMutableList()
                if (newBackstack.isNotEmpty()) {
                    newBackstack.removeAt(newBackstack.lastIndex)
                }
                setBackstack(newBackstack)
            }
            is BackTo -> {
                if (command.key != null) {
                    val keyToPopTo = command.key!!.requireIsFragmentKey()
                    val newBackstack = _backstack.dropLastWhile {
                        it != keyToPopTo
                    }
                    setBackstack(newBackstack)
                } else {
                    val newBackstack = mutableListOf<FragmentKey>()
                    _backstack.firstOrNull()?.let { newBackstack.add(it) }
                    setBackstack(newBackstack)
                }
            }
            else -> return false
        }

        return true
    }

    private fun setBackstack(newBackstack: List<FragmentKey>) {
        if (newBackstack == _backstack) return

        val oldBackstack = _backstack.toList()

        _backstack.clear()
        _backstack.addAll(newBackstack)

        val transaction = fm.beginTransaction()
            .disallowAddToBackStack()
            .setReorderingAllowed(true)

        for (oldKey in oldBackstack) {
            val fragment = oldKey.getFragment() ?: continue
            if (!newBackstack.contains(oldKey)) {
                transaction.remove(fragment)
            } else if (!fragment.isHidden) {
                transaction.hide(fragment)
            }
        }

        for (newKey in newBackstack) {
            val fragment = newKey.getFragment()
            if (newKey == newBackstack.last()) {
                if (fragment != null) {
                    // Fragments are quirky, they die asynchronously. Ignore if they're still there.
                    if (fragment.isRemoving) {
                        transaction.replace(
                            containerId,
                            newKey.createFragment(),
                            newKey.fragmentTag
                        )
                    } else if (fragment.isHidden) {
                        transaction.show(fragment)
                    }
                } else {
                    transaction.add(containerId, newKey.createFragment(), newKey.fragmentTag)
                }
            } else {
                if (fragment != null && !fragment.isHidden) {
                    transaction.hide(fragment)
                }
            }
        }

        transaction.commitNow()
    }

    private fun FragmentKey.getFragment(): Fragment? =
        fm.findFragmentByTag(fragmentTag)

    private fun Any.requireIsFragmentKey(): FragmentKey =
        (this as? FragmentKey) ?: error("must be a fragment key $this")
}