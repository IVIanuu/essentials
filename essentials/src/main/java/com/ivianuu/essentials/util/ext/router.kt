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
import com.ivianuu.essentials.ui.common.ActivityResultDestination
import com.ivianuu.essentials.ui.common.PermissionDestination
import com.ivianuu.essentials.ui.traveler.destination.ResultDestination
import com.ivianuu.essentials.util.RequestCodeGenerator
import com.ivianuu.rxjavaktx.observable
import com.ivianuu.traveler.Router
import com.ivianuu.traveler.navigate
import com.ivianuu.traveler.result.ResultListener
import com.ivianuu.traveler.result.addResultListener
import com.ivianuu.traveler.result.removeResultListener
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

fun <T> Router.results(resultCode: Int) = observable<T> { e ->
    @Suppress("UNCHECKED_CAST")
    val listener = object : ResultListener {
        override fun invoke(result: Any) {
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

@Suppress("UNCHECKED_CAST")
suspend fun <D : ResultDestination<R>, R> Router.navigateForResult(destination: D) =
    suspendCancellableCoroutine<R> { continuation ->
        val listener = object : ResultListener {
            override fun invoke(result: Any) {
                continuation.resume(result as R)
                removeResultListener(destination.resultCode, this)
            }
        }

        continuation.invokeOnCancellation {
            removeResultListener(destination.resultCode, listener)
        }

        addResultListener(destination.resultCode, listener)

        navigate(destination)
    }

suspend fun Router.navigateForActivityResult(intent: Intent) =
    navigateForActivityResult(RequestCodeGenerator.generate(), intent)

suspend fun Router.navigateForActivityResult(resultCode: Int, intent: Intent) =
    navigateForResult(ActivityResultDestination(resultCode, intent, resultCode))

suspend fun Router.requestPermissions(
    vararg permissions: String
) = requestPermissions(RequestCodeGenerator.generate(), *permissions)

suspend fun Router.requestPermissions(
    resultCode: Int,
    vararg permissions: String
): Boolean {
    val destination = PermissionDestination(
        resultCode,
        permissions.toList().toTypedArray(),
        resultCode
    )

    return navigateForResult(destination).allGranted
}