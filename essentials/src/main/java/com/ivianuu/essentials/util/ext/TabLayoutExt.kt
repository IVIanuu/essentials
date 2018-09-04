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

package com.ivianuu.essentials.util.ext

import com.google.android.material.tabs.TabLayout

fun TabLayout.doOnTabSelected(block: (tab: TabLayout.Tab) -> Unit) =
    addOnTabSelectedListener(onTabSelected = block)

fun TabLayout.doOnTabUnselected(block: (tab: TabLayout.Tab) -> Unit) =
    addOnTabSelectedListener(onTabUnselected = block)

fun TabLayout.doOnTabReselected(block: (tab: TabLayout.Tab) -> Unit) =
    addOnTabSelectedListener(onTabReselected = block)

fun TabLayout.addOnTabSelectedListener(
    onTabSelected: ((tab: TabLayout.Tab) -> Unit)? = null,
    onTabUnselected: ((tab: TabLayout.Tab) -> Unit)? = null,
    onTabReselected: ((tab: TabLayout.Tab) -> Unit)? = null
): TabLayout.OnTabSelectedListener {
    val listener = object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab) {
            onTabSelected?.invoke(tab)
        }

        override fun onTabUnselected(tab: TabLayout.Tab) {
            onTabUnselected?.invoke(tab)
        }

        override fun onTabReselected(tab: TabLayout.Tab) {
            onTabReselected?.invoke(tab)
        }
    }
    addOnTabSelectedListener(listener)
    return listener
}