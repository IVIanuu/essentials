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

package com.ivianuu.essentials.ui.common

import android.support.v4.app.FragmentTransaction
import android.view.View
import java.lang.ref.WeakReference

/**
 * Holder class for shared elements
 */
class SharedElements {

    val isEmpty
        get() = sharedElementViews.isEmpty()

    private val sharedElementViews = mutableMapOf<WeakReference<View>, String?>()

    fun addSharedElement(view: View, name: String = view.transitionName) {
        sharedElementViews[WeakReference(view)] = name
    }

    fun applyToTransaction(transaction: FragmentTransaction) {
        for ((viewRef, customTransitionName) in sharedElementViews) {
            viewRef.get()?.apply {
                transaction.addSharedElement(this, customTransitionName)
            }
        }
    }
}