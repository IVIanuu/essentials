/*
 * Copyright 2020 Manuel Wrage
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

package com.ivianuu.essentials.boot

import android.content.Context
import android.content.Intent
import com.ivianuu.essentials.broadcast.EsBroadcastReceiver
import com.ivianuu.essentials.coroutines.DefaultDispatcher
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.d
import com.ivianuu.injekt.android.ReceiverComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class StartupReceiver : EsBroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        val component = receiverComponent.mergeComponent<StartupReceiverComponent>()
        component.logger.d { "on system boot" }
        val scope = CoroutineScope(component.defaultDispatcher)
        component.bootListeners.forEach {
            scope.launch { it() }
        }
    }
}

@MergeInto(ReceiverComponent::class)
interface StartupReceiverComponent {
    val bootListeners: BootListeners
    val defaultDispatcher: DefaultDispatcher
    val logger: Logger
}
