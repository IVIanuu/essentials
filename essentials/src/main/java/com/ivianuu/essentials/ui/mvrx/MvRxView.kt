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

package com.ivianuu.essentials.ui.mvrx

import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.ivianuu.essentials.ui.viewmodel.ViewModelManagerOwner
import com.ivianuu.statestore.Consumer
import io.reactivex.disposables.Disposable

private val PENDING_INVALIDATES = HashSet<Int>()
private val HANDLER = Handler(Looper.getMainLooper(), Handler.Callback { message ->
    val view = message.obj as MvRxView
    PENDING_INVALIDATES.remove(System.identityHashCode(view))

    if (view.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
        view.invalidate()
    }

    return@Callback true
})

interface MvRxView : LifecycleOwner, ViewModelManagerOwner {

    fun invalidate()

    fun postInvalidate() {
        if (PENDING_INVALIDATES.add(System.identityHashCode(this))) {
            HANDLER.sendMessage(Message.obtain(HANDLER, System.identityHashCode(this), this))
        }
    }

    fun <S> MvRxViewModel<S>.subscribe(consumer: Consumer<S>): Disposable =
        subscribe(this@MvRxView, consumer)

}