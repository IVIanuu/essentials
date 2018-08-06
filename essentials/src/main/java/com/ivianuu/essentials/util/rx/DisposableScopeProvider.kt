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

package com.ivianuu.essentials.util.rx

import com.ivianuu.autodispose.ScopeProvider
import com.ivianuu.essentials.util.ext.publishSubject
import io.reactivex.Completable

/**
 * A scope provider which is disposable
 */
interface DisposableScopeProvider : ScopeProvider {
    fun dispose()
}

/**
 * A helper class to implement [ScopeProvider] without boilerplate
 */
class DisposableScopeProviderImpl : DisposableScopeProvider {

    private val scope = publishSubject<Unit>()

    override fun dispose() {
        scope.onNext(Unit)
    }

    override fun requestScope(): Completable =
        scope.take(1).ignoreElements()

}

fun DisposableScopeProvider() = DisposableScopeProviderImpl()