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

import android.view.View
import androidx.fragment.app.FragmentTransaction
import java.lang.ref.WeakReference

/**
 * Holder class for shared elements
 */
class SharedElements {

    val isEmpty
        get() = sharedElements.isEmpty()

    private val sharedElements =
        mutableMapOf<WeakReference<View>, String>()

    fun addSharedElement(view: View) {
        sharedElements[WeakReference(view)] = view.transitionName
    }

    fun applyToTransaction(transaction: FragmentTransaction) {
        sharedElements
            .map { it.key.get() to it.value }
            .filter { it.first != null }
            .forEach { transaction.addSharedElement(it.first!!, it.second) }
    }
}

fun sharedElementsOf(vararg views: View) =
    sharedElementsOf(views.toList())

fun sharedElementsOf(views: Collection<View>) = SharedElements().apply {
    views.forEach { addSharedElement(it) }
}