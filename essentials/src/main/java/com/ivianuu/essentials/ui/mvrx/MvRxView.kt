package com.ivianuu.essentials.ui.mvrx

import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelStoreOwner

private val PENDING_INVALIDATES = HashSet<Int>()
private val HANDLER = Handler(Looper.getMainLooper(), Handler.Callback { message ->
    val view = message.obj as MvRxView
    PENDING_INVALIDATES.remove(System.identityHashCode(view))

    if (view.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
        view.invalidate()
    }

    true
})

interface MvRxView : LifecycleOwner, ViewModelStoreOwner {

    fun invalidate()

    fun postInvalidate() {
        if (PENDING_INVALIDATES.add(System.identityHashCode(this))) {
            HANDLER.sendMessage(Message.obtain(HANDLER, System.identityHashCode(this), this))
        }
    }

    fun <S : MvRxState> MvRxViewModel<S>.subscribe(subscriber: (S) -> Unit) =
        subscribe(this@MvRxView, subscriber)
}