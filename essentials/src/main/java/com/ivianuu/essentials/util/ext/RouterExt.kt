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

package com.ivianuu.essentials.util.ext

import android.content.Intent
import com.ivianuu.essentials.ui.common.*
import com.ivianuu.essentials.ui.traveler.command.Toast
import com.ivianuu.essentials.ui.traveler.destination.ResultDestination
import com.ivianuu.traveler.ResultListener
import com.ivianuu.traveler.Router
import com.ivianuu.traveler.commands.Command
import com.ivianuu.traveler.onCommandApplied
import com.ivianuu.traveler.onCommandsApplied
import io.reactivex.Maybe
import io.reactivex.Observable

fun <T : Any> Router.results(resultCode: Int): Observable<T> = Observable.create { e ->
    val listener = object : ResultListener {
        override fun onResult(result: Any) {
            if (!e.isDisposed) {
                e.onNext(result as T)
            }
        }
    }

    e.setCancellable { removeResultListener(resultCode, listener) }

    if (!e.isDisposed) {
        addResultListener(resultCode, listener)
    }
}

fun Router.commandsApplied(): Observable<Array<out Command>> = Observable.create { e ->
    val listener = onCommandsApplied {
        if (!e.isDisposed) {
            e.onNext(it)
        }
    }

    e.setCancellable { removeNavigationListener(listener) }
}

fun Router.commandApplied(): Observable<Command> = Observable.create { e ->
    val listener = onCommandApplied {
        if (!e.isDisposed) {
            e.onNext(it)
        }
    }

    e.setCancellable { removeNavigationListener(listener) }
}

fun <T : Any> Router.navigateToForResult(destination: ResultDestination): Maybe<T> =
    results<T>(destination.resultCode)
        .take(1)
        .singleElement()
        .doOnSubscribe { navigateTo(destination) }

fun Router.navigateToForActivityResult(intent: Intent) =
    navigateToForActivityResult(RequestCodeGenerator.generate(), intent)

fun Router.navigateToForActivityResult(resultCode: Int, intent: Intent): Maybe<ActivityResult> {
    val destination = ActivityResultDestination(resultCode, intent, resultCode)
    return navigateToForResult(destination)
}

fun Router.requestPermissions(
    vararg permissions: String
) = requestPermissions(RequestCodeGenerator.generate(), *permissions)

fun Router.requestPermissions(
    resultCode: Int,
    vararg permissions: String
): Maybe<Boolean> {
    val destination = PermissionDestination(
        resultCode,
        permissions.toList().toTypedArray(),
        resultCode
    )

    return navigateToForResult<PermissionResult>(destination)
        .map { it.allGranted }
}

fun Router.showErrorMessage(message: CharSequence) {
    customCommands(Toast(Toast.TYPE_ERROR, message, 0, emptyArray()))
}

fun Router.showErrorMessage(messageRes: Int, vararg args: Any) {
    customCommands(Toast(Toast.TYPE_ERROR, null, messageRes, args))
}

fun Router.showInfoMessage(message: CharSequence) {
    customCommands(Toast(Toast.TYPE_INFO, message, 0, emptyArray()))
}

fun Router.showInfoMessage(messageRes: Int, vararg args: Any) {
    customCommands(Toast(Toast.TYPE_INFO, null, messageRes, args))
}

fun Router.showNormalMessage(message: CharSequence) {
    customCommands(Toast(Toast.TYPE_NORMAL, message, 0, emptyArray()))
}

fun Router.showNormalMessage(messageRes: Int, vararg args: Any) {
    customCommands(Toast(Toast.TYPE_NORMAL, null, messageRes, args))
}

fun Router.showSuccessMessage(message: CharSequence) {
    customCommands(Toast(Toast.TYPE_SUCCESS, message, 0, emptyArray()))
}

fun Router.showSuccessMessage(messageRes: Int, vararg args: Any) {
    customCommands(Toast(Toast.TYPE_SUCCESS, null, messageRes, args))
}

fun Router.showWarningMessage(message: CharSequence) {
    customCommands(Toast(Toast.TYPE_WARNING, message, 0, emptyArray()))
}

fun Router.showWarningMessage(messageRes: Int, vararg args: Any) {
    customCommands(Toast(Toast.TYPE_WARNING, null, messageRes, args))
}