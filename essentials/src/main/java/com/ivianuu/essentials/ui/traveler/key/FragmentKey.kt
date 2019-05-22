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
import com.ivianuu.essentials.ui.traveler.anim.FragmentNavOptions
import com.ivianuu.essentials.util.ext.unsafeLazy

abstract class FragmentKey(
    val factory: () -> Fragment,
    fragmentTag: String? = null,
    open val options: FragmentNavOptions? = null
) : Parcelable {

    private val _fragmentTag = fragmentTag

    open val fragmentTag: String? get() = _fragmentTag ?: this.toString()

    open fun createFragment(): Fragment = factory().also { addTo(it) }
}

fun FragmentKey.addTo(fragment: Fragment) {
    if (fragment.arguments == null) fragment.arguments = bundleOf()
    addTo(fragment.requireArguments())
}

fun FragmentKey.addTo(bundle: Bundle) {
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