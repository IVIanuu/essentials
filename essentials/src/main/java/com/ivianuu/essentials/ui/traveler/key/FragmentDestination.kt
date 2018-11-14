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

package com.ivianuu.essentials.ui.traveler.key

import android.os.Parcelable
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.ivianuu.essentials.util.ext.unsafeLazy
import com.ivianuu.traveler.Command
import com.ivianuu.traveler.fragment.FragmentKey
import kotlin.reflect.KClass

interface FragmentDestination : FragmentKey, Parcelable {

    val target: KClass<out Fragment>
    val fragmentTransactionSetup: FragmentTransactionSetup? get() = null

    override fun createFragment(data: Any?): Fragment = target.java.newInstance().apply {
        arguments = if (arguments != null) {
            arguments!!.apply { putParcelable(KEY_DESTINATION, this@FragmentDestination) }
        } else {
            bundleOf(KEY_DESTINATION to this@FragmentDestination)
        }
    }

    override fun setupFragmentTransaction(
        command: Command,
        currentFragment: Fragment?,
        nextFragment: Fragment,
        transaction: FragmentTransaction
    ) {
        fragmentTransactionSetup?.setupFragmentTransaction(
            command, currentFragment, nextFragment, transaction
        )
    }
}

abstract class BaseFragmentDestination(
    override val target: KClass<out Fragment>,
    override val fragmentTransactionSetup: FragmentTransactionSetup? = null
) : FragmentDestination

interface FragmentTransactionSetup {
    fun setupFragmentTransaction(
        command: Command,
        currentFragment: Fragment?,
        nextFragment: Fragment,
        transaction: FragmentTransaction
    ) {
    }
}

fun <T : Parcelable> Fragment.destination(): T = arguments!!.getParcelable(KEY_DESTINATION)!!

fun <T : Parcelable> Fragment.destinationOrNull() = try {
    destination<T>()
} catch (e: Exception) {
    null
}

fun <T : Parcelable> Fragment.bindDestination() = unsafeLazy { destination<T>() }