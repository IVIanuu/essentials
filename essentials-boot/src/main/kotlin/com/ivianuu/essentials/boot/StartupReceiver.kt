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

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ivianuu.essentials.coroutines.DefaultDispatcher
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.d
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.android.ReceiverComponent
import com.ivianuu.injekt.android.createReceiverComponent
import com.ivianuu.injekt.component.ComponentElementBinding
import com.ivianuu.injekt.component.element
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class StartupReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        val component = createReceiverComponent(context, intent)
            .element<StartupReceiverComponent>()
        component.logger.d { "on system boot" }
        val scope = CoroutineScope(component.defaultDispatcher)
        component.bootListeners.forEach {
            scope.launch { it() }
        }
        component.receiverComponent.dispose()
    }
}

@ComponentElementBinding<ReceiverComponent>
@Given
class StartupReceiverComponent(
    @Given val bootListeners: Set<BootListener> = emptySet(),
    @Given val defaultDispatcher: DefaultDispatcher,
    @Given val logger: Logger,
    @Given val receiverComponent: ReceiverComponent
)
