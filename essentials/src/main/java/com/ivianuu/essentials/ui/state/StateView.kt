package com.ivianuu.essentials.ui.state

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.ViewModelStoreOwner
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider

private val PENDING_INVALIDATES = HashSet<Int>()
private val HANDLER = Handler(Looper.getMainLooper(), Handler.Callback { message ->
    val view = message.obj as StateView
    PENDING_INVALIDATES.remove(System.identityHashCode(view))

    if (view.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
        view.invalidate()
    }

    true
})

interface StateView : LifecycleOwner, ViewModelStoreOwner {

    fun invalidate()

    fun postInvalidate() {
        if (PENDING_INVALIDATES.add(System.identityHashCode(this))) {
            HANDLER.sendMessage(Message.obtain(HANDLER, System.identityHashCode(this), this))
        }
    }

    fun <S : Any> StateViewModel<S>.subscribe(subscriber: (S) -> Unit) =
        subscribe(AndroidLifecycleScopeProvider.from(lifecycle), subscriber)
}