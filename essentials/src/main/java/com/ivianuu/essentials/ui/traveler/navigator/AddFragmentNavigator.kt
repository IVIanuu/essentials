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

package com.ivianuu.essentials.ui.traveler.navigator

import androidx.fragment.app.FragmentManager
import com.ivianuu.traveler.Command
import com.ivianuu.traveler.common.TypedResultNavigator
import com.ivianuu.traveler.fragment.FragmentKey

/**
 * Adds the fragment for the [key]
 */
data class AddFragment(val key: FragmentKey) : Command

/**
 * A [Navigator] which allows to add fragments instead of replacing them
 */
class AddFragmentPlugin(private val fragmentManager: FragmentManager) :
    TypedResultNavigator<AddFragment>(AddFragment::class) {
    override fun applyTypedCommandWithResult(command: AddFragment): Boolean {
        try {
            fragmentManager.executePendingTransactions()
        } catch (e: Exception) {
        }

        val fragment = command.key.createFragment(null)
        val tag = command.key.toString()

        fragmentManager.beginTransaction()
            .add(fragment, tag)
            .addToBackStack(tag)
            .commit()

        return true
    }
}