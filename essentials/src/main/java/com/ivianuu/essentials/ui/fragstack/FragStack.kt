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

package com.ivianuu.essentials.ui.fragstack

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.ivianuu.essentials.ui.common.BackListener
import java.util.*
import kotlin.collections.ArrayList

/**
 * @author Manuel Wrage (IVIanuu)
 */
class FragStack(
    private val fm: FragmentManager,
    val containerId: Int,
    val tag: String = "",
    savedInstanceState: Bundle? = null
) {

    private val backstack = mutableListOf<BackstackEntry>()

    private val transactionIndexer = TransactionIndexer()

    init {
        if (savedInstanceState != null) {
            restoreInstanceState(savedInstanceState)
        }
    }

    fun handleBack(): Boolean {
        if (backstack.isNotEmpty()) {
            val currentEntry = backstack.last()

            val currentFragment = fm.findFragmentByTag(currentEntry.who)
            if (currentFragment is BackListener && currentFragment.handleBack()) {
                return true
            } else if (popCurrentFragment()) {
                return true
            }
        }

        return false
    }

    fun pushFragment(fragment: Fragment, tag: String? = null) {
        val oldTopEntry = backstack.lastOrNull()
        val oldFragment = if (oldTopEntry != null) {
            fm.findFragmentByTag(oldTopEntry.who)
        } else {
            null
        }

        val newEntry = BackstackEntry(
            UUID.randomUUID().toString(),
            transactionIndexer.nextIndex(),
            tag
        )

        val transaction = fm.beginTransaction()

        if (oldFragment != null) {
            transaction.detach(oldFragment)
        }

        transaction.add(containerId, fragment, newEntry.who)

        transaction.commitNow()

        backstack.add(newEntry)
    }

    fun replaceTopFragment(fragment: Fragment, tag: String? = null) {
        val oldTopEntry = backstack.lastOrNull()
        val oldFragment = if (oldTopEntry != null) {
            fm.findFragmentByTag(oldTopEntry.who)
        } else {
            null
        }

        val newEntry = BackstackEntry(
            UUID.randomUUID().toString(),
            transactionIndexer.nextIndex(),
            tag
        )

        val transaction = fm.beginTransaction()

        if (oldFragment != null) {
            transaction.remove(oldFragment)
        }

        transaction.add(containerId, fragment, newEntry.who)

        transaction.commitNow()

        backstack.add(newEntry)
    }

    fun popCurrentFragment(): Boolean {
        return if (backstack.size > 1) {
            val oldEntry = backstack.last()

            val fragment = fm.findFragmentByTag(oldEntry.who)

            val transaction = fm.beginTransaction()

            if (fragment != null) {
                transaction.remove(fragment)
            }

            val newEntry = backstack[backstack.lastIndex - 1]
            val newFragment = fm.findFragmentByTag(newEntry.who)

            transaction.attach(newFragment)

            transaction.commitNow()
            backstack.removeAt(backstack.lastIndex)
            true
        } else {
            false
        }
    }

    fun popToEntry(entryToPop: BackstackEntry) {
        if (backstack.size > 1) {
            val transaction = fm.beginTransaction()

            while (backstack.size > 1) {
                val entry = backstack.last()

                if (entry == entryToPop) break

                val fragment = fm.findFragmentByTag(entry.who)
                if (fragment != null) {
                    transaction.remove(fragment)
                }
                backstack.removeAt(backstack.lastIndex)
            }

            val newTop = backstack.first()
            val fragment = fm.findFragmentByTag(newTop.who)
            transaction.attach(fragment)
            transaction.commitNow()
        }
    }

    fun popToFragment(fragment: Fragment) {
        val tag = fragment.tag
        if (tag != null) {
            popToTag(tag)
        }
    }

    fun popToTag(tag: String) {
        val entry = backstack.firstOrNull { it.tag == tag }
        if (entry != null) {
            popToEntry(entry)
        }
    }

    fun popToRoot() {
        if (backstack.size > 1) {
            val rootEntry = backstack.first()
            popToEntry(rootEntry)
        }
    }

    fun hasRoot() = backstack.isNotEmpty()

    fun setRoot(fragment: Fragment) {
        setBackstack(listOf(fragment))

    }

    fun getBackstack() = backstack.toList()

    fun setBackstack(fragments: List<Fragment>) {
        // clean up old backstack
        val transaction = fm.beginTransaction()

        backstack.forEach { entry ->
            val fragment = fm.findFragmentByTag(entry.who)
            if (fragment != null) {
                transaction.remove(fragment)
            }
        }

        transaction.commitNow()

        fragments.forEach { pushFragment(it) }
    }

    fun restoreInstanceState(savedInstanceState: Bundle) {
        val backstack =
            savedInstanceState.getParcelableArrayList<BackstackEntry>(
                KEY_BACKSTACK + tag)
        if (backstack != null) {
            this.backstack.addAll(backstack)
        }

        val transactionIndexerBundle =
            savedInstanceState.getBundle(KEY_TRANSACTION_INDEXER + tag)
        if (transactionIndexerBundle != null) {
            transactionIndexer.restoreInstanceState(transactionIndexerBundle)
        }
    }

    fun saveInstanceState(outState: Bundle) {
        outState.putParcelableArrayList(KEY_BACKSTACK + tag, ArrayList(backstack))

        val transactionIndexerBundle = Bundle()
        transactionIndexer.saveInstanceState(transactionIndexerBundle)
        outState.putBundle(KEY_TRANSACTION_INDEXER + tag, transactionIndexerBundle)
    }

    companion object {
        private const val KEY_BACKSTACK = "FragStack.backstack"
        private const val KEY_TRANSACTION_INDEXER = "FragStack.transactionIndexer"
    }

}