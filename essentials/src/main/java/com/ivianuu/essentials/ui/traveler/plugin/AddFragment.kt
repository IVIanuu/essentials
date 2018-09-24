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

package com.ivianuu.essentials.ui.traveler.plugin

import androidx.fragment.app.FragmentManager
import com.ivianuu.compass.fragment.fragment
import com.ivianuu.traveler.Command
import com.ivianuu.traveler.plugin.TypedNavigatorPlugin

/**
 * Adds the fragment for the [destination]
 */
data class AddFragment(val destination: Any) : Command

/**
 * A navigator plugin which allows to add fragments instead of replacing them
 */
class AddFragmentPlugin(private val fragmentManager: FragmentManager) :
    TypedNavigatorPlugin<AddFragment>(AddFragment::class) {
    override fun applyCommandTyped(command: AddFragment): Boolean {
        try {
            fragmentManager.executePendingTransactions()
        } catch (e: Exception) {
        }

        val fragment = command.destination.fragment()
        val tag = command.destination.toString()

        fragmentManager.beginTransaction()
            .add(fragment, tag)
            .addToBackStack(tag)
            .commit()

        return true
    }
}