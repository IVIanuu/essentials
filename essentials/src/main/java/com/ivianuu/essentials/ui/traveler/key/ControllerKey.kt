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

package com.ivianuu.essentials.ui.traveler.key

import android.os.Parcelable
import com.ivianuu.director.Controller
import com.ivianuu.director.RouterTransaction
import com.ivianuu.director.traveler.ControllerKey
import com.ivianuu.essentials.util.ext.unsafeLazy
import com.ivianuu.traveler.Command
import kotlin.reflect.KClass

abstract class ControllerKey(
    val target: KClass<out Controller>,
    open val setup: Setup? = null
) : ControllerKey, Parcelable {

    override fun createController(data: Any?): Controller = target.java.newInstance().apply {
        args.apply { putParcelable(KEY_KEY, this@ControllerKey) }
    }

    override fun setupTransaction(
        command: Command,
        currentController: Controller?,
        nextController: Controller,
        transaction: RouterTransaction
    ) {
        setup?.apply(command, currentController, nextController, transaction)
    }

    interface Setup {
        fun apply(
            command: Command,
            currentController: Controller?,
            nextController: Controller,
            transaction: RouterTransaction
        )
    }
}

fun <T : Parcelable> Controller.key(): T = args.getParcelable(KEY_KEY)!!

fun <T : Parcelable> Controller.keyOrNull() = try {
    key<T>()
} catch (e: Exception) {
    null
}

fun <T : Parcelable> Controller.bindKey() = unsafeLazy { key<T>() }