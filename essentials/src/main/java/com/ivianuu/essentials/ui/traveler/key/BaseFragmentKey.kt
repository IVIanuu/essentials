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

abstract class BaseFragmentKey(
    val target: KClass<out Fragment>,
    open val setup: Setup? = null
) : FragmentKey, Parcelable {

    override fun createFragment(data: Any?): Fragment = target.java.newInstance().apply {
        arguments = if (arguments != null) {
            arguments!!.apply { putParcelable(KEY_KEY, this@BaseFragmentKey) }
        } else {
            bundleOf(KEY_KEY to this@BaseFragmentKey)
        }
    }

    override fun setupFragmentTransaction(
        command: Command,
        currentFragment: Fragment?,
        nextFragment: Fragment,
        transaction: FragmentTransaction
    ) {
        setup?.apply(command, currentFragment, nextFragment, transaction)
    }

    interface Setup {
        fun apply(
            command: Command,
            currentFragment: Fragment?,
            nextFragment: Fragment,
            transaction: FragmentTransaction
        )
    }
}

fun <T : Parcelable> Fragment.key(): T = arguments!!.getParcelable(KEY_KEY)!!

fun <T : Parcelable> Fragment.keyOrNull() = try {
    key<T>()
} catch (e: Exception) {
    null
}

fun <T : Parcelable> Fragment.bindKey() = unsafeLazy { key<T>() }