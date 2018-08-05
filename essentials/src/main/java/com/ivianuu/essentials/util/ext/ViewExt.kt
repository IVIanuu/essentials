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

// Aliases to other public API.

package com.ivianuu.essentials.util.ext

import android.app.Activity
import android.view.View

inline val View.activity: Activity?
    get() = context.findActivity()

inline fun View.requireActivity() = context.findActivityOrThrow()

fun View.doOnViewAttachedToWindow(action: ((v: View) -> Unit)?) =
    addOnAttachStateChangeListener(onViewAttachedToWindow = action)

fun View.doOnViewDetachedFromWindow(action: ((v: View) -> Unit)?) =
    addOnAttachStateChangeListener(onViewDetachedFromWindow = action)

fun View.addOnAttachStateChangeListener(
    onViewAttachedToWindow: ((v: View) -> Unit)? = null,
    onViewDetachedFromWindow: ((v: View) -> Unit)? = null
): View.OnAttachStateChangeListener {
    val listener = object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(v: View) {
            onViewAttachedToWindow?.invoke(v)
        }

        override fun onViewDetachedFromWindow(v: View) {
            onViewDetachedFromWindow?.invoke(v)
        }
    }

    addOnAttachStateChangeListener(listener)

    return listener
}

@Suppress("UNCHECKED_CAST")
inline fun <T> View.tag() = tag as T