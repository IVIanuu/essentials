/*
 * Copyright 2019 Manuel Wrage
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

package com.ivianuu.essentials.activityresult

import android.content.Intent
import android.os.Bundle
import com.ivianuu.essentials.ui.coroutines.launch
import com.ivianuu.essentials.ui.injekt.inject
import com.ivianuu.essentials.ui.navigation.NavigatorAmbient
import com.ivianuu.essentials.ui.navigation.Route

fun ActivityResultRoute(
    intent: Intent,
    options: Bundle? = null
) = Route(opaque = true) {
    val activityResultController = inject<ActivityResultController>()
    val navigator = NavigatorAmbient.current
    launch {
        val result = activityResultController.startForResult(intent, options)
        navigator.popTop(result = result)
    }
}
