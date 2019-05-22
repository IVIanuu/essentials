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

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.dismissed
import androidx.fragment.app.shownByMe
import com.ivianuu.essentials.ui.traveler.anim.FragmentNavOptions
import com.ivianuu.essentials.ui.traveler.anim.NoopTransition
import com.ivianuu.essentials.ui.traveler.key.FragmentKey
import com.ivianuu.essentials.util.ext.andTrue
import com.ivianuu.traveler.*
import com.ivianuu.traveler.common.ResultNavigator
import java.util.*

/**
 * @author Manuel Wrage (IVIanuu)
 */
class EsFragmentNavigator(
    private val fm: FragmentManager,
    private val containerId: Int
) : ResultNavigator() {

    private val backStack = mutableListOf<FragmentKey>()

    override fun applyCommandWithResult(command: Command): Boolean {
        return when (command) {
            is Forward -> forward(command).andTrue()
            is Replace -> replace(command).andTrue()
            is Back -> handleBack().andTrue()
            is BackTo -> {
                val key = command.key?.requireFragmentKey()
                if (key != null) {
                    backTo(key)
                } else {
                    backToRoot()
                }
                true
            }
            else -> false
        }
    }

    fun restoreState(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) return
        backStack.addAll(savedInstanceState.getParcelableArrayList(KEY_BACKSTACK)!!)
    }

    fun saveState(outState: Bundle) {
        outState.putParcelableArrayList(KEY_BACKSTACK, backStack as ArrayList<FragmentKey>)
    }

    fun handleBack(): Boolean {
        return if (backStack.size > 1) {
            back()
            true
        } else {
            false
        }
    }

    private fun forward(command: Forward) {
        val key = command.key.requireFragmentKey()
        val options = command.data?.requireFragmentNavOptions()

        val fragment = key.createFragment()
        val tag = key.fragmentTag
        val isDialog = fragment is DialogFragment

        if (isDialog) {
            fragment as DialogFragment
            fragment.shownByMe = true
            fragment.dismissed = false
        }

        val transaction = fm.beginTransaction()
            .disallowAddToBackStack()
            .setReorderingAllowed(true)

        val oldKey = backStack.lastOrNull()
        val oldFragment = oldKey?.let { fm.findFragmentByTag(it.fragmentTag)!! }

        if (oldFragment != null && !isDialog) {
            transaction.detach(oldFragment)
        }

        backStack.add(key)

        if (isDialog) {
            transaction.add(fragment, tag)
        } else {
            transaction.add(containerId, fragment, tag)
        }

        val transition = options?.push()
            ?: key.options?.push()
            ?: NoopTransition

        transition.setup(transaction, oldFragment, fragment, true)

        transaction.commitNow()
    }

    private fun replace(command: Replace) {
        val key = command.key.requireFragmentKey()
        val options = command.data?.requireFragmentNavOptions()

        val fragment = key.createFragment()
        val tag = key.fragmentTag

        val transaction = fm.beginTransaction()
            .disallowAddToBackStack()
            .setReorderingAllowed(true)

        val oldKey = backStack.lastOrNull()
        if (oldKey != null) {
            backStack.removeAt(backStack.lastIndex)
        }

        val oldFragment = oldKey?.let { fm.findFragmentByTag(it.fragmentTag)!! }

        if (oldFragment != null) {
            transaction.remove(oldFragment)
        }

        backStack.add(key)
        transaction.add(containerId, fragment, tag)

        val transition = options?.push()
            ?: key.options?.push()
            ?: NoopTransition

        transition.setup(transaction, oldFragment, fragment, true)

        transaction.commitNow()
    }

    private fun back() {
        if (backStack.isEmpty()) return

        val oldTopKey = backStack.removeAt(backStack.lastIndex)
        val oldTopFragment = fm.findFragmentByTag(oldTopKey.fragmentTag)!!
        val newTopKey = backStack.lastOrNull()
        val newTopFragment = newTopKey?.fragmentTag?.let {
            fm.findFragmentByTag(it)
        }

        val transaction = fm.beginTransaction()
            .disallowAddToBackStack()
            .setReorderingAllowed(true)

        transaction.remove(oldTopFragment)

        if (newTopFragment != null) {
            transaction.attach(newTopFragment)
        }

        // todo use data once available
        /*val transition = options?.push()
            ?: key.options?.push()*/

        (oldTopKey.options?.pop() ?: NoopTransition).setup(
            transaction, oldTopFragment, newTopFragment, false
        )

        transaction.commitNow()
    }

    private fun backTo(newTopKey: FragmentKey) {
        check(backStack.contains(newTopKey)) { "Key is not in the backstack $newTopKey" }
        val oldTopKey = backStack.last()
        if (oldTopKey == newTopKey) return

        val newBackStack = mutableListOf<FragmentKey>()

        for (key in backStack) {
            newBackStack.add(key)
            if (key == newTopKey) break
        }

        val transaction = fm.beginTransaction()
            .disallowAddToBackStack()
            .setReorderingAllowed(true)

        backStack
            .filterNot { newBackStack.contains(it) }
            .forEach { transaction.remove(fm.findFragmentByTag(it.fragmentTag)!!) }

        backStack.clear()
        backStack.addAll(newBackStack)

        val newTopFragment = fm.findFragmentByTag(newBackStack.last().fragmentTag)!!
        val oldTopFragment = fm.findFragmentByTag(oldTopKey.fragmentTag)!!

        transaction.attach(newTopFragment)

        // todo use data once available
        /*val transition = options?.push()
            ?: key.options?.push()*/

        (oldTopKey.options?.pop() ?: NoopTransition).setup(
            transaction, oldTopFragment, newTopFragment, false
        )

        transaction.commitNow()
    }

    private fun backToRoot() {
        if (backStack.isNotEmpty()) {
            backTo(backStack.first())
        }
    }

    private fun Any.requireFragmentKey(): FragmentKey =
        this as? FragmentKey ?: error("$this is not a FragmentKey")

    private fun Any.requireFragmentNavOptions(): FragmentNavOptions =
        this as? FragmentNavOptions ?: error("$this is is not a FragmentNavOptions")

    private companion object {
        private const val KEY_BACKSTACK = "EsFragmentNavigator.backstack"
    }
}