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

package com.ivianuu.essentials.ui.anim

import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction

/**
 * @author Manuel Wrage (IVIanuu)
 */
abstract class FragmentTransition : Parcelable {
    abstract fun setup(
        transaction: FragmentTransaction,
        from: Fragment?,
        to: Fragment?,
        isPush: Boolean
    )
}

private const val KEY_PUSH_TRANSITION = "push_transition"
private const val KEY_POP_TRANSITION = "pop_transition"

var Fragment.pushTransition: FragmentTransition?
    get() = arguments?.getParcelable(KEY_PUSH_TRANSITION)
    set(value) {
        getOrCreateArgs().putParcelable(KEY_PUSH_TRANSITION, value)
    }

var Fragment.popTransition: FragmentTransition?
    get() = arguments?.getParcelable(KEY_POP_TRANSITION)
    set(value) {
        getOrCreateArgs().putParcelable(KEY_POP_TRANSITION, value)
    }

private fun Fragment.getOrCreateArgs(): Bundle =
    arguments ?: Bundle().also { arguments = it }