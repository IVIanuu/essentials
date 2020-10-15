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

package com.ivianuu.essentials.gestures.action

import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.runCatching
import com.ivianuu.essentials.permission.requestPermissions
import com.ivianuu.essentials.unlock.unlockScreen
import com.ivianuu.essentials.util.DefaultDispatcher
import com.ivianuu.essentials.util.DefaultResult
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.showToast
import com.ivianuu.injekt.Assisted
import com.ivianuu.injekt.FunBinding
import kotlinx.coroutines.withContext

@FunBinding
suspend fun executeAction(
    defaultDispatcher: DefaultDispatcher,
    logger: Logger,
    getAction: getAction,
    requestPermissions: requestPermissions,
    unlockScreen: unlockScreen,
    showToast: showToast,
    key: @Assisted String,
): DefaultResult<Boolean> = withContext(defaultDispatcher) {
    runCatching {
        logger.d("execute $key")
        val action = getAction(key)

        // check permissions
        if (!requestPermissions(action.permissions)) {
            logger.d("couldn't get permissions for $key")
            return@runCatching false
        }

        // unlock screen
        if (action.unlockScreen && !unlockScreen()) {
            logger.d("couldn't unlock screen for $key")
            return@runCatching false
        }

        logger.d("fire $key")

        // fire
        action.execute()
        return@runCatching true
    }.onFailure {
        it.printStackTrace()
        showToast("Failed to execute '$key'") // todo res
    }
}
