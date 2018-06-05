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

import com.ivianuu.essentials.ui.common.ActivityEvent.*

enum class ActivityEvent {
    CREATE, START, RESUME, PAUSE, STOP, DESTROY
}

val CORRESPONDING_ACTIVITY_EVENTS = Function<ActivityEvent, ActivityEvent> {
    when (it) {
        CREATE -> DESTROY
        START -> STOP
        RESUME -> PAUSE
        PAUSE -> STOP
        STOP -> DESTROY
        else -> throw IllegalStateException("out of lifecycle ${it::class.java.simpleName}")
    }
}