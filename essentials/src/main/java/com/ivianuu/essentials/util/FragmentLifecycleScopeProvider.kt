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

package com.ivianuu.essentials.util

import android.support.v4.app.Fragment
import com.ivianuu.autodispose.LifecycleScopeProvider
import com.ivianuu.essentials.ui.common.CORRESPONDING_FRAGMENT_EVENTS
import com.ivianuu.essentials.ui.common.FragmentEvent

/**
 * A lifecycle scope provider for fragments
 */
class FragmentLifecycleScopeProvider private constructor(
    private val fragment: Fragment
) : LifecycleScopeProvider<FragmentEvent> {

    override fun correspondingEvents() = CORRESPONDING_FRAGMENT_EVENTS

    override fun lifecycle() = FragmentLifecycleHandler.getLifecycle(fragment)

    override fun peekLifecycle() = FragmentLifecycleHandler.peekLifecycle(fragment)

    companion object {

        fun from(fragment: Fragment): FragmentLifecycleScopeProvider {
            return FragmentLifecycleScopeProvider(fragment)
        }
    }

}