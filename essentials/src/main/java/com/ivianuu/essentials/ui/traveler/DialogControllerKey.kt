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

package com.ivianuu.essentials.ui.traveler

import com.ivianuu.conductor.Controller
import com.ivianuu.conductor.RouterTransaction
import com.ivianuu.conductor.changehandler.SimpleSwapChangeHandler
import com.ivianuu.traveler.commands.Command
import kotlin.reflect.KClass

/**
 * Dialog controller key
 */
abstract class DialogControllerKey : ControllerKey() {
    override fun setupTransaction(command: Command, transaction: RouterTransaction) {
        transaction.pushChangeHandler = SimpleSwapChangeHandler(false)
        transaction.popChangeHandler = SimpleSwapChangeHandler(false)
    }
}

/**
 * Dialog controller clazz key
 */
open class DialogControllerClassKey(clazz: KClass<out Controller>) : ControllerClassKey(clazz) {
    override fun setupTransaction(command: Command, transaction: RouterTransaction) {
        transaction.pushChangeHandler = SimpleSwapChangeHandler(false)
        transaction.popChangeHandler = SimpleSwapChangeHandler(false)
    }
}