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

import android.content.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.android.*
import com.ivianuu.injekt.scope.*

class StartupReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        val component = createReceiverGivenScope(context, intent)
            .element<StartupReceiverComponent>()
        component.logger.d { "on system boot" }
        component.bootListeners.forEach { it() }
        component.receiverGivenScope.dispose()
    }
}

@InstallElement<ReceiverGivenScope>
@Given
class StartupReceiverComponent(
    @Given val bootListeners: Set<BootListener> = emptySet(),
    @Given val logger: Logger,
    @Given val receiverGivenScope: ReceiverGivenScope,
)
