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

import android.app.Activity
import com.ivianuu.autodispose.LifecycleScopeProvider
import com.ivianuu.essentials.ui.common.ActivityEvent
import com.ivianuu.essentials.ui.common.CORRESPONDING_ACTIVITY_EVENTS

/**
 * A [LifecycleScopeProvider] for [Activity]'s
 */
class ActivityLifecycleScopeProvider private constructor(
    private val activity: Activity
) : LifecycleScopeProvider<ActivityEvent> {
    
    override fun correspondingEvents() = CORRESPONDING_ACTIVITY_EVENTS

    override fun lifecycle() = ActivityLifecycleHandler.getLifecycle(activity)

    override fun peekLifecycle() = ActivityLifecycleHandler.peekLifecycle(activity)

    companion object {
        fun from(activity: Activity): LifecycleScopeProvider<ActivityEvent> {
            return ActivityLifecycleScopeProvider(activity)
        }
    }
}