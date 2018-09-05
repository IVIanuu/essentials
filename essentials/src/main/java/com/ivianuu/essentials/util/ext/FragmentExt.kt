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

@file:Suppress("NOTHING_TO_INLINE")

package com.ivianuu.essentials.util.ext

import android.app.Activity
import android.view.View
import androidx.fragment.app.Fragment

inline fun Fragment.hideInputMethod() {
    requireActivity().hideInputMethod()
}

inline fun Fragment.showInputMethod(view: View) {
    requireActivity().showInputMethod(view)
}

inline fun <reified T : Activity> Fragment.activity() = requireActivity() as T

inline fun Fragment.requireArgs() =
    arguments ?: throw IllegalStateException("no arguments provided")

inline fun Fragment.requireView() = view ?: throw IllegalStateException("view is null")

inline fun <reified T : Fragment> Fragment.parentFragment() = requireParentFragment() as T

inline fun <reified T : Fragment> Fragment.targetFragment() = requireTargetFragment() as T

inline fun Fragment.requireParentFragment() =
    parentFragment ?: throw IllegalStateException("parent fragment is null")

inline fun Fragment.requireTargetFragment() =
    targetFragment ?: throw IllegalStateException("target fragment is null")