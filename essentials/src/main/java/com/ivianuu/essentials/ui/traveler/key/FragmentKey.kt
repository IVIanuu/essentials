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

import android.os.Bundle
import android.os.Parcelable
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.ivianuu.essentials.ui.anim.popTransition
import com.ivianuu.essentials.ui.anim.pushTransition
import com.ivianuu.essentials.ui.traveler.FragmentNavOptions
import com.ivianuu.essentials.util.ext.unsafeLazy

import com.ivianuu.traveler.Command
import com.ivianuu.traveler.Forward
import com.ivianuu.traveler.Replace
import com.ivianuu.traveler.android.FragmentKey

abstract class FragmentKey(
    val factory: () -> Fragment,
    open val defaultNavOptions: FragmentNavOptions? = null
) : FragmentKey, Parcelable {

    override fun createFragment(data: Any?): Fragment = factory().also { addTo(it) }

    override fun setupFragmentTransaction(
        command: Command,
        currentFragment: Fragment?,
        nextFragment: Fragment,
        transaction: FragmentTransaction
    ) {
        super.setupFragmentTransaction(command, currentFragment, nextFragment, transaction)

        val isPush = command is Forward || command is Replace

        val transition = if (isPush) nextFragment.pushTransition
        else currentFragment?.popTransition

        transition?.setup(transaction, currentFragment, nextFragment, isPush)
    }

}

fun com.ivianuu.essentials.ui.traveler.key.FragmentKey.addTo(fragment: Fragment) {
    if (fragment.arguments == null) fragment.arguments = bundleOf()
    addTo(fragment.requireArguments())
}

fun com.ivianuu.essentials.ui.traveler.key.FragmentKey.addTo(bundle: Bundle) {
    bundle.putParcelable(TRAVELER_KEY, this)
    bundle.putString(TRAVELER_KEY_CLASS, javaClass.name)
}

fun <T : Parcelable> Fragment.getKey(): T = requireArguments().getParcelable(TRAVELER_KEY)!!

fun <T : Parcelable> Fragment.getKeyOrNull(): T? = try {
    getKey()
} catch (e: Exception) {
    null
}

fun <T : Parcelable> Fragment.key(): Lazy<T> = unsafeLazy { getKey<T>() }