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

package com.ivianuu.essentials.ui.traveler.anim

import com.ivianuu.director.Controller
import com.ivianuu.director.RouterTransaction
import com.ivianuu.director.common.changehandler.VerticalChangeHandler
import com.ivianuu.director.popChangeHandler
import com.ivianuu.director.pushChangeHandler
import com.ivianuu.essentials.ui.traveler.key.ControllerTransactionSetup
import com.ivianuu.traveler.Command

/**
 * Vertical controller transaction setup
 */
class VerticalControllerTransactionSetup : ControllerTransactionSetup {
    override fun setupTransaction(
        command: Command,
        currentController: Controller?,
        nextController: Controller,
        transaction: RouterTransaction
    ) {
        transaction
            .pushChangeHandler(VerticalChangeHandler())
            .popChangeHandler(VerticalChangeHandler())
    }
}