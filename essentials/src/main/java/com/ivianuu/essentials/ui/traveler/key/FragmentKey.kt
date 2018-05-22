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
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import com.ivianuu.traveler.commands.Command
import kotlin.reflect.KClass

/**
 * Key for [Fragment]'s
 */
abstract class FragmentKey {

    fun newInstance(data: Any? = null) = createFragment(data).apply {
        if (this@FragmentKey is Parcelable) {
            val args = arguments ?: Bundle().also { arguments = it }
            args.putParcelable(KEY_KEY, this@FragmentKey)
        }
    }

    protected abstract fun createFragment(data: Any?): Fragment

    open fun setupFragmentTransaction(
        command: Command,
        currentFragment: Fragment?,
        nextFragment: Fragment,
        transaction: FragmentTransaction
    ) {
    }

    companion object {
        private const val KEY_KEY = "key"

        fun <T> get(fragment: Fragment): T? where T : FragmentKey, T : Parcelable =
            fragment.arguments?.getParcelable(KEY_KEY) as T?
    }
}

/**
 * Auto creates the fragment by using its clazz new instance
 */
open class FragmentClassKey(val clazz: KClass<out Fragment>) : FragmentKey() {
    override fun createFragment(data: Any?): Fragment = clazz.java.newInstance()
}

fun <T> Fragment.key() where T : FragmentKey, T : Parcelable =
    FragmentKey.get<T>(this)

fun <T> Fragment.requireKey() where T : FragmentKey, T : Parcelable =
    key<T>() ?: throw IllegalStateException("missing key")