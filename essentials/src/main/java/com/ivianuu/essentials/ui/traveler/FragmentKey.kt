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
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import com.ivianuu.traveler.commands.Command

/**
 * Key for [Fragment]'s
 */
abstract class FragmentKey {

    fun newInstance(): Fragment = createFragment().apply {
        if (this@FragmentKey is Parcelable) {
            if (arguments != null) {
                arguments!!.putParcelable(KEY_KEY, this@FragmentKey)
            } else {
                arguments = Bundle().apply {
                    putParcelable(KEY_KEY, this@FragmentKey)
                }
            }
        }
    }

    open val fragmentTag: String
        get() = toString()

    protected abstract fun createFragment(): Fragment

    open fun setupFragmentTransactionAnimation(
        command: Command,
        currentFragment: Fragment?,
        nextFragment: Fragment,
        fragmentTransaction: FragmentTransaction
    ) {
    }

    companion object {
        private const val KEY_KEY = "key"

        fun <T> get(fragment: Fragment): T? where T : FragmentKey, T : Parcelable =
            fragment.arguments?.getParcelable(KEY_KEY) as T?
    }
}

fun <T> Fragment.key() where T : FragmentKey, T : Parcelable =
    FragmentKey.get<T>(this)

fun <T> Fragment.requireKey() where T : FragmentKey, T : Parcelable =
    key<T>() ?: throw IllegalStateException("missing key")