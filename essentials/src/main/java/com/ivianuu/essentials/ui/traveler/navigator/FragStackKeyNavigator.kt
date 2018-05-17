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

import com.ivianuu.essentials.ui.fragstack.FragStack
import com.ivianuu.essentials.ui.traveler.key.FragmentKey
import com.ivianuu.traveler.BaseNavigator
import com.ivianuu.traveler.commands.Back
import com.ivianuu.traveler.commands.BackTo
import com.ivianuu.traveler.commands.Forward
import com.ivianuu.traveler.commands.Replace

/**
 * @author Manuel Wrage (IVIanuu)
 */
class FragStackKeyNavigator(private val stack: FragStack) : BaseNavigator() {

    override fun forward(command: Forward) {
        val key = command.key as FragmentKey
        val fragment = key.newInstance(command.data)
        stack.pushFragment(fragment)
    }

    override fun replace(command: Replace) {
        val key = command.key as FragmentKey
        val fragment = key.newInstance(command.data)
        stack.replaceTopFragment(fragment)
    }

    override fun back(command: Back) {
        stack.popCurrentFragment()
    }

    override fun backTo(command: BackTo) {
        if (command.key == null) {
            stack.popToRoot()
        } else {
            stack.popToTag(command.key as String)
        }
    }

}