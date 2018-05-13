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

import android.os.Parcelable
import com.ivianuu.conductor.Controller
import com.ivianuu.conductor.RouterTransaction
import com.ivianuu.traveler.commands.Command
import kotlin.reflect.KClass

/**
 * Key for [Controller]'s
 */
abstract class ControllerKey {

    fun newInstance(data: Any? = null) = createController(data).apply {
        if (this@ControllerKey is Parcelable) {
            args.putParcelable(KEY_KEY, this@ControllerKey)
        }
    }

    protected abstract fun createController(data: Any?): Controller

    open fun setupTransaction(command: Command, transaction: RouterTransaction) {
    }

    companion object {
        private const val KEY_KEY = "key"

        fun <T> get(controller: Controller): T? where T : ControllerKey, T : Parcelable =
            controller.args.getParcelable(KEY_KEY) as T?
    }
}

/**
 * Auto creates the controller by using its clazz new instance
 */
open class ControllerClassKey(val clazz: KClass<out Controller>) : ControllerKey() {
    override fun createController(data: Any?) = clazz.java.newInstance()
}

fun <T> Controller.key() where T : ControllerKey, T : Parcelable =
    ControllerKey.get<T>(this)

fun <T> Controller.requireKey() where T : ControllerKey, T : Parcelable =
    key<T>() ?: throw IllegalStateException("missing key")