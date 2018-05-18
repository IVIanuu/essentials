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

import com.ivianuu.essentials.ui.common.FragmentEvent.*
import io.reactivex.functions.Function

enum class FragmentEvent {
    ATTACH, CREATE, CREATE_VIEW, START, RESUME, PAUSE, STOP, DESTROY_VIEW, DESTROY, DETACH
}

val CORRESPONDING_FRAGMENT_EVENTS = Function<FragmentEvent, FragmentEvent> {
    when(it) {
        ATTACH -> DETACH
        CREATE -> DESTROY
        CREATE_VIEW -> DESTROY_VIEW
        START -> STOP
        RESUME -> PAUSE
        PAUSE -> STOP
        STOP -> DESTROY_VIEW
        DESTROY -> DETACH
        else -> throw IllegalStateException("out of lifecycle ${it::class.java.simpleName}")
    }
}