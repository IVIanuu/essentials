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

import com.ivianuu.essentials.ui.traveler.destination.ResultDestination
import com.ivianuu.traveler.ResultListener
import com.ivianuu.traveler.Router
import com.ivianuu.traveler.commands.Command
import com.ivianuu.traveler.onCommandApplied
import com.ivianuu.traveler.onCommandsApplied
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

fun <T : Any> Router.navigateToForResult(destination: ResultDestination): Observable<T> =
    results<T>(destination.resultCode)
        .doOnSubscribe { navigateTo(destination) }